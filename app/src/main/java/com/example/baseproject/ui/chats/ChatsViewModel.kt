package com.example.baseproject.ui.chats

import android.os.Build
import androidx.annotation.RequiresApi
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.example.baseproject.models.Chat
import com.example.baseproject.models.Message
import com.example.core.base.BaseViewModel
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

@RequiresApi(Build.VERSION_CODES.N)
@HiltViewModel
class ChatsViewModel @Inject constructor() : BaseViewModel(){
    private var chatList = arrayListOf<Chat>(
        Chat("1", arrayListOf<Message>(
            Message("1", "1", "2", System.currentTimeMillis().toString(), "text", "xin chao")
        )),
        Chat("2", arrayListOf<Message>(
            Message("2", "1", "2", (System.currentTimeMillis() - 24 * 60 * 60 * 1000).toString(), "text", "xin chao")
        )),
        Chat("3", arrayListOf<Message>(
            Message("1", "1", "2", (System.currentTimeMillis() - 24 * 2 * 60 * 60 * 1000).toString(), "text", "xin chao")
        )),
        Chat("4", arrayListOf<Message>(
            Message("1", "1", "2", (System.currentTimeMillis() - 86400000 * 3).toString(), "text", "xin chao")
        )),
        Chat("5", arrayListOf<Message>(
            Message("1", "1", "2", (System.currentTimeMillis() - 86400000 * 4).toString(), "text", "xin chao")
        )),
    )


    private var _chatListLiveData: MutableLiveData<List<Chat>> = MutableLiveData()
    val chatListLiveData: LiveData<List<Chat>> get() = _chatListLiveData

    init {
        chatList.sortWith(compareBy<Chat> { it.messages?.get(it.messages.size - 1)?.sendAt }.reversed())
        _chatListLiveData.postValue(chatList)
    }

}