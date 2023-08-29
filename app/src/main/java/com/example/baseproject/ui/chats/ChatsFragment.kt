package com.example.baseproject.ui.chats

import android.os.Build
import android.os.Bundle
import android.view.View
import androidx.annotation.RequiresApi
import androidx.appcompat.widget.SearchView
import androidx.fragment.app.viewModels
import com.example.baseproject.R
import com.example.baseproject.databinding.FragmentChatsBinding
import com.example.baseproject.navigation.AppNavigation
import com.example.baseproject.utils.Constants
import com.example.core.base.fragment.BaseFragment
import dagger.hilt.android.AndroidEntryPoint
import javax.inject.Inject

@AndroidEntryPoint
class ChatsFragment : BaseFragment<FragmentChatsBinding, ChatsViewModel>(R.layout.fragment_chats),
ChatsAdapter.UnreadChat{
    @Inject
    lateinit var appNavigation: AppNavigation

    private val viewModel: ChatsViewModel by viewModels()

    override fun getVM() = viewModel

    private var chatAdapter: ChatsAdapter? = null


    override fun initView(savedInstanceState: Bundle?) {
        super.initView(savedInstanceState)
        chatAdapter = ChatsAdapter()
        binding.recyclerviewChats.adapter = chatAdapter
    }

    @RequiresApi(Build.VERSION_CODES.N)
    override fun bindingStateView() {
        super.bindingStateView()

        viewModel.actionChats.observe(viewLifecycleOwner) {
            when (it) {
                is ActionState.Loading -> {
                    binding.fragmentNotFound.visibility = View.GONE
                    binding.progressBar.visibility = View.VISIBLE
                }
                else -> binding.progressBar.visibility = View.GONE
            }
        }

        viewModel.chatListLiveData.observe(viewLifecycleOwner) {
            val sortedList = it.sortedByDescending { chat -> chat.messages?.get(chat.messages.size - 1)?.sendAt }
            chatAdapter?.submitList(sortedList.toMutableList())

            if (sortedList.isEmpty() && viewModel.query.value?.isNotEmpty() == true) {
                binding.fragmentNotFound.visibility = View.VISIBLE
            } else {
                binding.fragmentNotFound.visibility = View.GONE
            }
        }

        if (viewModel.chatListLiveData.value == null) {
            binding.fragmentNotFound.visibility = View.VISIBLE
        }

        listenerSearchView()
    }

    override fun setOnClick() {
        super.setOnClick()
        chatAdapter?.setOnClickListener(object : ChatsAdapter.OnClickToMessage{
            override fun onClickToMessage(id: String) {
                val bundle = Bundle()
                bundle.putString(Constants.USER_ID, id)
                appNavigation.openHomeToMessageScreen(bundle)
            }
        })

        binding.btnCreateMessages.setOnClickListener {
            appNavigation.openHomeToCreateMessagesScreen()
        }
    }

    override fun unreadChatListener(unreadChat: MutableList<String>) {
//        Log.e("abc", "unreadChatListener: $unreadChat", )
    }

    private fun listenerSearchView(){
        val queryTextListener: SearchView.OnQueryTextListener =
            object : SearchView.OnQueryTextListener {
                override fun onQueryTextSubmit(p0: String?): Boolean {
                    return false
                }

                @RequiresApi(Build.VERSION_CODES.N)
                override fun onQueryTextChange(p0: String?): Boolean {
                    viewModel.setSearchQuery(p0 ?: "")
                    return true
                }

            }

        binding.searchMessage.setOnQueryTextListener(queryTextListener)
    }
}