package plus.maa.android.assistant

import android.app.Application
import plus.maa.android.assistant.notification.channel.registerChannels

class MainApplication : Application() {

    override fun onCreate() {
        super.onCreate()

        registerChannels(applicationContext)
    }
}