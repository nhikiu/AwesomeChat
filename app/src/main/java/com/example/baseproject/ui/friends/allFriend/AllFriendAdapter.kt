package com.example.baseproject.ui.friends.allFriend

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.example.baseproject.databinding.ItemFriendPendingReceiveBinding
import com.example.baseproject.databinding.ItemFriendPendingSendBinding
import com.example.baseproject.databinding.ItemFriendRealBinding
import com.example.baseproject.databinding.ItemFriendUnfriendBinding
import com.example.baseproject.models.User

class AllFriendAdapter : ListAdapter<User, RecyclerView.ViewHolder>(AllFriendCallback()){
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        val inflate = LayoutInflater.from(parent.context)
        return RealFriendViewHolder(ItemFriendPendingSendBinding.inflate(inflate, parent, false))
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        val currentUser = getItem(position)
        holder.apply {
            when(holder ){
                is RealFriendViewHolder -> holder.bindData(currentUser)
            }
        }
    }


    class RealFriendViewHolder(private val binding: ItemFriendPendingSendBinding) : RecyclerView.ViewHolder(binding.root) {
        fun bindData(user: User) {
            binding.tvName.text = user.name
        }
    }

    class AllFriendCallback : DiffUtil.ItemCallback<User>() {
        override fun areItemsTheSame(oldItem: User, newItem: User): Boolean {
            return oldItem.id == newItem.id
        }

        override fun areContentsTheSame(oldItem: User, newItem: User): Boolean {
            return oldItem == newItem
        }

    }

}

