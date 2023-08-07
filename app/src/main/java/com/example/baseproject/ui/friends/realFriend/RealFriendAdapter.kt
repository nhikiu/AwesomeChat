package com.example.baseproject.ui.friends.realFriend

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.baseproject.R
import com.example.baseproject.databinding.ItemCharactorBinding
import com.example.baseproject.databinding.ItemFriendBinding
import com.example.baseproject.models.Friend
import com.example.baseproject.ui.friends.ItemCallback

class RealFriendAdapter : ListAdapter<Any, RecyclerView.ViewHolder>(ItemCallback()){

    override fun getItemViewType(position: Int): Int {
        if (getItem(position) is String) return 0
        return 1
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        val inflater = LayoutInflater.from(parent.context)
        if (viewType == 1) {
            return RealFriendViewHolder(ItemFriendBinding.inflate(inflater, parent, false))
        }
        return CharacterViewHolder(ItemCharactorBinding.inflate(inflater, parent, false))
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        val currentItem = getItem(position)
        holder.apply {
            when(holder) {
                is CharacterViewHolder -> holder.bindData(currentItem.toString())
                is RealFriendViewHolder -> {
                    holder.bindData(currentItem as Friend)
                }
            }
        }
    }

    class CharacterViewHolder(private val binding: ItemCharactorBinding)
        : RecyclerView.ViewHolder(binding.root) {
        fun bindData(character : String) {
            binding.tvCharactor.text = character
        }
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