package plus.maa.android.assistant.support

import android.app.Activity
import android.app.Service
import android.content.Context
import android.content.Intent
import android.content.ServiceConnection
import android.media.projection.MediaProjection
import android.media.projection.MediaProjectionManager
import androidx.activity.ComponentActivity
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import plus.maa.android.assistant.ScreenCaptureService

const val SCREEN_CAPTURE_KEY_RESULT_CODE = "key_result_code"
const val SCREEN_CAPTURE_KEY_INTENT = "key_intent"

fun ComponentActivity.createScreenCaptureLauncher(conn: ServiceConnection): ActivityResultLauncher<Intent> {
    return registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
        val intent = Intent(this, ScreenCaptureService::class.java).apply {
            putExtra(SCREEN_CAPTURE_KEY_RESULT_CODE, result.resultCode)
            putExtra(SCREEN_CAPTURE_KEY_INTENT, result.data)
        }
        startService(intent)
        bindService(intent, conn, Context.BIND_AUTO_CREATE)
    }
}

fun Context.launchScreenCapture(launcher: ActivityResultLauncher<Intent>) {
    val mediaProjectionManager =
        getSystemService(Context.MEDIA_PROJECTION_SERVICE) as MediaProjectionManager
    val screenCaptureIntent = mediaProjectionManager.createScreenCaptureIntent()

    launcher.launch(screenCaptureIntent)
}

fun Service.getMediaProjection(intent: Intent): MediaProjection {
    val mediaProjectionManager =
        getSystemService(Context.MEDIA_PROJECTION_SERVICE) as MediaProjectionManager

    val resultCode = intent.getIntExtra(SCREEN_CAPTURE_KEY_RESULT_CODE, Activity.RESULT_CANCELED)
    val data = intent.getParcelableExtra<Intent>(SCREEN_CAPTURE_KEY_INTENT)!!

    return mediaProjectionManager.getMediaProjection(resultCode, data)
}