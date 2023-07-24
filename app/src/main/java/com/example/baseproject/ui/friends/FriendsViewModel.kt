package com.example.baseproject.ui.friends

import androidx.lifecycle.MutableLiveData
import com.example.baseproject.models.User
import com.example.core.base.BaseViewModel
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

@HiltViewModel
class FriendsViewModel @Inject constructor() : BaseViewModel(){
    private val friendList = MutableLiveData<List<User>>()
}