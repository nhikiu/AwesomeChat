package com.example.baseproject.models

data class Chat(val id: String, val friendProfile: User, val messages: List<Message>?)
