package com.example.baseproject.ui.friends

import androidx.fragment.app.viewModels
import com.example.baseproject.R
import com.example.baseproject.databinding.FragmentChatsBinding
import com.example.baseproject.navigation.AppNavigation
import com.example.baseproject.ui.chats.ChatsViewModel
import com.example.core.base.fragment.BaseFragment
import dagger.hilt.android.AndroidEntryPoint
import javax.inject.Inject

@AndroidEntryPoint
class FriendsFragment : BaseFragment<FragmentChatsBinding, FriendsViewModel>(R.layout.fragment_friends){
    @Inject
    lateinit var appNavigation: AppNavigation

    private val viewModel: FriendsViewModel by viewModels()

    override fun getVM() = viewModel
}