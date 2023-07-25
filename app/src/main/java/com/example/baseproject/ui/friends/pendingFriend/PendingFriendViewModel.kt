package com.example.baseproject.ui.friends.pendingFriend

import androidx.lifecycle.MutableLiveData
import com.example.core.base.BaseViewModel
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

@HiltViewModel
class PendingFriendViewModel @Inject constructor() : BaseViewModel(){
    private val pendingFriendList = MutableLiveData<List<com.example.baseproject.models.User>>()
}