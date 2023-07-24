package com.example.baseproject.ui.profile

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.example.baseproject.respository.AuthRepository
import com.example.baseproject.utils.UIState
import com.example.core.base.BaseViewModel
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

@HiltViewModel
class ProfileViewModel @Inject constructor(private val repository: AuthRepository) : BaseViewModel(){
    private val _signout = MutableLiveData<UIState<String>>()
    val signout: LiveData<UIState<String>> get() = _signout
    fun logoutUser() {
        _signout.value = UIState.Loading

        repository.signoutUser{
            _signout.value = it
        }
    }
}