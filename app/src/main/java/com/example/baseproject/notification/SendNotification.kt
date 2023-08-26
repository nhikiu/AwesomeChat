package com.example.baseproject.notification

import android.os.Build
import android.util.Log
import androidx.annotation.RequiresApi
import com.example.baseproject.models.NotificationResponse
import com.example.baseproject.ui.friends.DataModel
import com.example.baseproject.ui.friends.FcmNotification
import com.example.baseproject.ui.friends.FcmNotificationData
import com.example.baseproject.utils.Constants
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

object SendNotification {
    @RequiresApi(Build.VERSION_CODES.O)
    fun sendNotification(token: String, notification: FcmNotification, data: DataModel) {
        val retrofit = Retrofit.Builder().baseUrl(Constants.GOOGLE_API)
            .addConverterFactory(GsonConverterFactory.create())
            .build()
        val apiService = retrofit.create(GoogleAPI::class.java)

        val notificationData = FcmNotificationData(
            to = token,
            notification = notification,
            data = data
        )

        val call = apiService.pushNotification(notification = notificationData)
        call.enqueue(object : Callback<NotificationResponse> {
            override fun onResponse(call: Call<NotificationResponse>, response: Response<NotificationResponse>) {
                Log.e("abc", "onResponse: $response", )
                if (response.isSuccessful) {
                    val responseBody = response.body()
                    Log.d("abc", "onResponse body: ${responseBody}")
                } else {
                    val errorBody = response.errorBody()
                    Log.e("abc", "onResponse error body: ${errorBody}")
                }
            }

            override fun onFailure(call: Call<NotificationResponse>, t: Throwable) {
                Log.e("abc", "Send Notification: onFailure: ${t.message}", )
            }
        })
    }
}