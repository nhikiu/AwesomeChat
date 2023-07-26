package com.example.baseproject.ui.friends.allFriend

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.example.baseproject.models.Friend
import com.example.baseproject.models.User
import com.example.baseproject.respository.AuthRepository
import com.example.core.base.BaseViewModel
import com.google.firebase.auth.FirebaseAuth
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

@HiltViewModel
class AllFriendViewModel @Inject constructor(
    private val repository: AuthRepository
) : BaseViewModel(){

    private val friendList = arrayListOf<Friend>()

    private var _friendListLiveData: MutableLiveData<List<Friend>> = MutableLiveData()
    val friendListLiveData: LiveData<List<Friend>> get() = _friendListLiveData

    private val _user:MutableLiveData<User> = MutableLiveData()
    val user: LiveData<User> get() = _user

    init {
        _friendListLiveData.postValue(friendList)
    }

    fun getAllUser() {
        repository.getAllUser(_friendListLiveData)
    }

    fun getUserById() {
        repository.getUserById(FirebaseAuth.getInstance().currentUser!!.uid, _user)
    }

    fun updateFriendState(newFriend: Friend) {
        repository.updateFriendState(newFriend)
    }

    fun getAllFriend() {
        repository.getAllFriend(_friendListLiveData)
    }
}