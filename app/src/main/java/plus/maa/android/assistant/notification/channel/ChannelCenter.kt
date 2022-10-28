package plus.maa.android.assistant.notification.channel

import android.content.Context
import androidx.core.app.NotificationChannelCompat
import androidx.core.app.NotificationManagerCompat

const val SCREEN_CAPTURE_FOREGROUND_ID = 1
const val SCREEN_CAPTURE_CHANNEL_ID = "channel_screen_capture"
const val SCREEN_CAPTURE_CHANNEL_NAME = "屏幕捕获"

fun registerChannels(context: Context) {
    val notificationManager = NotificationManagerCompat.from(context)
    val channel = NotificationChannelCompat.Builder(
        SCREEN_CAPTURE_CHANNEL_ID,
        NotificationManagerCompat.IMPORTANCE_DEFAULT
    )
        .setName(SCREEN_CAPTURE_CHANNEL_NAME)
        .build()

    notificationManager.createNotificationChannel(channel)
}