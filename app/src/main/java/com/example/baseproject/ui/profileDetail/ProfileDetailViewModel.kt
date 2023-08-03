package com.example.baseproject.ui.profileDetail

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.example.baseproject.models.User
import com.example.baseproject.utils.Constants
import com.example.baseproject.utils.UIState
import com.example.core.base.BaseViewModel
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import com.google.firebase.database.ktx.getValue
import com.google.firebase.storage.FirebaseStorage
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

@HiltViewModel
class ProfileDetailViewModel @Inject constructor(
    private val database: FirebaseDatabase,
    private val auth: FirebaseAuth,
    private val storage: FirebaseStorage
) : BaseViewModel() {
    private val _currentUser: MutableLiveData<User> = MutableLiveData()
    val currentUser: LiveData<User> get() = _currentUser

    private val _isSuccess = MutableLiveData<UIState<String>>()

    init {
        getCurrentUser()
    }

    private fun getCurrentUser(){
        val id = auth.currentUser!!.uid
        val userRef = database.getReference(Constants.USER).child(id).child(Constants.PROFILE)

        userRef.addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                if (snapshot.exists()) {
                    val user = User(
                        id = id,
                        name = snapshot.child(Constants.USER_NAME).getValue<String?>() ?: "",
                        email = snapshot.child(Constants.USER_EMAIL).getValue<String?>() ?: "",
                        phoneNumber = snapshot.child(Constants.USER_PHONENUMBER).getValue<String?>() ?: "",
                        dateOfBirth = snapshot.child(Constants.USER_DATE_OF_BIRTH).getValue<String>() ?: "",
                        avatar = snapshot.child(Constants.USER_AVATAR).getValue<String>() ?: ""
                    )
                    _currentUser.postValue(user)
                }
            }

            override fun onCancelled(error: DatabaseError) {
            }
        })
    }

    fun updateUserInfor(user: User, result: (UIState<String>) -> Unit) {
        val id = auth.currentUser!!.uid
        val userRef = database.getReference(Constants.USER).child(id).child(Constants.PROFILE)

        _isSuccess.value = UIState.Loading

        userRef
            .setValue(user)
            .addOnSuccessListener {
                result.invoke(UIState.Success(Constants.SUCCESS))

            }
            .addOnFailureListener {
                result.invoke(
                    UIState.Failure(it.localizedMessage)
                )
            }
    }
}