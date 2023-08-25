package com.example.baseproject.models

data class NotificationResponse(
    val multicast_id: Long,
    val success: Int,
    val failure: Int,
    val canonical_ids: Int,
    val results: List<FcmResult>
)

data class FcmResult(
    val message_id: String
)