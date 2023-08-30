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
        val regex = "^(?=.*[a-z])(?=.*[A-Z])(?=.*\\d).+\$".toRegex()

        if (password.isEmpty()) {
            return Constants.PASSWORD_REQUIRED
        } else if (password.length < 8 || !regex.matches(password)) {
            return Constants.PASSWORD_INVALID
        }
        return null
    }

    fun validatePhoneNumber(phoneNumber: String) : String?{
        val regex = "^0\\d{9}$".toRegex()

        if (phoneNumber.isNotEmpty() && !regex.matches(phoneNumber)) {
            return Constants.PHONE_NUMBER_INVALID
        }
        return null
    }

    fun validateChatId(fromId: String, toId: String) : String{
        if (fromId.hashCode() <= toId.hashCode()) {
            return "$fromId-$toId"
        }
        return "$toId-$fromId"
    }
}