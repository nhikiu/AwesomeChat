package com.example.baseproject.utils

object Constants {
    // database realtime
    const val USER = "users"
    const val PROFILE = "profile"
    const val FRIEND = "friend"
    const val USER_NAME = "name"
    const val USER_EMAIL = "email"
    const val USER_PHONENUMBER = "phoneNumber"
    const val USER_DATE_OF_BIRTH = "dateOfBirth"
    const val USER_AVATAR = "avatar"
    const val USER_ID = "id"
    const val USER_STATUS = "status"
    const val USER_TOKEN = "token"

    const val ISLOGIN = "isLogIn"

    const val SUCCESS = "success"

    const val NAME_REQUIRED = "name_required"
    const val EMAIL_REQUIRED = "email_required"
    const val EMAIL_INVALID = "email_invalid"
    const val PASSWORD_REQUIRED = "password_required"
    const val PASSWORD_INVALID = "password_invalid"
    const val PHONE_NUMBER_INVALID = "phone_number_invalid"

    const val USER_NOT_FOUND = "user_not_found"
    const val EMAIL_PASSWORD_INVALID = "email_password_invalid"
    const val NETWORK_NOT_CONNECTION = "network_not_connection"
    const val TOO_MANY_REQUEST = "too_many_request"
    const val USER_EXIST = "user_is_exist"

    const val IS_YESTERDAY = "Yesterday"
    const val IS_TODAY = "Today"

    // friend state
    const val STATE_UNFRIEND = "unfriend"
    const val STATE_FRIEND = "friend"
    const val STATE_SEND = "send_friend"
    const val STATE_RECEIVE = "receive_friend"

    // chat
    const val CHATS = "chats"
    const val MESSAGE_ID = "id"
    const val MESSAGE_SEND_AT = "sendAt"
    const val MESSAGE_SEND_ID = "sendId"
    const val MESSAGE_TO_ID = "toId"
    const val MESSAGE_TYPE = "type"
    const val MESSAGE_CONTENT = "content"
    const val POSITION_FIRST = "first"
    const val POSITION_LAST = "last"
    const val POSITION_MIDDLE = "middle"
    const val POSITION_ONLY = "only"
    const val MESSAGE_READ = "read"
    const val MESSAGE_UNREAD = "unread"
    const val POSITION = "position"

    // message
    const val TYPE_IMAGE = "image"
    const val TYPE_TEXT = "text"
    const val TYPE_STICKER = "sticker"
    const val VIEW_TYPE_TIME_SEND = 0
    const val VIEW_TYPE_DAY_SEND = 1
    const val VIEW_TYPE_MESSAGE_TEXT_SEND = 2
    const val VIEW_TYPE_MESSAGE_TEXT_RECEIVE = 3
    const val VIEW_TYPE_MESSAGE_IMAGE_SEND = 4
    const val VIEW_TYPE_MESSAGE_IMAGE_RECEIVE = 5
    const val VIEW_TYPE_MESSAGE_STICKER_SEND = 6
    const val VIEW_TYPE_MESSAGE_STICKER_RECEIVE = 7

    // notification
    const val CHANNEL_ID = "CHAT_NOTIFICATION"
    const val GOOGLE_API = "https://fcm.googleapis.com/"
    const val CHANNEL_NAME = "CHAT_NOTIFICATION"
    const val NOTIFICATION_TYPE_STATE_FRIEND = "newFriend"
    const val NOTIFICATION_TYPE_NEW_MESSAGE = "newMessage"
    const val FROM_ID_USER = "fromIdUser"
    const val NOTIFICATION_DATA = "data"
}