package plus.maa.android.assistant

import android.content.ComponentName
import android.content.ServiceConnection
import android.graphics.BitmapFactory
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.IBinder
import android.util.Log
import plus.maa.android.assistant.databinding.ActivityMainBinding
import plus.maa.android.assistant.support.createBMP
import plus.maa.android.assistant.support.createScreenCaptureLauncher
import plus.maa.android.assistant.support.launchScreenCapture

class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding

    private var scsBinder: ScreenCaptureService.Binder? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val screenCaptureLauncher = createScreenCaptureLauncher(object : ServiceConnection {
            override fun onServiceConnected(name: ComponentName, service: IBinder) {
                scsBinder = service as ScreenCaptureService.Binder

                scsBinder?.captureScreen { width, height, byteArray ->
                    val bmp = createBMP(width, height, byteArray)
                    val bitmap = BitmapFactory.decodeByteArray(bmp, 0, bmp.size)

                    Log.e("", "")
                }
            }

            override fun onServiceDisconnected(name: ComponentName) {
            }
        })

        binding.btnScreenCapture.setOnClickListener {
            launchScreenCapture(screenCaptureLauncher)
        }
    }

    /**
     * A native method that is implemented by the 'assistant' native library,
     * which is packaged with this application.
     */
    external fun stringFromJNI(): String

    companion object {
        // Used to load the 'assistant' library on application startup.
        init {
            System.loadLibrary("assistant")
        }
    }
}