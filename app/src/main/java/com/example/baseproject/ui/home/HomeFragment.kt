package com.example.baseproject.ui.home

import android.annotation.SuppressLint
import android.content.Context
import android.os.Bundle
import android.util.Log
import android.util.TypedValue
import android.view.*
import androidx.fragment.app.viewModels
import androidx.viewpager2.widget.ViewPager2
import com.example.baseproject.R
import com.example.baseproject.databinding.FragmentHomeBinding
import com.example.baseproject.models.FragmentData
import com.example.baseproject.navigation.AppNavigation
import com.example.baseproject.ui.chats.ChatsFragment
import com.example.baseproject.ui.friends.FriendsFragment
import com.example.baseproject.ui.profile.ProfileFragment
import com.example.baseproject.utils.Constants
import com.example.core.base.fragment.BaseFragment
import dagger.hilt.android.AndroidEntryPoint
import timber.log.Timber
import javax.inject.Inject


@AndroidEntryPoint
class HomeFragment : BaseFragment<FragmentHomeBinding, HomeViewModel>(R.layout.fragment_home) {

    @Inject
    lateinit var appNavigation: AppNavigation

    private val viewModel: HomeViewModel by viewModels()

    fun dpToPx(context: Context, dp: Int): Int {
        return Math.round(TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, dp.toFloat(), resources.displayMetrics))
    }

    @SuppressLint("MissingInflatedId")
    override fun initView(savedInstanceState: Bundle?) {
        super.initView(savedInstanceState)
        val sharePreferences = context?.getSharedPreferences(Constants.ISLOGIN, Context.MODE_PRIVATE)
        var isLogIn: Boolean
        sharePreferences?.let {
            isLogIn = it.getBoolean(Constants.ISLOGIN, false)
            if (isLogIn) {
                Log.e("abc", "Is Login: ${isLogIn}", )
            }
        }

//        val badge = binding.bottomNav.getOrCreateBadge(R.id.itFriends)
//        badge.isVisible = true
//        badge.verticalOffset = dpToPx(requireContext(), 3)
//        badge.badgeTextColor = resources.getColor(R.color.white)
//        badge.number = 12
//        badge.backgroundColor = resources.getColor(R.color.red)
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

            binding.viewPager.registerOnPageChangeCallback(object :
                ViewPager2.OnPageChangeCallback() {
                override fun onPageSelected(position: Int) {
                    binding.bottomNav.menu.getItem(position).isChecked = true
                }
            })
        }
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

    override fun onAttach(context: Context) {
        super.onAttach(context)
        Timber.tag("HomeFragment").d("A   " + "onAttach")
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        Timber.tag("HomeFragment").d("A   " + "onCreate")
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        Timber.tag("HomeFragment").d("A   " + "onCreateView")
        return super.onCreateView(inflater, container, savedInstanceState)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        Timber.tag("HomeFragment").d("A   " + "onViewCreated")
        super.onViewCreated(view, savedInstanceState)
    }

    override fun onStart() {
        Timber.tag("HomeFragment").d("A   " + "onStart")
        super.onStart()
    }

    override fun onResume() {
        Timber.tag("VietBH").d("A   " + "onResume")
        super.onResume()
    }

    override fun onPause() {
        Timber.tag("HomeFragment").d("A   " + "onPause")
        super.onPause()
    }

    override fun onStop() {
        Timber.tag("HomeFragment").d("A   " + "onStop")
        super.onStop()
    }

    override fun onDestroyView() {
        Timber.tag("HomeFragment").d("A   " + "onDestroyView")
        super.onDestroyView()
    }

    override fun onDestroy() {
        Timber.tag("HomeFragment").d("A   " + "onDestroy")
        super.onDestroy()
    }

    override fun onDetach() {
        Timber.tag("HomeFragment").d("A   " + "onCreate")
        super.onDetach()
    }


}