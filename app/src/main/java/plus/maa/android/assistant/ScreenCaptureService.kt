package plus.maa.android.assistant

import android.annotation.SuppressLint
import android.app.Service
import android.content.Intent
import android.graphics.Bitmap
import android.graphics.PixelFormat
import android.hardware.display.DisplayManager
import android.hardware.display.VirtualDisplay
import android.media.ImageReader
import android.media.projection.MediaProjection
import android.os.Handler
import android.os.HandlerThread
import android.os.IBinder
import androidx.core.app.NotificationCompat
import plus.maa.android.assistant.notification.channel.SCREEN_CAPTURE_CHANNEL_ID
import plus.maa.android.assistant.notification.channel.SCREEN_CAPTURE_FOREGROUND_ID
import plus.maa.android.assistant.support.getMediaProjection
import plus.maa.android.assistant.support.getScreenSize
import java.nio.ByteBuffer
import kotlin.math.min

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

    inner class Controller : android.os.Binder() {
        fun captureScreen(callback: (width: Int, height: Int, byteArray: ByteArray) -> Unit) {
            irHandler.post {
                val image = imageReader?.acquireLatestImage()

                if (image == null) {
                    callback(0, 0, ByteArray(0))
                    return@post
                }

                val planes = image.planes
                val buffer = planes[0].buffer

                val byteArray = ByteArray(buffer.remaining())
                buffer.get(byteArray, 0, byteArray.size)

                val height = image.height
                val width = byteArray.size / height / 4

//                val pixelStride = planes[0].pixelStride
//                val rowStride = planes[0].rowStride
//                val rowPadding = rowStride - pixelStride * width

                callback(width, height, byteArray)

                image.close()
            }
        }
    }

    override fun onBind(intent: Intent): IBinder {
        return Controller()
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

        val minSize = min(screenSize.width(), screenSize.height())
        val proportion = if (minSize > 720) {
            720.toDouble() / minSize
        } else {
            1.0
        }

        val width = (proportion * screenSize.width()).toInt()
        val height = (proportion * screenSize.height()).toInt()

        imageReader = ImageReader.newInstance(
            width,
            height,
            PixelFormat.RGBA_8888,
            2
        )

        vdHandlerThread.start()
        irHandlerThread.start()

        virtualDisplay = mp.createVirtualDisplay(
            "ScreenCapture",
            width,
            height,
            resources.displayMetrics.densityDpi,
            DisplayManager.VIRTUAL_DISPLAY_FLAG_AUTO_MIRROR,
            imageReader!!.surface,
            null,
            vdHandler
        )

        imageReader?.setOnImageAvailableListener({ reader ->
            val image = reader.acquireLatestImage()

            val planes = image.planes
            val buffer = planes[0].buffer

            val byteArray = ByteArray(buffer.remaining())
            buffer.get(byteArray, 0, byteArray.size)

            val h = image.height
            val w = byteArray.size / h / 4

            val bitmap = Bitmap.createBitmap(w, h, Bitmap.Config.ARGB_8888)
            bitmap.copyPixelsFromBuffer(ByteBuffer.wrap(byteArray))

            image.close()
        }, null)

        return super.onStartCommand(intent, flags, startId)
    }

    override fun onDestroy() {
        super.onDestroy()

        virtualDisplay?.release()
        vdHandlerThread.quit()
        irHandlerThread.quit()
    }
}