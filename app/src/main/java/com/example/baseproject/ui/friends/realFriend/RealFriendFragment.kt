package com.example.baseproject.ui.friends.realFriend

import android.os.Bundle
import androidx.fragment.app.viewModels
import com.example.baseproject.R
import com.example.baseproject.databinding.FragmentRealFriendBinding
import com.example.baseproject.navigation.AppNavigation
import com.example.baseproject.ui.friends.FriendsViewModel
import com.example.baseproject.utils.Constants
import com.example.baseproject.utils.ListUtils
import com.example.core.base.fragment.BaseFragment
import dagger.hilt.android.AndroidEntryPoint
import javax.inject.Inject

@AndroidEntryPoint
class RealFriendFragment : BaseFragment<FragmentRealFriendBinding, FriendsViewModel>(R.layout.fragment_real_friend) {
    @Inject
    lateinit var appNavigation: AppNavigation

    private val shareViewModel: FriendsViewModel by viewModels()

    override fun getVM() = shareViewModel

    private val realFriendAdapter: RealFriendAdapter = RealFriendAdapter()

    override fun initView(savedInstanceState: Bundle?) {
        super.initView(savedInstanceState)

        binding.recyclerViewRealFriend.adapter = realFriendAdapter

        shareViewModel.getAllUser()
    }

    override fun bindingStateView() {
        super.bindingStateView()

        shareViewModel.friendListLiveData.observe(viewLifecycleOwner) {
            val friendList = ListUtils.getListSortByName(it.toMutableList().filter { friend -> (friend.status == Constants.STATE_FRIEND)})
            realFriendAdapter.submitList(friendList)
        }
    }
}