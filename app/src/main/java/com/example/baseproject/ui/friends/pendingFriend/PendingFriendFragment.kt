package com.example.baseproject.ui.friends.pendingFriend

import android.os.Bundle
import android.view.View
import androidx.fragment.app.viewModels
import com.example.baseproject.R
import com.example.baseproject.databinding.FragmentPendingFriendBinding
import com.example.baseproject.navigation.AppNavigation
import com.example.baseproject.ui.friends.realFriend.RealFriendAdapter
import com.example.core.base.fragment.BaseFragment
import dagger.hilt.android.AndroidEntryPoint
import javax.inject.Inject

@AndroidEntryPoint
class PendingFriendFragment : BaseFragment<FragmentPendingFriendBinding, PendingFriendViewModel>(R.layout.fragment_pending_friend) {
    @Inject
    lateinit var appNavigation: AppNavigation

    private val viewModel: PendingFriendViewModel by viewModels()

    override fun getVM() = viewModel

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        val sendFriendAdapter = PendingFriendAdapter()

        binding.recyclerViewSendFriend.adapter = sendFriendAdapter

        viewModel.getSendRealFriend()

        viewModel.sendFriendListLiveData.observe(viewLifecycleOwner){
            sendFriendAdapter.submitList(it.toMutableList())
        }
    }

}