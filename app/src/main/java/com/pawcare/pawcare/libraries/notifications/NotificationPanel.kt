package com.pawcare.pawcare.libraries.notifications

import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context

import android.media.RingtoneManager
import android.os.Build
import androidx.core.app.NotificationCompat
import com.pawcare.pawcare.R

class NotificationPanel private constructor(parent: Context) {

    private val nManager: NotificationManager = parent.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager

    constructor(parent: Context, title: String?, body: String?, contentIntent: PendingIntent, channel: String) : this(parent) {
        val CHANNEL_ID = "my_channel_01"

        val soundUri = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION)
        val mBuilder = NotificationCompat.Builder(parent, CHANNEL_ID)
            .setSmallIcon(getNotificationIcon())
            .setContentTitle(title)
            .setStyle(NotificationCompat.BigTextStyle()
                .bigText(body))
            .setColor(parent.resources.getColor(R.color.text_color))
            .setVibrate(longArrayOf(500, 500, 500))
            .setSound(soundUri)
            .setOnlyAlertOnce(true)
            .setAutoCancel(true)
            .setContentIntent(contentIntent)

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val channelS = NotificationChannel(CHANNEL_ID, channel, NotificationManager.IMPORTANCE_DEFAULT)
            nManager.createNotificationChannel(channelS)
        }

        nManager.notify((System.currentTimeMillis() / 1000).toInt(), mBuilder.build())

    }

    private fun getNotificationIcon(): Int {
        val whiteIcon = android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.LOLLIPOP
        return if (whiteIcon) R.drawable.ic_stat_name else R.mipmap.ic_launcher
    }

}