package com.example.baseproject.ui.home

import android.annotation.SuppressLint
import android.os.Bundle
import android.util.TypedValue
import android.view.*
import androidx.core.content.ContextCompat
import androidx.fragment.app.viewModels
import com.example.baseproject.R
import com.example.baseproject.databinding.FragmentHomeBinding
import com.example.baseproject.models.FragmentData
import com.example.baseproject.models.Friend
import com.example.baseproject.navigation.AppNavigation
import com.example.baseproject.ui.chats.ChatsFragment
import com.example.baseproject.ui.chats.ChatsViewModel
import com.example.baseproject.ui.friends.FriendsFragment
import com.example.baseproject.ui.friends.FriendsViewModel
import com.example.baseproject.ui.profile.ProfileFragment
import com.example.baseproject.utils.Constants
import com.example.core.base.fragment.BaseFragment
import com.google.firebase.auth.FirebaseAuth
import dagger.hilt.android.AndroidEntryPoint
import javax.inject.Inject
import kotlin.math.roundToInt


@AndroidEntryPoint
class HomeFragment : BaseFragment<FragmentHomeBinding, HomeViewModel>(R.layout.fragment_home) {

    @Inject
    lateinit var appNavigation: AppNavigation

    private val viewModel: HomeViewModel by viewModels()
    private val friendViewModel: FriendsViewModel by viewModels()
    private val chatViewModel: ChatsViewModel by viewModels()

    @SuppressLint("MissingInflatedId")
    override fun initView(savedInstanceState: Bundle?) {
        super.initView(savedInstanceState)

        // dem so tin nhan chua doc
        chatViewModel.chatListLiveData.observe(viewLifecycleOwner) { listChat ->
            var unreadChat = 0
            for (i in listChat) {
                if (i.messages?.last()?.sendId != FirebaseAuth.getInstance().currentUser?.uid) {
                    val unreadMessage = i.messages?.count { it.read == Constants.MESSAGE_UNREAD}
                    if (unreadMessage != null && unreadMessage > 0) {
                        unreadChat++
                    }
                }
            }
            if (unreadChat > 0) {
                val badge = binding.bottomNav.getOrCreateBadge(R.id.itChats)
                badge.isVisible = true
                badge.verticalOffset = TypedValue.applyDimension(
                    TypedValue.COMPLEX_UNIT_DIP,
                    4.0F, resources.displayMetrics
                ).roundToInt()
                badge.badgeTextColor = ContextCompat.getColor(requireContext(), R.color.white)
                badge.number = unreadChat
                badge.backgroundColor = ContextCompat.getColor(requireContext(), R.color.red)
            } else {
                val badge = binding.bottomNav.getOrCreateBadge(R.id.itChats)
                badge.isVisible = false
            }
        }

        // dem so loi moi ket ban
        friendViewModel.friendListLiveData.observe(viewLifecycleOwner) {
            val sendList: MutableList<Friend> = it.toMutableList().filter { friend -> (friend.status == Constants.STATE_RECEIVE) } as MutableList<Friend>
            if (sendList.size > 0) {
                val badge = binding.bottomNav.getOrCreateBadge(R.id.itFriends)
                badge.isVisible = true
                badge.verticalOffset = TypedValue.applyDimension(
                    TypedValue.COMPLEX_UNIT_DIP,
                    4.0F, resources.displayMetrics
                ).roundToInt()
                badge.badgeTextColor = ContextCompat.getColor(requireContext(), R.color.white)
                badge.number = sendList.size
                badge.backgroundColor = ContextCompat.getColor(requireContext(), R.color.red)
            }
        }
    }

    override fun getVM() = viewModel

    override fun bindingStateView() {
        super.bindingStateView()

        context?.let {
            val fragmentList: List<FragmentData> = listOf(
                FragmentData(ChatsFragment(), R.drawable.ic_chat, it.getString(R.string.message)),
                FragmentData(
                    FriendsFragment(),
                    R.drawable.ic_friend,
                    it.getString(R.string.friend)
                ),
                FragmentData(
                    ProfileFragment(),
                    R.drawable.ic_user_circle,
                    it.getString(R.string.profile)
                ),
            )
            binding.viewPager.adapter =
                activity?.let { PagerAdapter(childFragmentManager, lifecycle, fragmentList) }

            binding.viewPager.offscreenPageLimit = 1
            binding.viewPager.isUserInputEnabled = false
        }

        onClickNotificationToFriendScreen()

    }

    override fun setOnClick() {
        super.setOnClick()

        onClickViewPager()
    }

    private fun onClickViewPager() {
        context?.let {
            binding.bottomNav.setOnItemSelectedListener { item ->
                when(item.itemId) {
                    R.id.itChats -> binding.viewPager.currentItem = 0
                    R.id.itFriends -> binding.viewPager.currentItem = 1
                    R.id.itProfile -> binding.viewPager.currentItem = 2
                }
                true
            }
        }
    }

    private fun onClickNotificationToFriendScreen(){
        val bundle = activity?.intent?.extras
        if (bundle != null) {
            val type = bundle.getString(Constants.MESSAGE_TYPE)
            if (type == Constants.NOTIFICATION_TYPE_STATE_FRIEND) {
                val targetFragmentPosition = 1
                binding.viewPager.setCurrentItem(1, false)
                binding.bottomNav.menu.getItem(targetFragmentPosition).isChecked = true
            }
        }
    }
}