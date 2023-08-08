package com.example.baseproject.ui.friends.pendingFriend

import android.os.Bundle
import androidx.core.view.isVisible
import androidx.fragment.app.viewModels
import androidx.recyclerview.widget.ItemTouchHelper
import androidx.recyclerview.widget.RecyclerView
import com.example.baseproject.R
import com.example.baseproject.databinding.FragmentPendingFriendBinding
import com.example.baseproject.models.Friend
import com.example.baseproject.navigation.AppNavigation
import com.example.baseproject.ui.friends.FriendsViewModel
import com.example.baseproject.utils.Constants
import com.example.core.base.fragment.BaseFragment
import dagger.hilt.android.AndroidEntryPoint
import javax.inject.Inject

@AndroidEntryPoint
class PendingFriendFragment : BaseFragment<FragmentPendingFriendBinding, FriendsViewModel>(R.layout.fragment_pending_friend) {
    @Inject
    lateinit var appNavigation: AppNavigation

    private val shareViewModel: FriendsViewModel by viewModels()

    override fun getVM() = shareViewModel

    private lateinit var sendFriendAdapter: PendingFriendAdapter
    private lateinit var receiveFriendAdapter: PendingFriendAdapter

    override fun initView(savedInstanceState: Bundle?) {
        super.initView(savedInstanceState)
        sendFriendAdapter = PendingFriendAdapter()
        receiveFriendAdapter = PendingFriendAdapter()

        binding.recyclerViewSendFriend.adapter = sendFriendAdapter
        binding.recyclerViewReceiveFriend.adapter = receiveFriendAdapter

        shareViewModel.getAllUser()

        shareViewModel.friendListLiveData.observe(viewLifecycleOwner) {
            val sendList: MutableList<Friend> = it.toMutableList().filter { friend -> (friend.status == Constants.STATE_SEND) } as MutableList<Friend>
            sendFriendAdapter.submitList(sendList)
        }

        shareViewModel.friendListLiveData.observe(viewLifecycleOwner) {
            val receiveList: MutableList<Friend> = it.toMutableList().filter { friend -> (friend.status == Constants.STATE_RECEIVE) } as MutableList<Friend>
            receiveFriendAdapter.submitList(receiveList)
        }
    }

    override fun setOnClick() {
        super.setOnClick()

        onClickReceiveFriend()

        onClickSendingFriend()
    }

    private fun onClickReceiveFriend() {
        receiveFriendAdapter.setOnClickListener(object : PendingFriendAdapter.OnClickListener{

            override fun onClickUnfriendToSending(friend: Friend) {
                friend.status = Constants.STATE_SEND
                shareViewModel.updateFriendState(friend){

                }
            }

            override fun onClickReceiveToConfirm(friend: Friend) {
                friend.status = Constants.STATE_FRIEND
                shareViewModel.updateFriendState(friend){

                }
            }

            override fun onClickSendingToCancel(friend: Friend) {
                friend.status = Constants.STATE_UNFRIEND
                shareViewModel.updateFriendState(friend){

                }
            }

            override fun onClickReceiveToUnfriend(friend: Friend) {
                friend.status = Constants.STATE_UNFRIEND
                shareViewModel.updateFriendState(friend){

                }
            }
        })

        val itemTouchHelperCallback = object : ItemTouchHelper.SimpleCallback(
            0, ItemTouchHelper.LEFT
        ) {
            override fun onMove(
                recyclerView: RecyclerView,
                viewHolder: RecyclerView.ViewHolder,
                target: RecyclerView.ViewHolder
            ): Boolean {
                return false
            }

            override fun onSwiped(viewHolder: RecyclerView.ViewHolder, direction: Int) {
                val itemViewHolder = viewHolder as PendingFriendAdapter.PendingFriendViewHolder
                itemViewHolder.btnDecline.isVisible = true
            }
        }

        val itemTouchHelper = ItemTouchHelper(itemTouchHelperCallback)
        itemTouchHelper.attachToRecyclerView(binding.recyclerViewReceiveFriend)

    }

    private fun onClickSendingFriend() {
        sendFriendAdapter.setOnClickListener(object : PendingFriendAdapter.OnClickListener{

            override fun onClickUnfriendToSending(friend: Friend) {
                friend.status = Constants.STATE_SEND
                shareViewModel.updateFriendState(friend){

                }
            }

            override fun onClickReceiveToConfirm(friend: Friend) {
                friend.status = Constants.STATE_FRIEND
                shareViewModel.updateFriendState(friend){

                }
            }

            override fun onClickSendingToCancel(friend: Friend) {
                friend.status = Constants.STATE_UNFRIEND
                shareViewModel.updateFriendState(friend){

                }
            }

            override fun onClickReceiveToUnfriend(friend: Friend) {

            }
        })
    }
}