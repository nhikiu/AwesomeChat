package com.example.baseproject.ui.friends

import androidx.recyclerview.widget.DiffUtil
import com.example.baseproject.models.Friend

class FriendCallback : DiffUtil.ItemCallback<Friend>() {
    override fun areItemsTheSame(oldItem: Friend, newItem: Friend): Boolean {
        return oldItem.id == newItem.id
    }

    override fun areContentsTheSame(oldItem: Friend, newItem: Friend): Boolean {
        return oldItem == newItem
    }

}