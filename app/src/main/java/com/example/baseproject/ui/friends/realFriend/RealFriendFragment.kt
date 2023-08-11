package com.example.baseproject.ui.friends.realFriend

import android.os.Bundle
import androidx.fragment.app.activityViewModels
import com.example.baseproject.R
import com.example.baseproject.databinding.FragmentRealFriendBinding
import com.example.baseproject.models.Friend
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

    private val shareViewModel: FriendsViewModel by activityViewModels()

    override fun getVM() = shareViewModel

    private val realFriendAdapter: RealFriendAdapter = RealFriendAdapter()

    override fun initView(savedInstanceState: Bundle?) {
        super.initView(savedInstanceState)

        binding.recyclerViewRealFriend.adapter = realFriendAdapter
    }

    override fun bindingStateView() {
        super.bindingStateView()

        shareViewModel.friendListLiveData.observe(viewLifecycleOwner) {
            val friendList = ListUtils.getListSortByName(it.toMutableList().filter { friend -> (friend.status == Constants.STATE_FRIEND)})
            realFriendAdapter.submitList(friendList)
        }
    }

    override fun setOnClick() {
        super.setOnClick()
        realFriendAdapter.setOnClickListener(object : RealFriendAdapter.OnClickListener{
            override fun onClickToMessage(friend: Friend) {
                val bundle = Bundle()
                bundle.putString(Constants.USER_ID, friend.id)
                appNavigation.openHomeToMessageScreen(bundle)
            }
        })
    }
}