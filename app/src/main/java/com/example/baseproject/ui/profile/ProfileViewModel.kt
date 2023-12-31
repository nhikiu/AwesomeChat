package com.example.baseproject.ui.profile

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.example.baseproject.models.User
import com.example.baseproject.respository.AuthRepository
import com.example.baseproject.ui.chats.ActionState
import com.example.baseproject.utils.Constants
import com.example.baseproject.utils.UIState
import com.example.core.base.BaseViewModel
import com.example.core.pref.RxPreferences
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import com.google.firebase.database.ktx.getValue
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class ProfileViewModel @Inject constructor(
    private val repository: AuthRepository,
    private val database: FirebaseDatabase,
    private val auth: FirebaseAuth,
    private val rxPreferences: RxPreferences,
) : BaseViewModel() {
    private val _signout = MutableLiveData<UIState<String>>()
    val signout: LiveData<UIState<String>> get() = _signout

    private val _currentUser: MutableLiveData<User> = MutableLiveData()
    val currentUser: LiveData<User> get() = _currentUser

    val actionFriend = MutableLiveData<ActionState>()

    init {
        getCurrentUser()
    }

    fun logoutUser() {
        _signout.value = UIState.Loading

        repository.signoutUser {
            val uid = auth.currentUser?.uid
            if (it == UIState.Success(Constants.SUCCESS) && uid != null) {
                val userRef = database.reference.child(Constants.USER).child(uid)
                userRef.child(Constants.PROFILE).child(Constants.USER_TOKEN).setValue("")
                _signout.value = it
            }
        }
    }

    fun getCurrentUser() {
        actionFriend.value = ActionState.Loading
        val id = auth.currentUser?.uid
        val userRef =
            id?.let { database.getReference(Constants.USER).child(it).child(Constants.PROFILE) }

        userRef?.addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                if (snapshot.exists()) {
                    val user = User(
                        id = id,
                        name = snapshot.child(Constants.USER_NAME).getValue<String?>() ?: "",
                        email = snapshot.child(Constants.USER_EMAIL).getValue<String?>() ?: "",
                        phoneNumber = snapshot.child(Constants.USER_PHONENUMBER).getValue<String?>()
                            ?: "",
                        dateOfBirth = snapshot.child(Constants.USER_DATE_OF_BIRTH)
                            .getValue<String>() ?: "",
                        avatar = snapshot.child(Constants.USER_AVATAR).getValue<String>() ?: "",
                        token = snapshot.child(Constants.USER_TOKEN).getValue<String>() ?: ""
                    )

                    _currentUser.postValue(user)
                    actionFriend.value = ActionState.Finish
                }
            }

            override fun onCancelled(error: DatabaseError) {
                actionFriend.value = ActionState.Fail
            }
        })
    }

    fun setLanguage(language: String) = viewModelScope.launch(Dispatchers.IO) {
        rxPreferences.setLanguage(language)
    }
}