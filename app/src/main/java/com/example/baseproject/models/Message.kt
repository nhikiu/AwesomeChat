package com.example.baseproject.models

data class Message(val id: String, val sendId: String, val toId: String, val sendAt: String, val type: String, val content: String, var position: String)
