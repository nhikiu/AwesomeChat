package com.example.baseproject.ui.friends.pendingFriend

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
import com.google.firebase.database.ktx.getValue
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

@HiltViewModel
class PendingFriendViewModel @Inject constructor(
    private val database: FirebaseDatabase,
    private val auth: FirebaseAuth
) : BaseViewModel(){
    private val mSendFriendList = arrayListOf<Friend>()
    private val mReceiveFriendList = arrayListOf<Friend>()

    private var _sendFriendListLiveData: MutableLiveData<List<Friend>> = MutableLiveData()
    private var _receiveFriendListLiveData: MutableLiveData<List<Friend>> = MutableLiveData()
    val sendFriendListLiveData: LiveData<List<Friend>> get() = _sendFriendListLiveData
    val receiveFriendListLiveData: LiveData<List<Friend>> get() = _receiveFriendListLiveData

    init {
        _sendFriendListLiveData.value = mSendFriendList
        _receiveFriendListLiveData.value = mReceiveFriendList
    }


    fun getSendRealFriend(result: (UIState<String>) -> Unit) {
        val myRef = database.getReference(Constants.USER)

        result.invoke(UIState.Loading)

        myRef.addValueEventListener(object : ValueEventListener {
            @SuppressLint("SuspiciousIndentation")
            override fun onDataChange(snapshot: DataSnapshot) {
                mSendFriendList.clear()
                for (dataSnapshot in snapshot.children) {
                    val profileRef = dataSnapshot.child(Constants.PROFILE)

                    val id = profileRef.child(Constants.USER_ID).getValue(String::class.java)
                    val name = profileRef.child(Constants.USER_NAME).getValue(String::class.java)
                    val avatar =
                        profileRef.child(Constants.USER_AVATAR).getValue(String::class.java)

                    if (id != auth.currentUser!!.uid && id != null && name != null) {
                        mSendFriendList.add(Friend(id, name, avatar, Constants.STATE_UNFRIEND))
                    }
                }

                val friendRef = myRef.child(auth.currentUser!!.uid).child(Constants.FRIEND)

                friendRef
                    .get()
                    .addOnSuccessListener {
                        if (it.value != null) {
                            val friends = it.value as HashMap<*, *>
                            for (i in friends.values) {
                                val friend = i as HashMap<*, *>
                                val id = friend[Constants.USER_ID] as String
                                val status = friend[Constants.USER_STATUS] as String
                                for (mFriend in mSendFriendList) {
                                    if (id == mFriend.id) {
                                        mFriend.status = status
                                    }
                                }
                            }
                        }
                        _sendFriendListLiveData.value = mSendFriendList.filter { sendFriend -> sendFriend.status == Constants.STATE_SEND }
                        result.invoke(UIState.Success(Constants.FRIEND))
                    }
            }

            override fun onCancelled(error: DatabaseError) {
                result.invoke(UIState .Failure(error.message))
            }

        })
    }

    fun getReceiveRealFriend(result: (UIState<String>) -> Unit) {
        val myRef = database.getReference(Constants.USER)

        result.invoke(UIState.Loading)

        myRef.addValueEventListener(object : ValueEventListener {
            @SuppressLint("SuspiciousIndentation")
            override fun onDataChange(snapshot: DataSnapshot) {
                mReceiveFriendList.clear()
                for (dataSnapshot in snapshot.children) {
                    val profileRef = dataSnapshot.child(Constants.PROFILE)

                    val id = profileRef.child(Constants.USER_ID).getValue(String::class.java)
                    val name = profileRef.child(Constants.USER_NAME).getValue(String::class.java)
                    val avatar =
                        profileRef.child(Constants.USER_AVATAR).getValue(String::class.java)

                    if (id != auth.currentUser!!.uid && id != null && name != null) {
                        mReceiveFriendList.add(Friend(id, name, avatar, Constants.STATE_UNFRIEND))
                    }
                }

                val friendRef = myRef.child(auth.currentUser!!.uid).child(Constants.FRIEND)

                friendRef
                    .get()
                    .addOnSuccessListener {
                        if (it.value != null) {
                            val friends = it.value as HashMap<*, *>
                            for (i in friends.values) {
                                val friend = i as HashMap<*, *>
                                val id = friend[Constants.USER_ID] as String
                                val status = friend[Constants.USER_STATUS] as String
                                for (mFriend in mReceiveFriendList) {
                                    if (id == mFriend.id) {
                                        mFriend.status = status
                                    }
                                }
                            }
                        }
                        _receiveFriendListLiveData.value = mReceiveFriendList.filter { receiveFriend -> receiveFriend.status == Constants.STATE_RECEIVE }
                        result.invoke(UIState.Success(Constants.FRIEND))
                    }
            }

            override fun onCancelled(error: DatabaseError) {
                result.invoke(UIState.Failure(error.message))
            }
        })
    }

    fun updateFriendState(friend: Friend) {
        val userRef = database.getReference(Constants.USER)
        val currentId = auth.currentUser!!.uid
        //update friend in current user
        userRef.child(currentId)
            .child(Constants.FRIEND).child(friend.id)
            .setValue(friend)
            .addOnSuccessListener {
            }
            .addOnFailureListener {

            }

        //update friend in other user

        var status = Constants.STATE_RECEIVE

        when(friend.status) {
            Constants.STATE_FRIEND -> status = Constants.FRIEND
            Constants.STATE_RECEIVE -> status = Constants.STATE_SEND
            Constants.STATE_SEND -> status = Constants.STATE_RECEIVE
            Constants.STATE_UNFRIEND -> status = Constants.STATE_UNFRIEND
        }

        userRef.child(currentId).child(Constants.PROFILE).addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                if (snapshot.exists()) {
                    val name = snapshot.child(Constants.USER_NAME).getValue<String?>() ?: ""

                    val currentFriend = Friend(
                        id = currentId,
                        name = name,
                        status = status,
                        avatar = ""
                    )

                    userRef.child(friend.id)
                        .child(Constants.FRIEND).child(currentId)
                        .setValue(currentFriend)
                        .addOnSuccessListener {
                        }
                        .addOnFailureListener {

                        }
                }
            }

            override fun onCancelled(error: DatabaseError) {
            }
        })

    }
}