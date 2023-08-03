package com.example.baseproject.ui.friends.allFriend

import android.os.Bundle
import androidx.fragment.app.viewModels
import com.example.baseproject.R
import com.example.baseproject.databinding.FragmentAllFriendBinding
import com.example.baseproject.models.Friend
import com.example.core.base.fragment.BaseFragment
import dagger.hilt.android.AndroidEntryPoint
import com.example.baseproject.utils.Constants

@AndroidEntryPoint
class AllFriendFragment : BaseFragment<FragmentAllFriendBinding, AllFriendViewModel>(R.layout.fragment_all_friend) {

    private val viewModel: AllFriendViewModel by viewModels()

    override fun getVM() = viewModel

    private var allFriendAdapter: AllFriendAdapter? = null

    override fun initView(savedInstanceState: Bundle?) {
        super.initView(savedInstanceState)

        allFriendAdapter = AllFriendAdapter()

        binding.recyclerviewAllFriend.adapter = allFriendAdapter
    }

    override fun setOnClick() {
        super.setOnClick()

        allFriendAdapter?.setOnClickListener(
            object : AllFriendAdapter.OnClickListener{
                override fun onClickToMessage(friend: Friend) {

                }

                override fun onClickUnfriendToSending(friend: Friend) {
                    val newFriend = Friend(friend.id, friend.name, friend.avatar, Constants.STATE_SEND)
                    viewModel.updateFriendState(newFriend)
                }

                override fun onClickReceiveToConfirm(friend: Friend) {
                    val newFriend = Friend(friend.id, friend.name, friend.avatar, Constants.STATE_FRIEND)
                    viewModel.updateFriendState(newFriend)
                }

                override fun onClickSendingToCancel(friend: Friend) {
                    val newFriend = Friend(friend.id, friend.name, friend.avatar, Constants.STATE_UNFRIEND)
                    viewModel.updateFriendState(newFriend)
                }
            }
        )
    }

    override fun bindingStateView() {
        super.bindingStateView()

        viewModel.friendListLiveData.observe(viewLifecycleOwner) {
            allFriendAdapter?.submitList(it.toMutableList())
        }
    }
}