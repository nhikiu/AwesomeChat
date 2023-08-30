package com.example.baseproject.ui.chats

import android.annotation.SuppressLint
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.example.baseproject.models.Chat
import com.example.baseproject.models.Message
import com.example.baseproject.models.User
import com.example.baseproject.utils.Constants
import com.example.core.base.BaseViewModel
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.*
import javax.inject.Inject

@HiltViewModel
class ChatsViewModel @Inject constructor(
    private val database: FirebaseDatabase,
    private val auth: FirebaseAuth
) : BaseViewModel(){
    private var mChatList = arrayListOf<Chat>()
    private var mUserList = arrayListOf<User>()

    private var _chatListLiveData: MutableLiveData<List<Chat>> = MutableLiveData()
    val chatListLiveData: LiveData<List<Chat>> get() = _chatListLiveData

    private var _userListLiveData: MutableLiveData<List<User>> = MutableLiveData()

    private var _unreadMessage: MutableLiveData<Int> = MutableLiveData()

    private var _query: MutableLiveData<String> = MutableLiveData()
    val query get() = _query

    val actionChats = MutableLiveData<ActionState>()

    init {
        getAllUser()
    }

    fun setSearchQuery(query: String) {
        _query.value = query
        _chatListLiveData.value = mChatList.filter { chat ->
            var check = false
            if (chat.messages != null) {
                for (i in chat.messages) {
                    if (i.content.lowercase().contains(query.lowercase())) {
                        check = true
                    }
                }
            }
            check
        }
    }

    private fun getAllUser() {
        actionChats.value = ActionState.Loading
        val myRef = database.getReference(Constants.USER)
        myRef.addValueEventListener(object : ValueEventListener {
            @SuppressLint("SuspiciousIndentation")
            override fun onDataChange(snapshot: DataSnapshot) {
                mUserList.clear()
                for (dataSnapshot in snapshot.children) {
                    val profileRef = dataSnapshot.child(Constants.PROFILE)

                    val id = profileRef.child(Constants.USER_ID).getValue(String::class.java) ?: ""
                    val name = profileRef.child(Constants.USER_NAME).getValue(String::class.java) ?: ""
                    val email = profileRef.child(Constants.USER_EMAIL).getValue(String::class.java) ?: ""
                    val phoneNumber = profileRef.child(Constants.USER_PHONENUMBER).getValue(String::class.java) ?: ""
                    val dateOfBirth = profileRef.child(Constants.USER_DATE_OF_BIRTH).getValue(String::class.java) ?: ""
                    val avatar = profileRef.child(Constants.USER_AVATAR).getValue(String::class.java) ?: ""
                    val token = profileRef.child(Constants.USER_TOKEN).getValue(String::class.java) ?: ""

                    if (id != auth.currentUser?.uid) {
                        mUserList.add(User(id, name, email, phoneNumber, dateOfBirth, avatar, token))
                    }
                }
                _userListLiveData.value = mUserList
                getAllChat()
            }

            override fun onCancelled(error: DatabaseError) {
            }
        })
    }

    private fun getAllChat() {
        actionChats.value = ActionState.Loading

        val chatRef = database.getReference(Constants.CHATS)
        val uid = auth.currentUser?.uid.toString()

        chatRef.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                mChatList.clear()
                if (snapshot.exists()) {
                    for (dataSnapshot in snapshot.children) {
                        if (dataSnapshot.key.toString().contains(uid)) {
                            val chatId = dataSnapshot.key.toString()

                            val listMessage = arrayListOf<Message>()
                            for (i in dataSnapshot.children) {
                                val message = i.value as HashMap<*, *>
                                val id = message[Constants.MESSAGE_ID].toString()
                                val sendAt = message[Constants.MESSAGE_SEND_AT].toString()
                                val sendId = message[Constants.MESSAGE_SEND_ID].toString()
                                val read = message[Constants.MESSAGE_READ].toString()
                                val toId = message[Constants.MESSAGE_TO_ID].toString()
                                val content = message[Constants.MESSAGE_CONTENT].toString()
                                val type = message[Constants.MESSAGE_TYPE].toString()

                                listMessage.add(Message(
                                    id = id,
                                    sendId = sendId,
                                    sendAt = sendAt,
                                    toId = toId,
                                    read = read,
                                    content = content,
                                    type = type,
                                    position = Constants.POSITION_MIDDLE
                                ))
                            }


                            _unreadMessage.value = listMessage.count { it.read == Constants.MESSAGE_UNREAD }

                            var friendId = ""
                            for (i in chatId.split("-")){
                                if (i != uid) {
                                    friendId = i
                                }
                            }
                            val friendProfile = _userListLiveData.value?.first{it.id == friendId}
                            if (friendProfile != null)
                                mChatList.add(Chat(chatId, friendProfile, _unreadMessage.value ?: 0, listMessage))
                        }
                    }
                    val searchString: String = _query.value ?: ""
                    if (searchString != "") {
                        _chatListLiveData.value = mChatList.filter { chat ->
                            var check = false
                            if (chat.messages != null) {
                                for (i in chat.messages) {
                                    if (i.content.lowercase().contains(searchString.lowercase())) {
                                        check = true
                                    }
                                }
                            }
                            check
                        }
                    } else {
                        _chatListLiveData.value = mChatList
                    }

                    actionChats.value = ActionState.Finish
                }
                else {
                    actionChats.value = ActionState.Finish
                }
            }

            override fun onCancelled(error: DatabaseError) {
                actionChats.value = ActionState.Fail
            }

        })
    }

}

sealed class ActionState {
    object Finish : ActionState()

    object Loading : ActionState()

    object Fail : ActionState()
}