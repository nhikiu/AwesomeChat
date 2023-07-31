package com.example.baseproject.ui.friends.pendingFriend

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.example.baseproject.models.Friend
import com.example.baseproject.utils.Constants
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
        _sendFriendListLiveData.postValue(mSendFriendList)
        _receiveFriendListLiveData.postValue(mReceiveFriendList)
    }


    fun getSendRealFriend() {
        val friendRef = database.getReference(Constants.USER).child(auth.currentUser!!.uid).child(
            Constants.FRIEND)

        friendRef.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {

                for (dataSnapshot in snapshot.children) {
                    val userHashMap: HashMap<*, *> = dataSnapshot.value as HashMap<*, *>
                    userHashMap.let {
                        val status = userHashMap["status"] as? String ?: ""
                        val friend = Friend(
                            name = userHashMap["name"] as? String ?: "",
                            avatar = userHashMap["avatar"] as? String ?: "",
                            id = userHashMap["id"] as? String ?: "",
                            status = status
                        )

                        if (status == Constants.STATE_SEND) {
                            mSendFriendList.add(friend)
                        }
                    }
                }
                _sendFriendListLiveData.postValue(mSendFriendList.toMutableList())
                mSendFriendList.clear()
            }

            override fun onCancelled(error: DatabaseError) {
            }

        })
    }

    fun getReceiveRealFriend() {
        val friendRef = database.getReference(Constants.USER).child(auth.currentUser!!.uid).child(
            Constants.FRIEND)

        friendRef.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {

                for (dataSnapshot in snapshot.children) {
                    val userHashMap: HashMap<*, *> = dataSnapshot.value as HashMap<*, *>
                    userHashMap.let {
                        val status = userHashMap["status"] as? String ?: ""
                        val friend = Friend(
                            name = userHashMap["name"] as? String ?: "",
                            avatar = userHashMap["avatar"] as? String ?: "",
                            id = userHashMap["id"] as? String ?: "",
                            status = status
                        )

                        if (status == Constants.STATE_RECEIVE) {
                            mReceiveFriendList.add(friend)
                        }
                    }
                }
                _receiveFriendListLiveData.postValue(mReceiveFriendList.toMutableList())
                mReceiveFriendList.clear()
            }

            override fun onCancelled(error: DatabaseError) {
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
                    val name = snapshot.child("name").getValue<String?>() ?: ""

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