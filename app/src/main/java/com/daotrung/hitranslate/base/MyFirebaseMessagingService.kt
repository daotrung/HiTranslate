package com.daotrung.hitranslate.base

import android.app.Notification
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.os.Build
import androidx.core.app.NotificationCompat
import androidx.core.content.ContextCompat
import com.daotrung.hitranslate.App
import com.daotrung.hitranslate.R
import com.daotrung.hitranslate.activity.MainActivity
import com.daotrung.hitranslate.base.Params.ON_OFF_NOTIFICATION
import com.daotrung.hitranslate.utils.LogInstance
import com.google.firebase.messaging.FirebaseMessagingService
import com.google.firebase.messaging.RemoteMessage

class MyFirebaseMessagingService : FirebaseMessagingService() {
    override fun onNewToken(token: String) {
        LogInstance.e(token)
        refreshToken(token)
        super.onNewToken(token)
    }

    private fun refreshToken(token: String) {
        LogInstance.e("refreshToken $token")
    }

    override fun onMessageReceived(message: RemoteMessage) {
        if (App.dataPer.getBoolean(ON_OFF_NOTIFICATION, true)) {
            message.let {
                if (it.notification != null) {
                    val data = it.notification
                    createNotificationFirebase(data!!)
                }
            }
        }
    }

    private fun createNotificationFirebase(message: RemoteMessage.Notification) {
        val intent = Intent(this, MainActivity::class.java)
        intent.action = "Notification"
        intent.putExtra(MyFirebaseMessagingService::class.java.canonicalName, true)
        val notificationID = 123
        intent.addFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP)
        val pendingIntent = PendingIntent.getActivity(
            this, 0, intent, PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )
        val channelId = this.applicationContext.packageName
        val notificationBuilder = NotificationCompat.Builder(this, channelId)
            .setSmallIcon(R.mipmap.ic_launcher)
            .setColor(ContextCompat.getColor(this, R.color.purple_200))
            .setContentTitle(if (message.title.isNullOrEmpty()) "New Message" else message.title)
            .setContentText(message.body)
            .setDefaults(Notification.DEFAULT_ALL)
            .setPriority(NotificationCompat.PRIORITY_HIGH)
            .setAutoCancel(true)
            .setNumber(1)
            .setContentIntent(pendingIntent)
        val notificationManager =
            this.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val chanel = NotificationChannel(
                channelId,
                "New Message",
                NotificationManager.IMPORTANCE_HIGH
            )
            notificationManager.createNotificationChannel(chanel)
        }
        notificationManager.notify(notificationID, notificationBuilder.build())
    }

}