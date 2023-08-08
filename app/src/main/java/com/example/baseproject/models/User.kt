package com.example.baseproject.models

data class User(
    var id: String = "",
    val name:String = "",
    val email: String = "",
    val phoneNumber: String = "",
    val dateOfBirth: String,
    val avatar: String?,)