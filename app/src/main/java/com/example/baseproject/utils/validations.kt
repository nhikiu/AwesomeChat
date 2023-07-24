package com.example.baseproject.utils

import com.example.core.utils.StringUtils.isEmailValid

object ValidationUtils{
    fun validateName(name: String): String? {
        if (name.isEmpty()) {
            return Constants.NAME_REQUIRED
        }
        return null
    }

    fun validateEmail(email: String) : String?{
        if (email.isEmpty()) {
            return Constants.EMAIL_REQUIRED
        } else if (!email.isEmailValid()) {
            return Constants.EMAIL_INVALID
        }
        return null
    }

    fun validatePassword(password: String) : String? {
        val regex = "^(?=.*[a-z])(?=.*[A-Z])(?=.*[0-9]).+\$".toRegex()

        if (password.isEmpty()) {
            return Constants.PASSWORD_REQUIRED
        } else if (password.length < 8 || !regex.matches(password)) {
            return Constants.PASSWORD_INVALID
        }
        return null
    }
}