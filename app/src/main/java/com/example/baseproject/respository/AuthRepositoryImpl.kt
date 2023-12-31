package com.example.baseproject.respository

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
import timber.log.Timber
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
                val currentUser = auth.currentUser
                if (currentUser != null) {
                    user.id = currentUser.uid

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
        result.invoke(UIState.Success(Constants.SUCCESS))
        auth.signOut()
    }

    override fun getUserById(id: String, liveData: MutableLiveData<User>) {
        val userRef = database.getReference(Constants.USER).child(id).child(Constants.PROFILE)

        userRef.addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                if (snapshot.exists()) {
                    val user = User(
                        id = id,
                        name = snapshot.child(Constants.USER_NAME).getValue<String?>() ?: "",
                        email = snapshot.child(Constants.USER_EMAIL).getValue<String?>() ?: "",
                        phoneNumber = snapshot.child(Constants.USER_PHONENUMBER).getValue<String?>() ?: "",
                        dateOfBirth = snapshot.child(Constants.USER_DATE_OF_BIRTH).getValue<String?>() ?: "",
                        avatar = snapshot.child(Constants.USER_AVATAR).getValue<String>() ?: "",
                        token = snapshot.child(Constants.USER_TOKEN).getValue<String>() ?: ""
                    )
                    liveData.postValue(user)
                }
            }

            override fun onCancelled(error: DatabaseError) {
            }
        })
    }

    override fun getRealFriend(liveData: MutableLiveData<List<Friend>>) {
    }

    override fun getAllFriend(liveData: MutableLiveData<List<Friend>>) {
        auth.currentUser?.uid?.let {
            database.getReference(Constants.USER).child(it).child(Constants.FRIEND)
        }?.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                val friendList: MutableList<Friend> = mutableListOf()

                for (dataSnapshot in snapshot.children) {
                    val userHashMap: HashMap<*, *>? = dataSnapshot.value as? HashMap<*, *>
                    userHashMap?.let {
                        val friend = Friend(
                            name = userHashMap[Constants.USER_NAME] as? String ?: "",
                            avatar = userHashMap[Constants.USER_AVATAR] as? String ?: "",
                            id = userHashMap[Constants.USER_ID] as? String ?: "",
                            status = userHashMap[Constants.USER_STATUS] as? String ?: "",
                            token = userHashMap[Constants.USER_TOKEN] as? String ?: "",
                        )
                        friendList.add(friend)
                    }
                }

                liveData.postValue(friendList)

            }

            override fun onCancelled(error: DatabaseError) {
                Timber.tag("database").e(error.toException().toString(), "onCancelled: Fail %s")
            }

        })
    }
}