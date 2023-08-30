package com.example.baseproject.ui.friends.createMessages

import android.annotation.SuppressLint
import android.os.Bundle
import android.view.View
import androidx.appcompat.widget.SearchView
import androidx.fragment.app.activityViewModels
import com.bumptech.glide.Glide
import com.example.baseproject.R
import com.example.baseproject.databinding.FragmentCreateMessagesBinding
import com.example.baseproject.models.Friend
import com.example.baseproject.navigation.AppNavigation
import com.example.baseproject.ui.friends.FriendsViewModel
import com.example.baseproject.utils.Constants
import com.example.core.base.fragment.BaseFragment
import dagger.hilt.android.AndroidEntryPoint
import javax.inject.Inject


@AndroidEntryPoint
class CreateMessagesFragment
    : BaseFragment<FragmentCreateMessagesBinding, FriendsViewModel> (R.layout.fragment_create_messages),
    CreateMessagesAdapter.OnSingleSelectedListener{
    @Inject
    lateinit var appNavigation: AppNavigation

    private val shareViewModel: FriendsViewModel by activityViewModels()
    private var _selectedFriend: Friend? = null

    override fun getVM() = shareViewModel

    private var friendAdapter: CreateMessagesAdapter? =null

    override fun initView(savedInstanceState: Bundle?) {
        super.initView(savedInstanceState)


        friendAdapter = CreateMessagesAdapter()
        binding.recyclerviewFriend.adapter = friendAdapter

        binding.recyclerviewFriend.post(object : Runnable{
            @SuppressLint("NotifyDataSetChanged")
            override fun run() {
                friendAdapter?.notifyDataSetChanged()
            }

        })
        friendAdapter?.setOnSingleSelectedListener(this)
    }

    override fun bindingStateView() {
        super.bindingStateView()

        shareViewModel.friendListLiveData.observe(viewLifecycleOwner){
            if (it.isEmpty()) {
                binding.fragmentNotFound.visibility = View.VISIBLE
            } else {
                binding.fragmentNotFound.visibility = View.GONE
            }
            val friendList = it.filter { friend -> friend.status == Constants.STATE_FRIEND }
            friendAdapter?.submitList(friendList)
        }

        binding.searchMessage.setOnQueryTextListener(object : SearchView.OnQueryTextListener{
            override fun onQueryTextSubmit(query: String?): Boolean {
                return false
            }

            override fun onQueryTextChange(newText: String?): Boolean {
                shareViewModel.setSearchQuery(newText ?: "")
                return true
            }

        })
    }

    override fun setOnClick() {
        super.setOnClick()

        binding.btnBack.setOnClickListener {
            appNavigation.navigateUp()
        }

        binding.btnNext.setOnClickListener {
                val bundle = Bundle()
                bundle.putString(Constants.USER_ID, _selectedFriend?.id)
                appNavigation.openCreateMessagesToMessaagesScreen(bundle)
        }

        binding.btnCancel.setOnClickListener {
            friendAdapter?.cleatSelectedFriend()
        }
    }

    override fun onSingleSelectedListener(selectedFriend: Friend?) {
        if (selectedFriend != null) {
            _selectedFriend = selectedFriend
            binding.linearChooseFriend.visibility = View.VISIBLE
        } else {
            binding.linearChooseFriend.visibility = View.GONE
        }
        if (selectedFriend?.avatar != null && selectedFriend.avatar.isNotEmpty()) {
            Glide.with(requireContext()).load(selectedFriend.avatar)
                .into(binding.ivAvatar)
        } else {
            Glide.with(requireContext()).load(R.drawable.ic_avatar_default)
                .into(binding.ivAvatar)
        }
    }
}