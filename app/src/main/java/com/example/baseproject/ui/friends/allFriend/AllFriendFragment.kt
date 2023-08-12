package com.example.baseproject.ui.friends.allFriend

import android.os.Bundle
import android.util.Log
import android.view.View
import androidx.fragment.app.activityViewModels
import com.example.baseproject.R
import com.example.baseproject.databinding.FragmentAllFriendBinding
import com.example.baseproject.models.Friend
import com.example.baseproject.navigation.AppNavigation
import com.example.baseproject.ui.friends.FriendsViewModel
import com.example.baseproject.utils.Constants
import com.example.baseproject.utils.DialogView
import com.example.baseproject.utils.ListUtils
import com.example.core.base.fragment.BaseFragment
import dagger.hilt.android.AndroidEntryPoint
import javax.inject.Inject

@AndroidEntryPoint
class AllFriendFragment :
    BaseFragment<FragmentAllFriendBinding, FriendsViewModel>(R.layout.fragment_all_friend) {
    @Inject
    lateinit var appNavigation: AppNavigation

    private val shareViewModel: FriendsViewModel by activityViewModels()

    override fun getVM() = shareViewModel

    private var allFriendAdapter: AllFriendAdapter? = null

    override fun initView(savedInstanceState: Bundle?) {
        super.initView(savedInstanceState)

        allFriendAdapter = AllFriendAdapter()

        binding.recyclerviewAllFriend.adapter = allFriendAdapter
    }

    override fun setOnClick() {
        super.setOnClick()

        allFriendAdapter?.setOnClickListener(
            object : AllFriendAdapter.OnClickListener {
                override fun onClickToMessage(friend: Friend) {
                    if (friend.status == Constants.STATE_FRIEND) {
                        val bundle = Bundle()
                        bundle.putString(Constants.USER_ID, friend.id)
                        appNavigation.openHomeToMessageScreen(bundle)
                    }
                }

                override fun onClickUnfriendToSending(friend: Friend) {
                    val newFriend =
                        Friend(friend.id, friend.name, friend.avatar, Constants.STATE_SEND)
                    shareViewModel.updateFriendState(newFriend)
                }

                override fun onClickReceiveToConfirm(friend: Friend) {
                    val newFriend =
                        Friend(friend.id, friend.name, friend.avatar, Constants.STATE_FRIEND)
                    shareViewModel.updateFriendState(newFriend)
                }

                override fun onClickSendingToCancel(friend: Friend) {
                    DialogView().showConfirmDialog(
                        activity,
                        resources.getString(R.string.change_title),
                        resources.getString(R.string.change_body)
                    ) {
                        val newFriend =
                            Friend(friend.id, friend.name, friend.avatar, Constants.STATE_UNFRIEND)
                        shareViewModel.updateFriendState(newFriend)
                    }
                }
            }
        )
    }

    override fun bindingStateView() {
        super.bindingStateView()

        shareViewModel.friendListLiveData.observe(viewLifecycleOwner){
            if (it.isEmpty()) {
                Log.e("abc", "EMPTY!!!!!", )
                binding.fragmentNotFound.visibility = View.VISIBLE
            } else {
                binding.fragmentNotFound.visibility = View.GONE
            }
            allFriendAdapter?.submitList(ListUtils.getListSortByName(it))
        }
    }
}