package com.example.baseproject.ui.friends.realFriend

import androidx.fragment.app.viewModels
import com.example.baseproject.R
import com.example.baseproject.databinding.FragmentRealFriendBinding
import com.example.baseproject.navigation.AppNavigation
import com.example.core.base.fragment.BaseFragment
import dagger.hilt.android.AndroidEntryPoint
import javax.inject.Inject

@AndroidEntryPoint
class RealFriendFragment : BaseFragment<FragmentRealFriendBinding, RealFriendViewModel>(R.layout.fragment_real_friend) {
    @Inject
    lateinit var appNavigation: AppNavigation

    private val viewModel: RealFriendViewModel by viewModels()

    override fun getVM() = viewModel

}