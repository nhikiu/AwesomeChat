package com.example.baseproject.respository

import android.util.Log
import androidx.lifecycle.MutableLiveData
import com.example.baseproject.models.Friend
import com.example.baseproject.models.User
import com.example.baseproject.utils.Constants
import com.example.baseproject.utils.UIState
import com.google.firebase.FirebaseNetworkException
import com.google.firebase.FirebaseTooManyRequestsException
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseAuthInvalidCredentialsException
import com.google.firebase.auth.FirebaseAuthInvalidUserException
import com.google.firebase.auth.FirebaseAuthUserCollisionException
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import com.google.firebase.database.ktx.getValue
import com.google.firebase.database.ktx.values
import javax.inject.Inject


class AuthRepositoryImpl @Inject constructor(
    private val auth : FirebaseAuth,
    private val database : FirebaseDatabase
) : AuthRepository {
    override fun loginUser(email: String, password: String, result: (UIState<String>) -> Unit) {
        auth.signInWithEmailAndPassword(email, password)
            .addOnSuccessListener {
                result.invoke(
                    UIState.Success(Constants.SUCCESS)
                )
            }
            .addOnFailureListener { exception ->
                val errorMessage = when(exception) {
                    is FirebaseAuthInvalidUserException -> Constants.USER_NOT_FOUND
                    is FirebaseAuthInvalidCredentialsException -> Constants.EMAIL_PASSWORD_INVALID
                    is FirebaseNetworkException -> Constants.NETWORK_NOT_CONNECTION
                    is FirebaseTooManyRequestsException -> Constants.TOO_MANY_REQUEST
                    else -> exception.localizedMessage
                }
                result.invoke(UIState.Failure(errorMessage))
            }
    }

    override fun signupUser(
        email: String,
        password: String,
        user: User,
        result: (UIState<String>) -> Unit
    ) {
        auth.createUserWithEmailAndPassword(email, password)
            .addOnSuccessListener {
                user.id = auth.currentUser!!.uid
                updateUserInfor(user) { state ->
                    when(state){
                        is UIState.Success -> {
                            result.invoke(UIState.Success(Constants.SUCCESS))
                        }
                        is UIState.Failure -> {
                            result.invoke(UIState.Failure(state.error))
                        }
                        else -> {}
                    }
                }
            }
            .addOnFailureListener { exception ->
                val errorMessage = when(exception) {
                    is FirebaseNetworkException -> Constants.NETWORK_NOT_CONNECTION
                    is FirebaseAuthUserCollisionException -> Constants.USER_EXIST
                    else -> exception.localizedMessage
                }
                result.invoke(UIState.Failure(errorMessage))
            }
    }

    override fun updateUserInfor(user: User, result: (UIState<String>) -> Unit) {
        val userRef = database.reference.child(Constants.USER).child(user.id)

        userRef.child(Constants.PROFILE)
            .setValue(user)
            .addOnSuccessListener {
                result.invoke(UIState.Success(Constants.SUCCESS))
            }
            .addOnFailureListener {
                result.invoke(
                    UIState.Failure(it.localizedMessage)
                )
            }
    }

    override fun signoutUser(result: (UIState<String>) -> Unit) {
        auth.signOut()
        result.invoke(UIState.Success(Constants.SUCCESS))
    }

    override fun getAllUser(liveData: MutableLiveData<List<Friend>>) {
        val myRef = database.getReference(Constants.USER)

        myRef.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {

                val friendList: MutableList<Friend> = mutableListOf()

                for (dataSnapshot in snapshot.children) {
                    val userHashMap: HashMap<String, User>? = dataSnapshot.child(Constants.PROFILE).getValue() as? HashMap<String, User>
                    userHashMap?.let {
                        val friend = Friend(
                            name = userHashMap["name"] as? String ?: "",
                            avatar = userHashMap["avatar"] as? String ?: "",
                            id = userHashMap["id"] as? String ?: "",
                            status = userHashMap["status"] as? String ?: ""
                        )
                        friendList.add(friend)
                    }
                }

                liveData.postValue(friendList)

            }

            override fun onCancelled(error: DatabaseError) {
                Log.e("database", "onCancelled: Fail ${error.toException()}", )
            }

        })

    }

    override fun getUserById(id: String, liveData: MutableLiveData<User>) {
        val userRef = database.getReference(Constants.USER).child(id).child("profile")

        userRef.addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                if (snapshot.exists()) {
                    val user = User(
                        id = id,
                        name = snapshot.child("name").getValue<String?>() ?: "",
                        email = snapshot.child("email").getValue<String?>() ?: "",
                        phoneNumber = snapshot.child("phoneNumber").getValue<String?>() ?: "",
                        dateOfBirth = null,
                        avatar = null
                    )
                    liveData.postValue(user)
                }
            }

            override fun onCancelled(error: DatabaseError) {
            }
        })
    }

    override fun updateFriendState(friend: Friend) {
        val friendRef = database.getReference(Constants.USER).child(auth.currentUser!!.uid)
            .child("friend")

        val map = mapOf(
            "id" to friend.id,
            "name" to friend.name,
            "avatar" to friend.avatar,
            "status" to friend.status,
        )

        friendRef.child(friend.id)
            .setValue(friend)
            .addOnSuccessListener {
            }
            .addOnFailureListener {

            }

    }

    override fun getAllFriend(liveData: MutableLiveData<List<Friend>>) {
        val myRef = database.getReference(Constants.USER).child(auth.currentUser!!.uid).child("friend")

        myRef.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                Log.e("firebase", "onDataChange: $snapshot", )
                val friendList: MutableList<Friend> = mutableListOf()

                for (dataSnapshot in snapshot.children) {
                    val userHashMap: HashMap<String, User>? = dataSnapshot.getValue() as? HashMap<String, User>
                    Log.e("firebase", "hash map: $userHashMap", )
                    userHashMap?.let {
                        val friend = Friend(
                            name = userHashMap["name"] as? String ?: "",
                            avatar = userHashMap["avatar"] as? String ?: "",
                            id = userHashMap["id"] as? String ?: "",
                            status = userHashMap["status"] as? String ?: ""
                        )
                        friendList.add(friend)

                        Log.e("firebase", "Friend $friend")
                    }
                }

                liveData.postValue(friendList)

            }

            override fun onCancelled(error: DatabaseError) {
                Log.e("database", "onCancelled: Fail ${error.toException()}", )
            }

        })
    }
}