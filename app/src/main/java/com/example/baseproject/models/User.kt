package com.example.baseproject.models

import java.util.Date

data class User(
    var id: String = "",
    val name:String = "",
    val email: String = "",
    val phoneNumber: String = "",
    val dateOfBirth: Date?,
    val avatar: String?,)
