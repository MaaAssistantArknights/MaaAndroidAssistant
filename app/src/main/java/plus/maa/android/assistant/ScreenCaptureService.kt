package plus.maa.android.assistant

import android.annotation.SuppressLint
import android.app.Service
import android.content.Context
import android.content.Intent
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.PixelFormat
import android.hardware.display.DisplayManager
import android.hardware.display.VirtualDisplay
import android.media.ImageReader
import android.media.projection.MediaProjection
import android.media.projection.MediaProjectionManager
import android.os.Binder
import android.os.Handler
import android.os.HandlerThread
import android.os.IBinder
import android.util.Log
import androidx.core.app.NotificationCompat
import plus.maa.android.assistant.notification.channel.SCREEN_CAPTURE_CHANNEL_ID
import plus.maa.android.assistant.notification.channel.SCREEN_CAPTURE_FOREGROUND_ID
import plus.maa.android.assistant.support.getMediaProjection
import plus.maa.android.assistant.support.getScreenSize

class ScreenCaptureService : Service() {

    private var mediaProjection: MediaProjection? = null

    private var imageReader: ImageReader? = null

    private var virtualDisplay: VirtualDisplay? = null

    private val vdHandlerThread = HandlerThread("VirtualDisplay")

    private val vdHandler by lazy {
        Handler(vdHandlerThread.looper)
    }

    private val irHandlerThread = HandlerThread("ImageRead")

    private val irHandler by lazy {
        Handler(irHandlerThread.looper)
    }

    inner class Binder : android.os.Binder() {
        fun captureScreen(callback: (width: Int, height: Int, byteArray: ByteArray) -> Unit) {
            irHandler.post {
                val image = imageReader?.acquireLatestImage() ?: return@post

                val width = image.width
                val height = image.height
                val planes = image.planes
                val buffer = planes[0].buffer
                val pixelStride = planes[0].pixelStride
                val rowStride = planes[0].rowStride
                val rowPadding = rowStride - pixelStride * width

                val byteArray = ByteArray(buffer.remaining())
                buffer.get(byteArray, 0, byteArray.size)

                callback(width + rowPadding / pixelStride, height, byteArray)

                image.close()
            }
        }
    }

    override fun onBind(intent: Intent): IBinder {
        return Binder()
    }

    override fun onCreate() {
        super.onCreate()

        val notification = NotificationCompat.Builder(this, SCREEN_CAPTURE_CHANNEL_ID).build()
        startForeground(SCREEN_CAPTURE_FOREGROUND_ID, notification)
    }

    @SuppressLint("WrongConstant")
    override fun onStartCommand(intent: Intent, flags: Int, startId: Int): Int {
        if (mediaProjection == null) {
            mediaProjection = getMediaProjection(intent)
        }
        val mp = mediaProjection!!

        val screenSize = getScreenSize()

        imageReader = ImageReader.newInstance(
            screenSize.width(),
            screenSize.height(),
            PixelFormat.RGBA_8888,
            2
        )

        vdHandlerThread.start()
        irHandlerThread.start()

        virtualDisplay = mp.createVirtualDisplay(
            "ScreenCapture",
            screenSize.width(),
            screenSize.height(),
            resources.displayMetrics.densityDpi,
            DisplayManager.VIRTUAL_DISPLAY_FLAG_AUTO_MIRROR,
            imageReader!!.surface,
            null,
            vdHandler
        )

        return super.onStartCommand(intent, flags, startId)
    }

    override fun onDestroy() {
        super.onDestroy()

        virtualDisplay?.release()
        vdHandlerThread.quit()
        irHandlerThread.quit()
    }
}