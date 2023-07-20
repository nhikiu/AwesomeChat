package com.example.baseproject.ui.login

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.example.baseproject.respository.AuthRepository
import com.example.baseproject.utils.UIState
import com.example.core.base.BaseViewModel
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

@HiltViewModel
class LoginViewModel @Inject constructor(val repository: AuthRepository) : BaseViewModel(){
    private val _login = MutableLiveData<UIState<String>>()
    val login: LiveData<UIState<String>> get() = _login

    fun loginUser(email: String, password:String) {
        _login.value = UIState.Loading

        repository.loginUser(email, password) {
            _login.value = it
        }
    }

}