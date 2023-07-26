package com.example.baseproject.ui.friends.allFriend

import android.os.Bundle
import android.util.Log
import android.view.View
import androidx.fragment.app.viewModels
import com.example.baseproject.R
import com.example.baseproject.databinding.FragmentAllFriendBinding
import com.example.baseproject.models.Friend
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
        //viewModel.getAllUser()
        viewModel.getAllFriend()
        viewModel.getUserById()
        viewModel.friendListLiveData.observe(viewLifecycleOwner) {
            allFriendAdapter?.submitList(it.toMutableList())
        }

        var count1 = 0
        var count2 = 0

        allFriendAdapter?.setOnClickListener(
            object : AllFriendAdapter.OnClickListener{

                override fun onClickToMessage(friend: Friend) {
                    count1++
                    Log.e("database", "onClickToMessage: ", )

                }

                override fun onClickChange(friend: Friend, statusFriend: String) {
                    Log.e("database", "Click to button", )
                    count2++
                    friend.status = "SENDING"
                    viewModel.updateFriendState(friend)
                }

            }

        )


    }

    override fun setOnClick() {
        super.setOnClick()


    }

    override fun bindingStateView() {
        super.bindingStateView()

    }
}