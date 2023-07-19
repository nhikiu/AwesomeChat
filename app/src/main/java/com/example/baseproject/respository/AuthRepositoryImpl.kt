package com.example.baseproject.respository

import com.example.baseproject.models.User
import com.example.baseproject.utils.FirestoreCollection
import com.example.baseproject.utils.UIState
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseAuthInvalidCredentialsException
import com.google.firebase.auth.FirebaseAuthUserCollisionException
import com.google.firebase.auth.FirebaseAuthWeakPasswordException
import com.google.firebase.firestore.FirebaseFirestore
import java.lang.Exception

class AuthRepositoryImpl(
    val auth : FirebaseAuth,
    val database : FirebaseFirestore
) : AuthRepository {
    override fun loginUser(user: User, result: (UIState<String>) -> Unit) {

    }

    override fun signupUser(
        email: String,
        password: String,
        user: User,
        result: (UIState<String>) -> Unit
    ) {
        auth.createUserWithEmailAndPassword(email, password)
            .addOnCompleteListener {
                if (it.isSuccessful) {
                    updateUserInfor(user) {state ->
                        when(state) {
                            is UIState.Success -> {
                                result.invoke(
                                    UIState.Success("User sign up successfully!")
                                )
                            }
                            is UIState.Failure -> {
                                result.invoke(UIState.Failure(state.error))
                            }
                            else -> {}
                        }

                    }

                } else {
                    try {
                        throw it.exception ?: Exception("Invalid authentication")
                    } catch (e: FirebaseAuthUserCollisionException) {
                        result.invoke(UIState.Failure("Authentication failed, User is exist!"))
                    } catch (e: Exception) {
                        result.invoke(UIState.Failure(e.message))
                    }
                }

            }
            .addOnFailureListener {
                result.invoke(UIState.Failure(it.localizedMessage))
            }
    }

    override fun updateUserInfor(user: User, result: (UIState<String>) -> Unit) {
        val document = database.collection(FirestoreCollection.USER).document()
        user.id = document.id
        document
            .set(user)
            .addOnSuccessListener {
                result.invoke(UIState.Success("User has been updated successfully!"))
            }
            .addOnFailureListener {
                result.invoke(
                    UIState.Failure(it.localizedMessage)
                )
            }
    }
}