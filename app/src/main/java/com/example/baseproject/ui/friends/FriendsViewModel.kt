package com.example.baseproject.ui.friends

import android.annotation.SuppressLint
import android.content.Context
import android.os.Build
import androidx.annotation.RequiresApi
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.example.baseproject.R
import com.example.baseproject.models.Friend
import com.example.baseproject.notification.SendNotification
import com.example.baseproject.ui.chats.ActionState
import com.example.baseproject.utils.Constants
import com.example.baseproject.utils.ListUtils
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

    private var _query: MutableLiveData<String> = MutableLiveData()
    val query: LiveData<String> get() = _query

    val actionFriend = MutableLiveData<ActionState>()
    fun setSearchQuery(query: String) {
        _query.value = query
        getAllUser()
    }

    init {
        getAllUser()
    }

    private fun getAllUser() {
        actionFriend.value = ActionState.Loading

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
                    val token = profileRef.child(Constants.USER_TOKEN).getValue(String::class.java)

                    if (id != auth.currentUser?.uid && id != null && name != null) {
                        mFriendList.add(Friend(id, name, avatar, Constants.STATE_UNFRIEND, token))
                    }
                }

                val friendRef = auth.currentUser?.uid?.let { myRef.child(it).child(Constants.FRIEND) }

                friendRef?.get()?.addOnSuccessListener {
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

                    val searchString: String = _query.value ?: ""
                    _friendListLiveData.postValue(ListUtils.sortByName(mFriendList.filter { friend ->
                        ListUtils.removeVietnameseAccents(
                            friend.name.lowercase()
                        ).contains(
                            ListUtils.removeVietnameseAccents(
                                searchString.lowercase().trim()
                            )
                        )
                    }.toMutableList()))
                    actionFriend.value = ActionState.Finish
                }?.addOnFailureListener {
                    actionFriend.value = ActionState.Fail
                }
            }

            override fun onCancelled(error: DatabaseError) {
                actionFriend.value = ActionState.Fail
            }

        })
    }

    @RequiresApi(Build.VERSION_CODES.O)
    fun updateFriendState(friend: Friend, context: Context) {
        val userRef = database.getReference(Constants.USER)
        val currentId = auth.currentUser?.uid
        //update friend in current user
        if (currentId != null) {
            userRef.child(currentId)
                .child(Constants.FRIEND).child(friend.id)
                .setValue(friend)
                .addOnSuccessListener {
                    if (friend.status == Constants.STATE_FRIEND) {
                        sendNotification(friend, context.getString(R.string.agree_to_be_friend))
                    }
                }
                .addOnFailureListener {

                }
        }

        //update friend in other user
        var status = Constants.STATE_RECEIVE

        when (friend.status) {
            Constants.STATE_FRIEND -> status = Constants.FRIEND
            Constants.STATE_RECEIVE -> status = Constants.STATE_SEND
            Constants.STATE_SEND -> status = Constants.STATE_RECEIVE
            Constants.STATE_UNFRIEND -> status = Constants.STATE_UNFRIEND
        }

        if (currentId != null) {
            userRef.child(currentId).child(Constants.PROFILE)
                .addListenerForSingleValueEvent(object : ValueEventListener {
                    @RequiresApi(Build.VERSION_CODES.O)
                    override fun onDataChange(snapshot: DataSnapshot) {
                        if (snapshot.exists()) {
                            val name = snapshot.child(Constants.USER_NAME).getValue<String?>() ?: ""
                            val avatar = snapshot.child(Constants.USER_AVATAR).getValue<String?>() ?: ""
                            val token = snapshot.child(Constants.USER_TOKEN).getValue<String?>() ?: ""
                            val currentFriend = Friend(
                                id = currentId,
                                name = name,
                                status = status,
                                avatar = avatar,
                                token = token
                            )
                            userRef.child(friend.id)
                                .child(Constants.FRIEND).child(currentId)
                                .setValue(currentFriend)
                                .addOnSuccessListener {
                                    if (currentFriend.status == Constants.STATE_RECEIVE) {
                                        sendNotification(friend, context.getString(R.string.sent_add_friend))
                                    }
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

    @RequiresApi(Build.VERSION_CODES.O)
    fun sendNotification(friend: Friend, content: String) {

        val currentId = auth.currentUser?.uid
        if (currentId != null) {
            SendNotification.sendNotification(
                friend.token.toString(),
                FcmNotification(
                    title = friend.name,
                    body = content),
                DataModel("high", null, true, content, friend.name, currentId)
            )
        }
    }
}