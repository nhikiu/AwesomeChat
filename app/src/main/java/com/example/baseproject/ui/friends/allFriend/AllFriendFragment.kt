package com.example.baseproject.ui.friends.allFriend

import android.os.Bundle
import androidx.fragment.app.viewModels
import com.example.baseproject.R
import com.example.baseproject.databinding.FragmentAllFriendBinding
import com.example.baseproject.models.Friend
import com.example.baseproject.navigation.AppNavigation
import com.example.baseproject.ui.friends.FriendsViewModel
import com.example.baseproject.utils.Constants
import com.example.baseproject.utils.DialogView
import com.example.baseproject.utils.ListUtils
import com.example.baseproject.utils.UIState
import com.example.core.base.fragment.BaseFragment
import dagger.hilt.android.AndroidEntryPoint
import javax.inject.Inject

@AndroidEntryPoint
class AllFriendFragment :
    BaseFragment<FragmentAllFriendBinding, FriendsViewModel>(R.layout.fragment_all_friend) {
    @Inject
    lateinit var appNavigation: AppNavigation

    private val shareViewModel: FriendsViewModel by viewModels()

    override fun getVM() = shareViewModel

    private var allFriendAdapter: AllFriendAdapter? = null

    override fun initView(savedInstanceState: Bundle?) {
        super.initView(savedInstanceState)

        allFriendAdapter = AllFriendAdapter()

        binding.recyclerviewAllFriend.adapter = allFriendAdapter

        shareViewModel.getAllUser()
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
                    shareViewModel.updateFriendState(newFriend) { state ->
                        when (state) {
                            is UIState.Loading -> {}
                            is UIState.Success -> {}
                            is UIState.Failure -> {
                                DialogView().showErrorDialog(
                                    activity,
                                    resources.getString(R.string.error),
                                    resources.getString(R.string.error_update)
                                )
                            }
                        }
                    }
                }

                override fun onClickReceiveToConfirm(friend: Friend) {
                    val newFriend =
                        Friend(friend.id, friend.name, friend.avatar, Constants.STATE_FRIEND)
                    shareViewModel.updateFriendState(newFriend) { state ->
                        when (state) {
                            is UIState.Loading -> {}
                            is UIState.Success -> {}
                            is UIState.Failure -> {
                                DialogView().showErrorDialog(
                                    activity,
                                    resources.getString(R.string.error),
                                    resources.getString(R.string.error_update)
                                )
                            }
                        }
                    }
                }

                override fun onClickSendingToCancel(friend: Friend) {
                    DialogView().showConfirmDialog(
                        activity,
                        resources.getString(R.string.change_title),
                        resources.getString(R.string.change_body)
                    ) {
                        val newFriend =
                            Friend(friend.id, friend.name, friend.avatar, Constants.STATE_UNFRIEND)
                        shareViewModel.updateFriendState(newFriend) { state ->
                            when (state) {
                                is UIState.Loading -> {}
                                is UIState.Success -> {}
                                is UIState.Failure -> {
                                    DialogView().showErrorDialog(
                                        activity,
                                        resources.getString(R.string.error),
                                        resources.getString(R.string.error_update),
                                    )
                                }
                            }
                        }
                    }
                }
            }
        )
    }

    override fun bindingStateView() {
        super.bindingStateView()

        shareViewModel.friendListLiveData.observe(viewLifecycleOwner) {
            allFriendAdapter?.submitList(ListUtils.getListSortByName(it.toMutableList()))
        }
    }
}