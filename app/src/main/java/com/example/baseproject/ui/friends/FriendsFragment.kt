package com.example.baseproject.ui.friends

import android.annotation.SuppressLint
import android.view.LayoutInflater
import android.view.View
import android.widget.TextView
import androidx.fragment.app.viewModels
import androidx.viewpager2.widget.ViewPager2
import com.example.baseproject.R
import com.example.baseproject.databinding.FragmentFriendsBinding
import com.example.baseproject.models.FragmentData
import com.example.baseproject.navigation.AppNavigation
import com.example.baseproject.ui.friends.allFriend.AllFriendFragment
import com.example.baseproject.ui.friends.pendingFriend.PendingFriendFragment
import com.example.baseproject.ui.friends.realFriend.RealFriendFragment
import com.example.core.base.fragment.BaseFragment
import com.google.android.material.tabs.TabLayoutMediator
import dagger.hilt.android.AndroidEntryPoint
import javax.inject.Inject

@AndroidEntryPoint
class FriendsFragment : BaseFragment<FragmentFriendsBinding, FriendsViewModel>(R.layout.fragment_friends){
    @Inject
    lateinit var appNavigation: AppNavigation

    private val viewModel: FriendsViewModel by viewModels()

    override fun getVM() = viewModel

    @SuppressLint("MissingInflatedId")
    override fun bindingStateView() {
        super.bindingStateView()
        val fragmentList: List<FragmentData> = listOf(
            FragmentData(RealFriendFragment(), null, resources.getString(R.string.friend)),
            FragmentData(AllFriendFragment(), null, resources.getString(R.string.all_friend)),
            FragmentData(PendingFriendFragment(), null, resources.getString(R.string.pending_friend))
        )
        binding.viewPager.adapter =
            activity?.let { FriendsPagerAdapter(childFragmentManager, lifecycle, fragmentList) }

        binding.viewPager.offscreenPageLimit = 1
        binding.viewPager.isUserInputEnabled = true

        TabLayoutMediator(binding.tabLayout, binding.viewPager) {
                tab, position ->
            run {
                val customView = LayoutInflater.from(tab.parent!!.context).inflate(R.layout.item_tab_layout, null)
                val title = customView.findViewById<TextView>(R.id.tv_tab_title)
                val countNum = customView.findViewById<TextView>(R.id.tv_tab_count)
                val count = 0
                if (position == 2) {
                    countNum.text = "$count"
                    countNum.visibility = View.VISIBLE
                }
                title.text = fragmentList[position].title
                tab.customView = customView
                binding.viewPager.registerOnPageChangeCallback(object : ViewPager2.OnPageChangeCallback() {
                    override fun onPageSelected(position: Int) {
                        super.onPageSelected(position)
                        val tabView = binding.tabLayout.getTabAt(position)?.customView
                        if (tabView != null) {
                            val tabTitleTextView = tabView.findViewById<TextView>(R.id.tv_tab_title)
                            // Đổi màu cho tab đang chọn
                            tabTitleTextView.setTextColor(resources.getColor(R.color.primary_color))
                        }
                    }
                })
            }
        }.attach()
    }
}