package com.example.baseproject.models

data class Chat(val id: String, val friendProfile: User, val unread: Int, val messages: List<Message>?)
