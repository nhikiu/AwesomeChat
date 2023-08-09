package com.example.baseproject.ui.friends

import android.annotation.SuppressLint
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.example.baseproject.models.Friend
import com.example.baseproject.utils.Constants
import com.example.baseproject.utils.ListUtils
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
class FriendsViewModel @Inject constructor(
    private val database: FirebaseDatabase,
    private val auth: FirebaseAuth
) : BaseViewModel() {

    private val mFriendList = arrayListOf<Friend>()

    private var _friendListLiveData: MutableLiveData<List<Friend>> = MutableLiveData()
    val friendListLiveData: LiveData<List<Friend>> get() = _friendListLiveData

    init {
        getAllUser()
    }

    private fun getAllUser() {
        val myRef = database.getReference(Constants.USER)

        myRef.addValueEventListener(object : ValueEventListener {
            @SuppressLint("SuspiciousIndentation")
            override fun onDataChange(snapshot: DataSnapshot) {
                mFriendList.clear()
                for (dataSnapshot in snapshot.children) {
                    val profileRef = dataSnapshot.child(Constants.PROFILE)

                    val id = profileRef.child(Constants.USER_ID).getValue(String::class.java)
                    val name = profileRef.child(Constants.USER_NAME).getValue(String::class.java)
                    val avatar =
                        profileRef.child(Constants.USER_AVATAR).getValue(String::class.java)

                    if (id != auth.currentUser!!.uid && id != null && name != null) {
                        mFriendList.add(Friend(id, name, avatar, Constants.STATE_UNFRIEND))
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
                                for (mFriend in mFriendList) {
                                    if (id == mFriend.id) {
                                        mFriend.status = status
                                    }
                                }
                            }
                        }
                        _friendListLiveData.value = ListUtils.sortByName(mFriendList)
                    }
                    .addOnFailureListener {
                    }
            }

            override fun onCancelled(error: DatabaseError) {
            }

        })
    }

    fun updateFriendState(friend: Friend, result: (UIState<String>) -> Unit) {
        val userRef = database.getReference(Constants.USER)
        val currentId = auth.currentUser!!.uid
        //update friend in current user
        result.invoke(UIState.Loading)
        userRef.child(currentId)
            .child(Constants.FRIEND).child(friend.id)
            .setValue(friend)
            .addOnSuccessListener {
                result.invoke(UIState.Success(currentId))
            }
            .addOnFailureListener {
                result.invoke(UIState.Failure(it.localizedMessage))
            }

        //update friend in other user
        var status = Constants.STATE_RECEIVE

        when (friend.status) {
            Constants.STATE_FRIEND -> status = Constants.FRIEND
            Constants.STATE_RECEIVE -> status = Constants.STATE_SEND
            Constants.STATE_SEND -> status = Constants.STATE_RECEIVE
            Constants.STATE_UNFRIEND -> status = Constants.STATE_UNFRIEND
        }

        userRef.child(currentId).child(Constants.PROFILE)
            .addListenerForSingleValueEvent(object : ValueEventListener {
                override fun onDataChange(snapshot: DataSnapshot) {
                    if (snapshot.exists()) {
                        val name = snapshot.child(Constants.USER_NAME).getValue<String?>() ?: ""
                        val avatar = snapshot.child(Constants.USER_AVATAR).getValue<String?>() ?: ""
                        val currentFriend = Friend(
                            id = currentId,
                            name = name,
                            status = status,
                            avatar = avatar
                        )
                        userRef.child(friend.id)
                            .child(Constants.FRIEND).child(currentId)
                            .setValue(currentFriend)
                            .addOnSuccessListener {
                                result.invoke(UIState.Success(currentId))
                            }
                            .addOnFailureListener {
                                result.invoke(UIState.Failure(it.localizedMessage))
                            }
                    }
                }

                override fun onCancelled(error: DatabaseError) {
                    result.invoke(UIState.Failure(error.message))
                }
            })
    }
}