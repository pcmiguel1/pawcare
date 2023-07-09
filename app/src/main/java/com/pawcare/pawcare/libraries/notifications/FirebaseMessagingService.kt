package com.pawcare.pawcare.libraries.notifications

import android.app.PendingIntent
import android.util.Log
import com.google.firebase.messaging.RemoteMessage
import com.pawcare.pawcare.App
import com.pawcare.pawcare.R

const val NOTIFICATION_INFORMATIVE = 0
const val NOTIFICATION_URL = 1

class FirebaseMessagingService : com.google.firebase.messaging.FirebaseMessagingService() {

    private enum class NotificationParser {
        title, body, subtitle, type, theme, url, notificationId
    }

    override fun onNewToken(token: String) {
        super.onNewToken(token)

        sendRegistrationToServer(token)
        Log.d("notificationToken", token)

    }

    private fun sendRegistrationToServer(token: String) {
        App.instance.backOffice.postNotificationToken(token)
    }

    override fun onMessageReceived(remoteMessage: RemoteMessage) {

        val notification = remoteMessage.notification
        val data = remoteMessage.data

        Log.d("notificationadafa", notification.toString() + " " + notification!!.title.toString() + " " + data.toString())

        val title = notification?.title
        val body = notification?.body
        val notificationId = if (data.containsKey(NotificationParser.notificationId.toString())) data[NotificationParser.notificationId.toString()]!! else ""

        buildInformativeNotification(notificationId, title, body)
    }

    private fun buildInformativeNotification(notificationId: String, title: String?, body: String?) {
        val requestId = System.currentTimeMillis().toInt()
        val intent = applicationContext.packageManager.getLaunchIntentForPackage(applicationContext.packageName)

        val pendingIntent = if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.S)
            PendingIntent.FLAG_IMMUTABLE or PendingIntent.FLAG_UPDATE_CURRENT
        else
            PendingIntent.FLAG_UPDATE_CURRENT

        val contentIntent = PendingIntent.getActivity(this@FirebaseMessagingService,
        requestId, intent, pendingIntent)

        NotificationPanel(this, title, body, contentIntent, getString(R.string.notifications))

    }

}