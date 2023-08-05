package com.example.baseproject.ui.friends.realFriend

import android.annotation.SuppressLint
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.example.baseproject.models.Friend
import com.example.baseproject.utils.Constants
import com.example.baseproject.utils.UIState
import com.example.core.base.BaseViewModel
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

@HiltViewModel
class RealFriendViewModel @Inject constructor(
    private val database: FirebaseDatabase,
    private val auth: FirebaseAuth
) : BaseViewModel(){
    private val mRealFriendList = arrayListOf<Friend>()

    private var _realFriendListLiveData: MutableLiveData<List<Friend>> = MutableLiveData()
    val realFriendListLiveData: LiveData<List<Friend>> get() = _realFriendListLiveData

    init {
        _realFriendListLiveData.value = mRealFriendList
    }

    fun getAllRealFriend(result: (UIState<String>) -> Unit) {
        val userRef = database.getReference(Constants.USER)
        
        result.invoke(UIState.Loading)

        userRef.addValueEventListener(object : ValueEventListener {
            @SuppressLint("SuspiciousIndentation")
            override fun onDataChange(snapshot: DataSnapshot) {
                mRealFriendList.clear()
                for (dataSnapshot in snapshot.children) {
                    val profileRef = dataSnapshot.child(Constants.PROFILE)

                    val id = profileRef.child(Constants.USER_ID).getValue(String::class.java)
                    val name = profileRef.child(Constants.USER_NAME).getValue(String::class.java)
                    val avatar =
                        profileRef.child(Constants.USER_AVATAR).getValue(String::class.java)

                    if (id != auth.currentUser!!.uid && id != null && name != null) {
                        mRealFriendList.add(Friend(id, name, avatar, Constants.STATE_UNFRIEND))
                    }
                }

                val friendRef = userRef.child(auth.currentUser!!.uid).child(Constants.FRIEND)

                friendRef
                    .get()
                    .addOnSuccessListener {
                        if (it.value != null) {
                            val friends = it.value as HashMap<*, *>
                            for (i in friends.values) {
                                val friend = i as HashMap<*, *>
                                val id = friend[Constants.USER_ID] as String
                                val status = friend[Constants.USER_STATUS] as String
                                for (mFriend in mRealFriendList) {
                                    if (id == mFriend.id) {
                                        mFriend.status = status
                                    }
                                }
                            }
                        }
                        _realFriendListLiveData.value = mRealFriendList.filter { realFriend -> realFriend.status == Constants.STATE_FRIEND  }
                        result.invoke(UIState.Success(Constants.FRIEND))
                    }
            }

            override fun onCancelled(error: DatabaseError) {
                result.invoke(UIState.Failure(error.message))
            }
        })
    }
}