package com.example.baseproject.ui.friends.realFriend

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.example.baseproject.models.Friend
import com.example.baseproject.models.User
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


    fun getAllRealFriend() {
        val friendRef = database.getReference(Constants.USER).child(auth.currentUser!!.uid).child(
            Constants.FRIEND)

        friendRef.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {

                for (dataSnapshot in snapshot.children) {
                    val userHashMap: HashMap<String, User>? = dataSnapshot.value as? HashMap<String, User>
                    userHashMap?.let {
                        val status = userHashMap["status"] as? String ?: ""
                        val friend = Friend(
                            name = userHashMap["name"] as? String ?: "",
                            avatar = userHashMap["avatar"] as? String ?: "",
                            id = userHashMap["id"] as? String ?: "",
                            status = status
                        )

                        if (status == Constants.STATE_FRIEND) {
                            mRealFriendList.add(friend)
                        }
                    }
                }
                _realFriendListLiveData.value = mRealFriendList.toMutableList()
                mRealFriendList.clear()
            }

            override fun onCancelled(error: DatabaseError) {
                Log.e("database", "onCancelled: Fail ${error.toException()}", )
            }

        })
    }
}