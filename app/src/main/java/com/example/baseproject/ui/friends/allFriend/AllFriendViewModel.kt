package com.example.baseproject.ui.friends.allFriend

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.example.baseproject.models.User
import com.example.core.base.BaseViewModel
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

@HiltViewModel
class AllFriendViewModel @Inject constructor() : BaseViewModel(){
    private val friendList = arrayListOf<User>(
        User("123", "Nguyễn Văn A", "nguyenvana@gmail.com", "0866555888", null, null),
        User("124", "Nguyễn Văn B", "nguyenvanb@gmail.com", "0866555888", null, null),
        User("125", "Nguyễn Văn C", "nguyenvanc@gmail.com", "0866555888", null, null),
        User("126", "Nguyễn Văn D", "nguyenvand@gmail.com", "0866555888", null, null),
        User("127", "Nguyễn Văn E", "nguyenvane@gmail.com", "0866555888", null, null),
        User("128", "Nguyễn Văn F", "nguyenvanf@gmail.com", "0866555888", null, null),
        User("129", "Nguyễn Văn G", "nguyenvang@gmail.com", "0866555888", null, null),
        User("130", "Nguyễn Văn H", "nguyenvanh@gmail.com", "0866555888", null, null),
        User("131", "Nguyễn Văn I", "nguyenvani@gmail.com", "0866555888", null, null),
        User("132", "Nguyễn Văn K", "nguyenvank@gmail.com", "0866555888", null, null),
    )

    private var _friendListLiveData: MutableLiveData<List<User>> = MutableLiveData()
    val friendListLiveData: LiveData<List<User>> get() = _friendListLiveData

    init {
        _friendListLiveData.postValue(friendList)
    }
}