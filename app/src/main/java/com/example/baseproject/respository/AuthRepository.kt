package com.example.baseproject.respository

import androidx.lifecycle.MutableLiveData
import com.example.baseproject.models.Friend
import com.example.baseproject.models.User
import com.example.baseproject.utils.UIState

interface AuthRepository {
    fun loginUser(email: String, password: String, result: (UIState<String>) -> Unit)
    fun signupUser(email: String, password: String, user: User, result: (UIState<String>) -> Unit)
    fun updateUserInfor(user: User, result: (UIState<String>) -> Unit)

    fun signoutUser(result: (UIState<String>) -> Unit)

    fun getAllUser(liveData: MutableLiveData<List<Friend>>)

    fun getUserById(id: String, liveData: MutableLiveData<User>)

    fun updateFriendState(newFriend: Friend)

    fun getAllFriend(liveData: MutableLiveData<List<Friend>>)
}