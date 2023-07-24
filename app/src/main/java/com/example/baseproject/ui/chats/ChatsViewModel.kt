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
            Message("1", "1", "2", System.currentTimeMillis(), "text", "xin chao")
        )),
        Chat("2", arrayListOf<Message>(
            Message("2", "1", "2", System.currentTimeMillis() - 24 * 60 * 60 * 1000, "text", "xin chao")
        )),
        Chat("3", arrayListOf<Message>(
            Message("1", "1", "2", System.currentTimeMillis() - 24 * 2 * 60 * 60 * 1000, "text", "xin chao")
        )),
        Chat("4", arrayListOf<Message>(
            Message("1", "1", "2", System.currentTimeMillis() - 86400000 * 3, "text", "xin chao")
        )),
        Chat("5", arrayListOf<Message>(
            Message("1", "1", "2", System.currentTimeMillis() - 86400000 * 4, "text", "xin chao")
        )),
        Chat("6", arrayListOf<Message>(
            Message("1", "1", "2", System.currentTimeMillis() - 86400000 * 5, "text", "xin chao")
        )),
        Chat("7", arrayListOf<Message>(
            Message("1", "1", "2", System.currentTimeMillis()  - 86400000 * 6, "text", "xin chao")
        )),
        Chat("8", arrayListOf<Message>(
            Message("1", "1", "2", System.currentTimeMillis()  - 86400000 * 7, "text", "xin chao")
        )),
        Chat("9", arrayListOf<Message>(
            Message("1", "1", "2", System.currentTimeMillis() - 86400000 * 8, "text", "xin chao")
        )),
        Chat("10", arrayListOf<Message>(
            Message("1", "1", "2", System.currentTimeMillis() - 86400000 * 9, "text", "xin chao")
        )),
        Chat("11", arrayListOf<Message>(
            Message("1", "1", "2", System.currentTimeMillis() - 86400000 * 10, "text", "xin chao")
        )),
        Chat("12", arrayListOf<Message>(
            Message("1", "1", "2", System.currentTimeMillis() - 86400000 * 11, "text", "xin chao")
        ))
    )


    private var _chatListLiveData: MutableLiveData<List<Chat>> = MutableLiveData()
    val chatListLiveData: LiveData<List<Chat>> get() = _chatListLiveData

    init {
        chatList.sortWith(compareBy<Chat> { it.messages?.get(it.messages.size - 1)?.sendAt }.reversed())
        _chatListLiveData.postValue(chatList)
    }

}