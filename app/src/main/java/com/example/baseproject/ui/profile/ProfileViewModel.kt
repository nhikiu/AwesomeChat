package com.example.baseproject.ui.profile

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.example.baseproject.models.User
import com.example.baseproject.respository.AuthRepository
import com.example.baseproject.utils.Constants
import com.example.baseproject.utils.UIState
import com.example.core.base.BaseViewModel
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import com.google.firebase.database.ktx.getValue
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

@HiltViewModel
class ProfileViewModel @Inject constructor(
    private val repository: AuthRepository,
    private val database: FirebaseDatabase,
    private val auth: FirebaseAuth
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
        val id = auth.currentUser!!.uid
        val userRef = database.getReference(Constants.USER).child(id).child(Constants.PROFILE)

        userRef.addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                if (snapshot.exists()) {
                    val user = User(
                        id = id,
                        name = snapshot.child("name").getValue<String?>() ?: "",
                        email = snapshot.child("email").getValue<String?>() ?: "",
                        phoneNumber = snapshot.child("phoneNumber").getValue<String?>() ?: "",
                        dateOfBirth = null,
                        avatar = null
                    )
                    _currentUser.postValue(user)
                }
            }

            override fun onCancelled(error: DatabaseError) {
            }
        })
    }
}