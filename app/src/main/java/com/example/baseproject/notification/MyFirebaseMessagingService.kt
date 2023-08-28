package com.example.baseproject.notification

import android.annotation.SuppressLint
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.os.Build
import android.os.Bundle
import android.util.Log
import androidx.core.app.NotificationCompat
import com.example.baseproject.R
import com.example.baseproject.container.MainActivity
import com.example.baseproject.utils.Constants
import com.google.firebase.messaging.FirebaseMessagingService
import com.google.firebase.messaging.RemoteMessage

@SuppressLint("MissingFirebaseInstanceTokenRefresh")
class MyFirebaseMessagingService : FirebaseMessagingService() {
    private val channelId = Constants.CHANNEL_ID

    override fun onMessageReceived(message: RemoteMessage) {
        super.onMessageReceived(message)

        val title = message.notification?.title
        val body = message.notification?.body
        val uid = message.data[Constants.FROM_ID_USER]
        Log.e("abc", "notification: ${message.data}", )

        val intent = Intent(this, MainActivity::class.java)
        intent.addFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP or Intent.FLAG_ACTIVITY_CLEAR_TOP)
        val bundle = Bundle()
        bundle.putString(Constants.FROM_ID_USER, uid)
        bundle.putString(Constants.MESSAGE_TYPE, message.data[Constants.MESSAGE_TYPE])
        intent.putExtra("data",bundle)
        Log.e("abc", "onMessageReceived: ${intent.getBundleExtra("data")?.getString(Constants.FROM_ID_USER)}")
        val pendingIntent = PendingIntent.getActivity(
            this, 0, intent,
            PendingIntent.FLAG_UPDATE_CURRENT
        )

        val notificationBuilder = NotificationCompat.Builder(this, channelId)
            .setContentTitle(title)
            .setContentText(body)
            .setAutoCancel(true)
            .setSmallIcon(R.drawable.ic_mail_common)
            .setContentIntent(pendingIntent)

        val notificationManager = getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val channel = NotificationChannel(channelId, Constants.CHANNEL_NAME, NotificationManager.IMPORTANCE_HIGH)
            channel.name = Constants.CHANNEL_NAME
            channel.enableLights(true)
            notificationManager.createNotificationChannel(channel)
        }

        val notificationId = 0
        notificationManager.notify(notificationId, notificationBuilder.build())
    }
}