package com.example.baseproject.ui.chats

import android.os.Build
import androidx.annotation.RequiresApi
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
import javax.inject.Inject

@RequiresApi(Build.VERSION_CODES.N)
@HiltViewModel
class ChatsViewModel @Inject constructor(
    private val database: FirebaseDatabase,
    private val auth: FirebaseAuth
) : BaseViewModel(){
    private var mChatList = arrayListOf<Chat>()

    private var _chatListLiveData: MutableLiveData<List<Chat>> = MutableLiveData()
    val chatListLiveData: LiveData<List<Chat>> get() = _chatListLiveData

    private var _unreadMessage: MutableLiveData<Int> = MutableLiveData()
    val unreadMessage: LiveData<Int> get() = _unreadMessage

    val actionChats = MutableLiveData<ActionState>()

    init {
        mChatList.sortWith(compareBy<Chat> { it.messages?.get(it.messages.size - 1)?.sendAt }.reversed())

        getAllChat()
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

                            val userRef = database.getReference(Constants.USER).child(friendId).child(Constants.PROFILE)
                            userRef.addValueEventListener(object : ValueEventListener {
                                override fun onDataChange(snapshot: DataSnapshot) {
                                    if (snapshot.exists()) {
                                        val friendProfile = User(
                                            id = friendId,
                                            name = snapshot.child(Constants.USER_NAME).value.toString(),
                                            email = snapshot.child(Constants.USER_EMAIL).value.toString(),
                                            phoneNumber = snapshot.child(Constants.USER_PHONENUMBER).value.toString(),
                                            dateOfBirth = snapshot.child(Constants.USER_DATE_OF_BIRTH).value.toString(),
                                            avatar = snapshot.child(Constants.USER_AVATAR).value.toString(),
                                            token = snapshot.child(Constants.USER_TOKEN).value.toString()
                                        )

                                        val chat = Chat(chatId, friendProfile, listMessage.count { it.read == Constants.MESSAGE_UNREAD }, listMessage.sortedBy { it.sendAt })
                                        mChatList.add(chat)
                                        _chatListLiveData.value = mChatList
                                    }
                                    actionChats.value = ActionState.Finish
                                }

                                override fun onCancelled(error: DatabaseError) {
                                    actionChats.value = ActionState.Fail
                                }
                            })
                        }
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