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

    init {
        mChatList.sortWith(compareBy<Chat> { it.messages?.get(it.messages.size - 1)?.sendAt }.reversed())

        getAllChat()
    }

    private fun getAllChat() {
        val chatRef = database.getReference(Constants.CHATS)
        chatRef.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                if (snapshot.exists()) {
                    for (dataSnapshot in snapshot.children) {
                        val chatId = dataSnapshot.key.toString()
                        val uid = auth.currentUser?.uid.toString()
                        val listMessage = arrayListOf<Message>()
                        for (i in dataSnapshot.children) {
                            val message = i.value as HashMap<*, *>
                            val id = message[Constants.MESSAGE_ID].toString()
                            val sendAt = message[Constants.MESSAGE_SEND_AT].toString()
                            val sendId = message[Constants.MESSAGE_SEND_ID].toString()
                            val toId = message[Constants.MESSAGE_TO_ID].toString()
                            val content = message[Constants.MESSAGE_CONTENT].toString()
                            val type = message[Constants.MESSAGE_TYPE].toString()

                            listMessage.add(Message(
                                id = id,
                                sendId = sendId,
                                sendAt = sendAt,
                                toId = toId,
                                content = content,
                                type = type,
                                position = Constants.POSITION_MIDDLE
                            ))
                        }

                        var friendId = ""
                        for (i in chatId.split("-")){
                            if (i != uid) {
                                friendId = i
                            }
                        }

                        val userRef = database.getReference(Constants.USER).child(friendId).child(Constants.PROFILE)
                        mChatList.clear()
                        userRef.addListenerForSingleValueEvent(object : ValueEventListener {
                            override fun onDataChange(snapshot: DataSnapshot) {
                                if (snapshot.exists()) {
                                    val friendProfile = User(
                                        id = friendId,
                                        name = snapshot.child(Constants.USER_NAME).value.toString(),
                                        email = snapshot.child(Constants.USER_EMAIL).value.toString(),
                                        phoneNumber = snapshot.child(Constants.USER_PHONENUMBER).value.toString(),
                                        dateOfBirth = snapshot.child(Constants.USER_DATE_OF_BIRTH).value.toString(),
                                        avatar = snapshot.child(Constants.USER_AVATAR).value.toString()
                                    )
                                    val chat = Chat(chatId, friendProfile, listMessage.sortedBy { it.sendAt })
                                    mChatList.add(chat)
                                }
                                _chatListLiveData.postValue(mChatList)
                            }

                            override fun onCancelled(error: DatabaseError) {
                            }
                        })

                    }

                }
            }

            override fun onCancelled(error: DatabaseError) {

            }

        })
    }

}