package com.example.baseproject.ui.friends.pendingFriend

import android.os.Bundle
import androidx.fragment.app.viewModels
import com.example.baseproject.R
import com.example.baseproject.databinding.FragmentPendingFriendBinding
import com.example.baseproject.models.Friend
import com.example.baseproject.navigation.AppNavigation
import com.example.baseproject.utils.Constants
import com.example.core.base.fragment.BaseFragment
import dagger.hilt.android.AndroidEntryPoint
import javax.inject.Inject

@AndroidEntryPoint
class PendingFriendFragment : BaseFragment<FragmentPendingFriendBinding, PendingFriendViewModel>(R.layout.fragment_pending_friend) {
    @Inject
    lateinit var appNavigation: AppNavigation

    private val viewModel: PendingFriendViewModel by viewModels()

    override fun getVM() = viewModel

    private lateinit var sendFriendAdapter: PendingFriendAdapter
    private lateinit var receiveFriendAdapter: PendingFriendAdapter


    override fun initView(savedInstanceState: Bundle?) {
        super.initView(savedInstanceState)
        sendFriendAdapter = PendingFriendAdapter()
        receiveFriendAdapter = PendingFriendAdapter()

        binding.recyclerViewSendFriend.adapter = sendFriendAdapter
        binding.recyclerViewReceiveFriend.adapter = receiveFriendAdapter

        viewModel.getSendRealFriend()
        viewModel.getReceiveRealFriend()

        viewModel.sendFriendListLiveData.observe(viewLifecycleOwner){
            sendFriendAdapter.submitList(it.toMutableList())
        }

        viewModel.receiveFriendListLiveData.observe(viewLifecycleOwner){
            receiveFriendAdapter.submitList(it.toMutableList())
        }

    }

    override fun setOnClick() {
        super.setOnClick()

        receiveFriendAdapter.setOnClickListener(object : PendingFriendAdapter.OnClickListener{
            override fun onClickToMessage(friend: Friend) {

            }

            override fun onClickUnfriendToSending(friend: Friend) {
                friend.status = Constants.STATE_SEND
                viewModel.updateFriendState(friend)
            }

            override fun onClickReceiveToConfirm(friend: Friend) {
                friend.status = Constants.STATE_FRIEND
                viewModel.updateFriendState(friend)
            }

            override fun onClickSendingToCancel(friend: Friend) {
                friend.status = Constants.STATE_UNFRIEND
                viewModel.updateFriendState(friend)
            }

        })

        sendFriendAdapter.setOnClickListener(object : PendingFriendAdapter.OnClickListener{
            override fun onClickToMessage(friend: Friend) {

            }

            override fun onClickUnfriendToSending(friend: Friend) {
                friend.status = Constants.STATE_SEND
                viewModel.updateFriendState(friend)
            }

            override fun onClickReceiveToConfirm(friend: Friend) {
                friend.status = Constants.STATE_FRIEND
                viewModel.updateFriendState(friend)
            }

            override fun onClickSendingToCancel(friend: Friend) {
                friend.status = Constants.STATE_UNFRIEND
                viewModel.updateFriendState(friend)
            }

        })
    }

}