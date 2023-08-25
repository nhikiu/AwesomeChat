package com.example.baseproject.ui.signup

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.example.baseproject.models.User
import com.example.baseproject.respository.AuthRepository
import com.example.baseproject.utils.Constants
import com.example.baseproject.utils.UIState
import com.example.core.base.BaseViewModel
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.messaging.FirebaseMessaging
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

@HiltViewModel
class SignupViewModel @Inject constructor(
    private val repository: AuthRepository,
    private val fcm: FirebaseMessaging,
    private val auth: FirebaseAuth,
    private val database: FirebaseDatabase
) : BaseViewModel() {

    private val _signup = MutableLiveData<UIState<String>>()
    val signup: LiveData<UIState<String>> get() = _signup

    fun signupUser(email: String, password: String, user: User) {
        _signup.value = UIState.Loading

        repository.signupUser(
            email = email,
            password = password,
            user = user
        ) { state ->
            _signup.value = state
            if (state == UIState.Success(Constants.SUCCESS)) {
                fcm.token.addOnSuccessListener { token ->
                    val userRef = auth.currentUser?.let {
                        database.reference.child(Constants.USER).child(
                            it.uid
                        )
                    }
                    userRef?.child(Constants.PROFILE)?.child(Constants.USER_TOKEN)?.setValue(token)
                }
            }
        }
    }

}