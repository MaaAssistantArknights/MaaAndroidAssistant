package plus.maa.android.assistant

import android.content.ComponentName
import android.content.ServiceConnection
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.Handler
import android.os.IBinder
import android.os.Looper
import android.util.Log
import android.widget.Toast
import plus.maa.android.assistant.databinding.ActivityMainBinding
import plus.maa.android.assistant.server.MaaServer
import plus.maa.android.assistant.server.task.MaaServerCallback
import plus.maa.android.assistant.support.createScreenCaptureLauncher
import plus.maa.android.assistant.support.launchScreenCapture

class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding

    private val maaServer = MaaServer().apply {
        setCallback(object : MaaServerCallback {
            override fun onServerStartSuccess() {
                Toast.makeText(this@MainActivity, "MaaServer start success", Toast.LENGTH_LONG).show()
                Log.i("MaaServer", "MaaServer start success")
            }

            override fun onServerStartFailed(e: Exception) {
                Toast.makeText(this@MainActivity, "MaaServer start failed, cause: $e", Toast.LENGTH_LONG).show()
                Log.i("MaaServer", "MaaServer start failed, cause: $e")
            }
        })
    }

    private var scsController: ScreenCaptureService.Controller? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val screenCaptureLauncher = createScreenCaptureLauncher(object : ServiceConnection {
            override fun onServiceConnected(name: ComponentName, service: IBinder) {
                scsController = service as ScreenCaptureService.Controller
                MaaServer.setScreenCaptureController(scsController!!)

                maaServer.start(8082)
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