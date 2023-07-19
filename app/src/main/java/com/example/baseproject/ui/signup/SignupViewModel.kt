package com.example.baseproject.ui.signup

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.example.baseproject.models.User
import com.example.baseproject.respository.AuthRepository
import com.example.baseproject.utils.UIState
import com.example.core.base.BaseViewModel
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

@HiltViewModel
class SignupViewModel @Inject constructor(val repository : AuthRepository) :  BaseViewModel(){

    private val _signup = MutableLiveData<UIState<String>>()
    val signup: LiveData<UIState<String>> get() = _signup

    fun signupUser(email: String, password: String, user: User) {
        _signup.value = UIState.Loading

        repository.signupUser(
            email = email,
            password = password,
            user = user
        ) {
            _signup.value = it
        }
    }

}