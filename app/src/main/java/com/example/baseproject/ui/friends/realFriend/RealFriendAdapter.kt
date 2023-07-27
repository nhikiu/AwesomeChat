package com.example.baseproject.ui.friends.realFriend

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.example.baseproject.databinding.ItemFriendRealBinding
import com.example.baseproject.models.Friend

class RealFriendAdapter : ListAdapter<Friend, RealFriendAdapter.RealFriendViewHolder>(RealFriendCallback()){

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RealFriendViewHolder {
        val inflater = LayoutInflater.from(parent.context)
        return RealFriendViewHolder(ItemFriendRealBinding.inflate(inflater, parent, false))
    }

    override fun onBindViewHolder(holder: RealFriendViewHolder, position: Int) {
        val currentFriend = getItem(position)
        holder.bindData(currentFriend)
    }

    class RealFriendViewHolder(private val binding: ItemFriendRealBinding)
        : RecyclerView.ViewHolder(binding.root) {
        fun bindData(friend: Friend) {
            binding.tvName.text = friend.name
        }
    }

    class RealFriendCallback : DiffUtil.ItemCallback<Friend>() {
        override fun areItemsTheSame(oldItem: Friend, newItem: Friend): Boolean {
            return oldItem.id == newItem.id
        }

        override fun areContentsTheSame(oldItem: Friend, newItem: Friend): Boolean {
            return oldItem == newItem
        }

    }
}