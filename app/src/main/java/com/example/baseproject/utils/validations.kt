package com.example.baseproject.utils

import android.annotation.SuppressLint
import android.content.Context
import com.example.baseproject.R
import com.example.core.utils.StringUtils.isEmailValid

@SuppressLint("StaticFieldLeak")
object ValidationUtils{
    private lateinit var context : Context

    fun init(context: Context) {
        this.context = context
    }

    fun validateEmail(email: String) : String?{
        if (email.isEmpty()) {
            return context.getString(R.string.email_required)
        } else if (!email.isEmailValid()) {
            return context.getString(R.string.email_error_body)
        }
        return null
    }

    fun validatePassword(password: String) : String? {
        val regex = "^(?=.*[a-z])(?=.*[A-Z])(?=.*[0-9]).+\$".toRegex()

        if (password.isEmpty()) {
            return context.getString(R.string.password_required)
        } else if (password.length < 8 || !regex.matches(password)) {
            return context.getString(R.string.password_error_body)
        }
        return null
    }
}