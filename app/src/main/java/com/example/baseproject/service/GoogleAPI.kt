package com.example.baseproject.service

import com.example.baseproject.models.NotificationResponse
import com.example.baseproject.ui.friends.FcmNotificationData
import retrofit2.Call
import retrofit2.http.Body
import retrofit2.http.Headers
import retrofit2.http.POST

interface GoogleAPI {
    @Headers(
        "Authorization: key=AAAAKwCSYeo:APA91bG7BNMHBrBQMmNoY_ciXHld0fuS0pCo0KUybRnDNgubRC8bbAOa-JyhVRdUec2yn2Oi27u-pVydEhxLOh4P99tRlqaf0cbhKR1NbJmkK6swyyOWMPlTjie51Dm-dV98azYptfO3",
        "Content-Type: application/json"
    )
    @POST("fcm/send")
    fun pushNotification(@Body notification: FcmNotificationData) : Call<NotificationResponse>
}