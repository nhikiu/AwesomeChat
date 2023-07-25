package com.example.baseproject.ui.friends.realFriend

import androidx.lifecycle.MutableLiveData
import com.example.baseproject.models.User
import com.example.core.base.BaseViewModel
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

@HiltViewModel
class RealFriendViewModel @Inject constructor() : BaseViewModel(){
    private val realFriendList = MutableLiveData<List<User>>()
}