package com.example.baseproject.respository

import com.example.baseproject.models.User
import com.example.baseproject.utils.UIState

interface AuthRepository {
    fun loginUser(email: String, password: String, result: (UIState<String>) -> Unit)
    fun signupUser(email: String, password: String, user: User, result: (UIState<String>) -> Unit)
    fun updateUserInfor(user: User, result: (UIState<String>) -> Unit)

    fun signoutUser(result: (UIState<String>) -> Unit)
}