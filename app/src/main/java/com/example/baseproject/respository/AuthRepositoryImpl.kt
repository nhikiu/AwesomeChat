package com.example.baseproject.respository

import com.example.baseproject.models.User
import com.example.baseproject.utils.Constants
import com.example.baseproject.utils.UIState
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseAuthUserCollisionException
import com.google.firebase.firestore.FirebaseFirestore
import java.lang.Exception

class AuthRepositoryImpl(
    private val auth : FirebaseAuth,
    private val database : FirebaseFirestore
) : AuthRepository {
    override fun loginUser(email: String, password: String, result: (UIState<String>) -> Unit) {
        auth.signInWithEmailAndPassword(email, password)
            .addOnCompleteListener {
                if (it.isSuccessful) {
                    result.invoke(
                        UIState.Success("Log In success")
                    )
                }
                else {
                    try {
                        throw it.exception ?: Exception("Invalid authentication")
                    } catch (e: Exception) {
                        result.invoke(UIState.Failure(e.message))
                    }
                }
            }
            .addOnFailureListener {
                result.invoke(UIState.Failure(it.localizedMessage))
            }
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
        val document = database.collection(Constants.USER).document()
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

    override fun signoutUser(result: (UIState<String>) -> Unit) {
        auth.signOut()
        result.invoke(UIState.Success("Log out success"))
    }
}