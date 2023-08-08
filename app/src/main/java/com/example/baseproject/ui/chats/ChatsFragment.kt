package com.example.baseproject.ui.chats

import android.os.Build
import android.os.Bundle
import android.view.View
import androidx.annotation.RequiresApi
import androidx.fragment.app.viewModels
import com.example.baseproject.R
import com.example.baseproject.databinding.FragmentChatsBinding
import com.example.baseproject.navigation.AppNavigation
import com.example.core.base.fragment.BaseFragment
import dagger.hilt.android.AndroidEntryPoint
import javax.inject.Inject

@AndroidEntryPoint
class ChatsFragment : BaseFragment<FragmentChatsBinding, ChatsViewModel>(R.layout.fragment_chats) {
    @Inject
    lateinit var appNavigation: AppNavigation

    private val viewModel: ChatsViewModel by viewModels()


    override fun getVM() = viewModel

    private var chatAdapter: ChatsAdapter? = null

    @RequiresApi(Build.VERSION_CODES.N)
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        chatAdapter = ChatsAdapter()
        binding.recyclerviewChats.adapter = chatAdapter
        viewModel.chatListLiveData.observe(viewLifecycleOwner) {
            chatAdapter?.submitList(it.toMutableList())
        }
    }

    override fun bindingStateView() {
        super.bindingStateView()
    }
}