package com.example.baseproject.ui.messages

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.example.baseproject.models.Message
import com.example.baseproject.models.User
import com.example.baseproject.utils.Constants
import com.example.baseproject.utils.ValidationUtils
import com.example.core.base.BaseViewModel
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import com.google.firebase.database.ktx.getValue
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

@HiltViewModel
class MessagesViewModel @Inject constructor(
    private val database: FirebaseDatabase,
    private val auth: FirebaseAuth
): BaseViewModel() {
    private val _friendProfile: MutableLiveData<User> = MutableLiveData()
    val friendProfile: LiveData<User> get() = _friendProfile

    private val _chatId: MutableLiveData<String> = MutableLiveData()
    val chatId: LiveData<String> get() = _chatId

    private val mMessageList = arrayListOf<Message>()

    private var _messageListLiveData: MutableLiveData<List<Message>> = MutableLiveData()
    val messageListLiveData: LiveData<List<Message>> get() = _messageListLiveData

    fun getUserById(id: String) {
        val fromId = auth.currentUser?.uid.toString()
        val toId = id
        _chatId.value = ValidationUtils.validateChatId(fromId, toId)


        val userRef = id.let { database.getReference(Constants.USER).child(it).child(Constants.PROFILE) }

        userRef.addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                if (snapshot.exists()) {
                    val user = User(
                        id = id,
                        name = snapshot.child(Constants.USER_NAME).getValue<String?>() ?: "",
                        email = snapshot.child(Constants.USER_EMAIL).getValue<String?>() ?: "",
                        phoneNumber = snapshot.child(Constants.USER_PHONENUMBER).getValue<String?>() ?: "",
                        dateOfBirth = snapshot.child(Constants.USER_DATE_OF_BIRTH).getValue<String>() ?: "",
                        avatar = snapshot.child(Constants.USER_AVATAR).getValue<String>() ?: ""
                    )

                    _friendProfile.postValue(user)

                    }
            }

            override fun onCancelled(error: DatabaseError) {
            }
        })
    }

    fun getAllMessage() {
        val chatRef =  database.getReference(Constants.CHATS).child(_chatId.value.toString())
        chatRef?.addListenerForSingleValueEvent(object : ValueEventListener{
            override fun onDataChange(snapshot: DataSnapshot) {
                if (snapshot.exists()) {
                    for (dataSnapshot in snapshot.children) {

                        val id = dataSnapshot.child(Constants.MESSAGE_ID).getValue(String::class.java)
                        val sendAt = dataSnapshot.child(Constants.MESSAGE_SEND_AT).getValue(String::class.java)
                        val sendId = dataSnapshot.child(Constants.MESSAGE_SEND_ID).getValue(String::class.java)
                        val toId = dataSnapshot.child(Constants.MESSAGE_TO_ID).getValue(String::class.java)
                        val type = dataSnapshot.child(Constants.MESSAGE_TYPE).getValue(String::class.java)
                        val content = dataSnapshot.child(Constants.MESSAGE_CONTENT).getValue(String::class.java)
                        if (id != null && sendId != null && toId != null && sendAt != null && type != null && content != null) {
                            val message = Message(id, sendId, toId, sendAt, type, content)
                            mMessageList.add(message)
                        }
                    }
                    _messageListLiveData.value = mMessageList.sortedBy { it.sendAt }
                }
            }

            override fun onCancelled(error: DatabaseError) {

            }

        })
    }

    fun sendMessage(toId: String, text: String, type: String) {
        val sendId = auth.currentUser?.uid
        if (sendId != null) {
            val chatId = ValidationUtils.validateChatId(sendId, toId)
            val chatsRef = database.getReference(Constants.CHATS).child(chatId)

            val chatRef = chatsRef.push()
            chatRef.setValue(Message(chatRef.key.toString(), sendId, toId, System.currentTimeMillis().toString(), type, text))

        }
    }
}