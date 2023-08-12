package com.example.baseproject.ui.friends.createMessages

import android.os.Bundle
import android.util.Log
import android.view.View
import androidx.appcompat.widget.SearchView
import androidx.fragment.app.activityViewModels
import com.example.baseproject.R
import com.example.baseproject.databinding.FragmentCreateMessagesBinding
import com.example.baseproject.navigation.AppNavigation
import com.example.baseproject.ui.friends.FriendsViewModel
import com.example.baseproject.utils.Constants
import com.example.core.base.fragment.BaseFragment
import javax.inject.Inject


class CreateMessagesFragment : BaseFragment<FragmentCreateMessagesBinding, FriendsViewModel> (R.layout.fragment_create_messages) {
    @Inject
    lateinit var appNavigation: AppNavigation

    private val shareViewModel: FriendsViewModel by activityViewModels()

    override fun getVM() = shareViewModel

    private var friendAdapter: CreateMessagesAdapter? =null

    override fun initView(savedInstanceState: Bundle?) {
        super.initView(savedInstanceState)

        friendAdapter = CreateMessagesAdapter()
        binding.recyclerviewFriend.adapter = friendAdapter
    }

    override fun bindingStateView() {
        super.bindingStateView()

        shareViewModel.friendListLiveData.observe(viewLifecycleOwner){
            if (it.isEmpty()) {
                binding.fragmentNotFound.visibility = View.VISIBLE
            } else {
                binding.fragmentNotFound.visibility = View.GONE
            }
            friendAdapter?.submitList(it.filter { friend -> friend.status == Constants.STATE_FRIEND })
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

        friendAdapter?.mSelectedFriend?.observe(viewLifecycleOwner){
            Log.d("abc", "setOnClick() returned: $it")
        }
//        Log.e("abc", "setOnClick: $friend", )
//        if (friend != null) {
//            if (friend.avatar != null && friend.avatar.isNotEmpty()) {
//                Glide.with(this).load(friend.avatar)
//                    .error(R.drawable.ic_error)
//                    .placeholder(R.drawable.ic_avatar_default)
//                    .into(binding.ivAvatar)
//            }
//
//            binding.btnDelete.setOnClickListener {
//                friendAdapter?.selectedFriend = null
//            }
//
//            binding.btnNext.setOnClickListener {
//                val bundle = Bundle()
//                bundle.putString(Constants.USER_ID, friend.id)
//                appNavigation.openCreateMessagesToMessaagesScreen(bundle)
//            }
//        }
    }
}