package com.example.baseproject.ui.friends.allFriend

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
class AllFriendViewModel @Inject constructor(
    private val database: FirebaseDatabase,
    private val auth: FirebaseAuth
) : BaseViewModel() {

    private val mFriendList = arrayListOf<Friend>()

    private var _friendListLiveData: MutableLiveData<List<Friend>> = MutableLiveData()
    val friendListLiveData: LiveData<List<Friend>> get() = _friendListLiveData

    init {

//        _friendListLiveData.postValue(mFriendList)
        getAllUser()
    }

    private fun getAllUser() {
        val myRef = database.getReference(Constants.USER)
        myRef.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                mFriendList.clear()
                for (dataSnapshot in snapshot.children) {

                    val profileRef = dataSnapshot.child(Constants.PROFILE)

                    val id = profileRef.child("id").getValue(String::class.java)
                    val name = profileRef.child("name").getValue(String::class.java)
                    val avatar = profileRef.child("avatar").getValue(String::class.java)
                    if (id != auth.currentUser!!.uid && id != null && name != null) {
                        mFriendList.add(Friend(id, name, avatar, Constants.STATE_UNFRIEND))
                    }
                }
            }

            override fun onCancelled(error: DatabaseError) {
            }

        })

        myRef.child(auth.currentUser!!.uid).child(Constants.FRIEND)
            .addValueEventListener(object : ValueEventListener {
                override fun onDataChange(snapshot: DataSnapshot) {
                    for (dataSnapshot in snapshot.children) {

                        val friend = dataSnapshot.value as HashMap<*, *>

                        val id = friend["id"] as String
                        val status = friend["status"] as String

                        for (i in mFriendList) {
                            if (id == i.id) {
                                i.status = status
                            }
                        }

                    }
                    _friendListLiveData.postValue(mFriendList.toMutableList())

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