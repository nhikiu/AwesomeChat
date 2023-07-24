package com.example.baseproject.respository

import com.example.baseproject.models.User
import com.example.baseproject.utils.Constants
import com.example.baseproject.utils.UIState
import com.google.firebase.FirebaseNetworkException
import com.google.firebase.FirebaseTooManyRequestsException
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseAuthInvalidCredentialsException
import com.google.firebase.auth.FirebaseAuthInvalidUserException
import com.google.firebase.auth.FirebaseAuthUserCollisionException
import com.google.firebase.firestore.FirebaseFirestore

class AuthRepositoryImpl(
    private val auth : FirebaseAuth,
    private val database : FirebaseFirestore
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
                result.invoke(UIState.Success(Constants.SUCCESS))
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
        val document = database.collection(Constants.USER).document()
        user.id = document.id
        document
            .set(user)
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
}