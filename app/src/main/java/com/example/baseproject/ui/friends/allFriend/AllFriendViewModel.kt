package com.example.baseproject.ui.friends.allFriend

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.example.baseproject.models.Friend
import com.example.baseproject.models.User
import com.example.baseproject.respository.AuthRepository
import com.example.baseproject.utils.Constants
import com.example.core.base.BaseViewModel
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
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
        getAllUser()
    }

    fun getAllUser() {
        val myRef = database.getReference(Constants.USER)
        myRef.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                Log.e("abc", "1")
                for (dataSnapshot in snapshot.children) {
                    val userHashMap: HashMap<String, User>? =
                        dataSnapshot.child(Constants.PROFILE).value as? HashMap<String, User>
                    userHashMap?.let {
                        val friend = Friend(
                            name = userHashMap["name"] as String,
                            avatar = userHashMap["avatar"] as String,
                            id = userHashMap["id"] as String,
                            status = Constants.STATE_UNFRIEND
                        )
                        if (friend.id != auth.currentUser!!.uid) {
                            mFriendList.add(friend)
                        }
                    }
                }


                myRef.child(auth.currentUser!!.uid).child(Constants.FRIEND)
                    .addValueEventListener(object : ValueEventListener {
                        override fun onDataChange(snapshot: DataSnapshot) {
                            Log.e("abc", "2")
                            for (dataSnapshot in snapshot.children) {
                                val userHashMap: HashMap<String, User>? =
                                    dataSnapshot.value as? HashMap<String, User>
                                userHashMap?.let {
                                    val id = userHashMap["id"] as? String ?: ""
                                    val status =
                                        userHashMap["status"] as? String ?: Constants.STATE_UNFRIEND

                                    for (user in mFriendList) {
                                        if (user.id == id) {
                                            user.status = status
                                        }
                                    }
                                }
                            }
                            _friendListLiveData.postValue(mFriendList.toMutableList())
                        }

                        override fun onCancelled(error: DatabaseError) {
                            Log.e("database", "onCancelled: Fail ${error.toException()}")
                        }

                    })
            }

            override fun onCancelled(error: DatabaseError) {
                Log.e("database", "onCancelled: Fail ${error.toException()}")
            }

        })
    }

    fun getFriend(friendList: MutableList<Friend>): List<Friend> {
        val friendRef = database.getReference(Constants.USER).child(auth.currentUser!!.uid)
            .child(Constants.FRIEND)

        friendRef.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {

                for (dataSnapshot in snapshot.children) {
                    val userHashMap: HashMap<String, User>? =
                        dataSnapshot.value as? HashMap<String, User>
                    userHashMap?.let {
                        val id = userHashMap["id"] as? String ?: ""
                        val status = userHashMap["status"] as? String ?: Constants.STATE_UNFRIEND

                        for (i in friendList) {
                            if (i.id == id) {
                                i.status = status
                            }
                        }
                    }
                }
                _friendListLiveData.postValue(friendList.toMutableList())
            }

            override fun onCancelled(error: DatabaseError) {
                Log.e("database", "onCancelled: Fail ${error.toException()}")
            }

        })
        return friendList

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
        val currentFriend = Friend(
            id = currentId,
            name = auth.currentUser!!.email.toString(),
            status = Constants.STATE_RECEIVE,
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