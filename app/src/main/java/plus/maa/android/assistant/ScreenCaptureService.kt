package plus.maa.android.assistant

import android.app.Service
import android.content.Intent
import android.os.IBinder

class ScreenCaptureService : Service() {

    override fun onCreate() {
        super.onCreate()
    }

    override fun onBind(intent: Intent): IBinder {
        TODO("Return the communication channel to the service.")
    }
}