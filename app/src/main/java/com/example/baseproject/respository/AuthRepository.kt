package com.example.baseproject.respository

import com.example.baseproject.models.User
import com.example.baseproject.utils.UIState

interface AuthRepository {
    fun loginUser(user: User, result: (UIState<String>) -> Unit)
    fun signupUser(email: String, password: String, user: User, result: (UIState<String>) -> Unit)
    fun updateUserInfor(user: User, result: (UIState<String>) -> Unit)
}