package com.example.baseproject.ui.friends

import android.annotation.SuppressLint
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.widget.TextView
import androidx.appcompat.widget.SearchView
import androidx.core.content.ContextCompat
import androidx.fragment.app.activityViewModels
import androidx.fragment.app.viewModels
import com.example.baseproject.R
import com.example.baseproject.databinding.FragmentFriendsBinding
import com.example.baseproject.models.FragmentData
import com.example.baseproject.navigation.AppNavigation
import com.example.baseproject.ui.chats.ActionState
import com.example.baseproject.ui.friends.allFriend.AllFriendFragment
import com.example.baseproject.ui.friends.pendingFriend.PendingFriendFragment
import com.example.baseproject.ui.friends.realFriend.RealFriendFragment
import com.example.baseproject.utils.Constants
import com.example.core.base.fragment.BaseFragment
import com.google.android.material.tabs.TabLayout
import com.google.android.material.tabs.TabLayoutMediator
import dagger.hilt.android.AndroidEntryPoint
import javax.inject.Inject

@AndroidEntryPoint
class FriendsFragment :
    BaseFragment<FragmentFriendsBinding, FriendsViewModel>(R.layout.fragment_friends) {
    @Inject
    lateinit var appNavigation: AppNavigation

    private val viewModel: FriendsViewModel by viewModels()

    override fun getVM() = viewModel

    private val shareViewModel: FriendsViewModel by activityViewModels()

    override fun initView(savedInstanceState: Bundle?) {
        super.initView(savedInstanceState)
        val fragmentList: List<FragmentData> = listOf(
            FragmentData(RealFriendFragment(), null, resources.getString(R.string.friend)),
            FragmentData(AllFriendFragment(), null, resources.getString(R.string.all_friend)),
            FragmentData(
                PendingFriendFragment(),
                null,
                resources.getString(R.string.pending_friend)
            )
        )
        binding.viewPager.adapter =
            activity?.let { FriendsPagerAdapter(childFragmentManager, lifecycle, fragmentList) }

        binding.viewPager.offscreenPageLimit = 1
        binding.viewPager.isUserInputEnabled = false

        TabLayoutMediator(binding.tabLayout, binding.viewPager) { tab, position ->
            run {
                val customView =
                    LayoutInflater.from(tab.parent?.context).inflate(R.layout.item_tab_layout, null)
                val title = customView.findViewById<TextView>(R.id.tv_tab_title)
                val countNum = customView.findViewById<TextView>(R.id.tv_tab_count)
                var count: Int

                viewModel.friendListLiveData.observe(viewLifecycleOwner) {
                    count = it.filter { friend -> friend.status == Constants.STATE_RECEIVE }.size
                    if (position == 2 && count > 0) {
                        if (count > 9) {
                            countNum.text = "9+"
                        } else {
                            countNum.text = "$count"
                        }
                        countNum.visibility = View.VISIBLE
                    } else {
                        countNum.visibility = View.GONE
                    }
                }

                title.text = fragmentList[position].title
                tab.customView = customView

            }
        }.attach()

    }

    @SuppressLint("MissingInflatedId")
    override fun bindingStateView() {
        super.bindingStateView()

        viewModel.actionFriend.observe(viewLifecycleOwner) {
            when (it) {
                is ActionState.Loading -> binding.progressBar.visibility = View.VISIBLE
                else -> binding.progressBar.visibility = View.GONE
            }
        }

        binding.tabLayout
            .addOnTabSelectedListener(object : TabLayout.OnTabSelectedListener {
                @SuppressLint("SuspiciousIndentation")
                override fun onTabSelected(tab: TabLayout.Tab?) {
                    val tabTextView = tab?.customView?.findViewById<TextView>(R.id.tv_tab_title)
                    tabTextView?.setTextColor(
                        ContextCompat.getColor(
                            requireContext(),
                            R.color.primary_color
                        )
                    )
                }

                override fun onTabUnselected(tab: TabLayout.Tab?) {
                    val tabTextView = tab?.customView?.findViewById<TextView>(R.id.tv_tab_title)
                    tabTextView?.setTextColor(
                        ContextCompat.getColor(
                            requireContext(),
                            R.color.grey_999999
                        )
                    )
                }

                override fun onTabReselected(tab: TabLayout.Tab?) {
                }

            })

        val initialSelectedTab = binding.tabLayout.getTabAt(0)
        initialSelectedTab?.let {
            it.customView?.findViewById<TextView>(R.id.tv_tab_title)
                ?.setTextColor(ContextCompat.getColor(requireContext(), R.color.primary_color))
        }

        val queryTextListener: SearchView.OnQueryTextListener =
            object : SearchView.OnQueryTextListener {
                override fun onQueryTextSubmit(p0: String?): Boolean {
                    return false
                }

                override fun onQueryTextChange(p0: String?): Boolean {
                    shareViewModel.setSearchQuery(p0 ?: "")
                    return true
                }

            }

        binding.searchFriend.setOnQueryTextListener(queryTextListener)
    }
}

data class FcmNotificationData(
    val to: String,
    val notification: FcmNotification,
    val data: DataModel,
)

data class FcmNotification(
    val title: String,
    val body: String
)

data class DataModel(
    val priority: String?,
    val sound: String?,
    val content_available: Boolean?,
    val bodyText: String?,
    val title: String?,
    val fromIdUser: String,
)