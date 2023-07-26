package com.example.baseproject.ui.profile

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.example.baseproject.models.User
import com.example.baseproject.respository.AuthRepository
import com.example.baseproject.utils.UIState
import com.example.core.base.BaseViewModel
import com.google.firebase.auth.FirebaseAuth
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

@HiltViewModel
class ProfileViewModel @Inject constructor(
    private val repository: AuthRepository
    ) : BaseViewModel(){
    private val _signout = MutableLiveData<UIState<String>>()
    val signout: LiveData<UIState<String>> get() = _signout

    private val _currentUser: MutableLiveData<User> = MutableLiveData()
    val currentUser: LiveData<User> get() = _currentUser

    fun logoutUser() {
        _signout.value = UIState.Loading

        repository.signoutUser{
            _signout.value = it
        }
    }

    fun getCurrentUser(){
        repository.getUserById(FirebaseAuth.getInstance().currentUser!!.uid, _currentUser)
    }
}