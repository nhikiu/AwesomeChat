package com.example.baseproject.ui.friends.allFriend

import android.os.Bundle
import android.view.View
import androidx.fragment.app.viewModels
import com.example.baseproject.R
import com.example.baseproject.databinding.FragmentAllFriendBinding
import com.example.core.base.fragment.BaseFragment
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class AllFriendFragment : BaseFragment<FragmentAllFriendBinding, AllFriendViewModel>(R.layout.fragment_all_friend) {

    private val viewModel: AllFriendViewModel by viewModels()

    override fun getVM() = viewModel

    private var allFriendAdapter: AllFriendAdapter? = null

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        allFriendAdapter = AllFriendAdapter()
        binding.recyclerviewAllFriend.adapter = allFriendAdapter
        viewModel.friendListLiveData.observe(viewLifecycleOwner) {
            allFriendAdapter?.submitList(it.toMutableList())
        }
    }

    override fun bindingStateView() {
        super.bindingStateView()

    }
}