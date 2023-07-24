package com.example.baseproject.models

data class Message(val id: String, val sendId: String, val toId: String, val sendAt: Long, val type: String, val content: String)
