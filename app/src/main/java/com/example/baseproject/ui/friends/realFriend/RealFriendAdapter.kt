package com.example.baseproject.ui.friends.realFriend

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.baseproject.R
import com.example.baseproject.databinding.ItemFriendBinding
import com.example.baseproject.models.Friend
import com.example.baseproject.ui.friends.FriendCallback

class RealFriendAdapter : ListAdapter<Friend, RealFriendAdapter.RealFriendViewHolder>(FriendCallback()){

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RealFriendViewHolder {
        val inflater = LayoutInflater.from(parent.context)
        return RealFriendViewHolder(ItemFriendBinding.inflate(inflater, parent, false))
    }

    override fun onBindViewHolder(holder: RealFriendViewHolder, position: Int) {
        val currentFriend = getItem(position)
        holder.bindData(currentFriend)
    }

    inner class RealFriendViewHolder(private val binding: ItemFriendBinding) : RecyclerView.ViewHolder(binding.root) {
        fun bindData(friend: Friend) {
            binding.tvName.text = friend.name
            binding.btnUnfriendToSending.visibility = View.GONE
            binding.btnReceiveToConfirm.visibility = View.GONE
            binding.btnSendingToCancel.visibility = View.GONE
            if (friend.avatar != null && friend.avatar.isNotEmpty()){
                Glide.with(binding.root).load(friend.avatar)
                    .error(R.drawable.ic_error)
                    .placeholder(R.drawable.ic_avatar_default)
                    .into(binding.ivAvatar)
            }
        }
    }
}