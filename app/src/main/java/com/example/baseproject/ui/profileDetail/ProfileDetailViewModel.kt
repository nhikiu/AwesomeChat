package com.example.baseproject.ui.profileDetail

import android.net.Uri
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.example.baseproject.models.User
import com.example.baseproject.ui.chats.ActionState
import com.example.baseproject.utils.Constants
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

    val actionProfileDetail = MutableLiveData<ActionState>()
    val actionAvatar = MutableLiveData<ActionState>()
    val actionUpdate = MutableLiveData<ActionState>()

    fun getCurrentUser() {
        actionProfileDetail.value = ActionState.Loading
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
                    actionProfileDetail.value = ActionState.Finish
                }
            }

            override fun onCancelled(error: DatabaseError) {
                actionProfileDetail.value = ActionState.Fail
            }
        })
    }

    fun updateUserInfor(user: User) {
        actionUpdate.value = ActionState.Loading
        val id = auth.currentUser?.uid
        val userRef =
            id?.let { database.getReference(Constants.USER).child(it).child(Constants.PROFILE) }

        userRef?.setValue(user)?.addOnCompleteListener {
            it.addOnSuccessListener {
                actionUpdate.value = ActionState.Finish
            }.addOnFailureListener {
                actionUpdate.value = ActionState.Fail
            }
        }
    }

    fun uploadImageToStorage(user: User, uri: Uri) {
        actionAvatar.value = ActionState.Loading
        try {
            val avatarRef = storage.reference.child(Constants.USER_AVATAR).child(user.id)
            avatarRef.putFile(uri)
                .addOnSuccessListener {
                    val id = auth.currentUser?.uid
                    val userRef =
                        id?.let { it1 ->
                            database.getReference(Constants.USER).child(it1)
                                .child(Constants.PROFILE)
                        }
                    avatarRef
                        .downloadUrl
                        .addOnSuccessListener { avatarUri ->
                            val imgUrl = avatarUri.toString()
                            val newUser = user.copy(avatar = imgUrl)
                            userRef?.setValue(newUser)?.addOnCompleteListener {
                                it.addOnSuccessListener {
                                    actionAvatar.value = ActionState.Finish
                                }.addOnFailureListener {
                                    actionAvatar.value = ActionState.Fail
                                }
                            }
                        }
                }
                .addOnFailureListener {
                    actionAvatar.value = ActionState.Fail
                }
        } catch (e: java.lang.Exception) {
            actionAvatar.value = ActionState.Fail
        }
    }
}