package com.example.baseproject.ui.friends

import android.annotation.SuppressLint
import androidx.recyclerview.widget.DiffUtil
import com.example.baseproject.models.Friend
import com.example.baseproject.models.Message

class FriendCallback : DiffUtil.ItemCallback<Friend>() {
        override fun areItemsTheSame(oldItem: Friend, newItem: Friend): Boolean {
        return oldItem.id == newItem.id
    }

    override fun areContentsTheSame(oldItem: Friend, newItem: Friend): Boolean {
        return oldItem == newItem
    }
}

class ItemCallback : DiffUtil.ItemCallback<Any>() {


    override fun areItemsTheSame(oldItem: Any, newItem: Any): Boolean {
        return when{
            oldItem is String && newItem is String -> oldItem == newItem
            oldItem is Friend && newItem is Friend -> oldItem.id == newItem.id
            oldItem is Message && newItem is Message -> oldItem.id == newItem.id
            else -> false
        }
    }

    @SuppressLint("DiffUtilEquals")
    override fun areContentsTheSame(oldItem: Any, newItem: Any): Boolean {
        return oldItem == newItem
    }

}