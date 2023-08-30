package com.example.baseproject.notification

import com.example.baseproject.models.NotificationResponse
import com.example.baseproject.ui.friends.DataModel
import com.example.baseproject.ui.friends.FcmNotification
import com.example.baseproject.ui.friends.FcmNotificationData
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import timber.log.Timber

object SendNotification {
    fun sendNotification(token: String, notification: FcmNotification, data: DataModel, apiService: GoogleAPI) {
        val notificationData = FcmNotificationData(
            to = token,
            notification = notification,
            data = data
        )

        val call = apiService.pushNotification(notification = notificationData)
        call.enqueue(object : Callback<NotificationResponse> {
            override fun onResponse(call: Call<NotificationResponse>, response: Response<NotificationResponse>) {
                if (response.isSuccessful) {
                    val responseBody = response.body()
                    Timber.tag("abc").d("onResponse body: %s", responseBody)
                } else {
                    val errorBody = response.errorBody()
                    Timber.tag("abc").e("onResponse error body: %s", errorBody)
                }
            }

            override fun onFailure(call: Call<NotificationResponse>, t: Throwable) {
                Timber.tag("abc").e("Send Notification: onFailure: %s", t.message)
            }
        })
    }
}