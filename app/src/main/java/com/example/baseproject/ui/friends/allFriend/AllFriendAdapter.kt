package com.example.baseproject.ui.friends.allFriend

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.example.baseproject.databinding.ItemFriendBinding
import com.example.baseproject.models.Friend
import com.example.baseproject.utils.Constants

class AllFriendAdapter : ListAdapter<Friend, AllFriendAdapter.FriendViewHolder>(AllFriendCallback()){

    private var onClickListener: OnClickListener? = null

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): AllFriendAdapter.FriendViewHolder {
        val inflate = LayoutInflater.from(parent.context)
        return FriendViewHolder(ItemFriendBinding.inflate(inflate, parent, false))
    }

    override fun onBindViewHolder(holder: AllFriendAdapter.FriendViewHolder, position: Int) {
        val currentFriend = getItem(position)
        holder.bindData(currentFriend)
        holder.itemView.setOnClickListener {
            onClickListener?.onClickToMessage(currentFriend)
        }
    }

    fun setOnClickListener(onClickListener: OnClickListener) {
        this.onClickListener = onClickListener
    }

    interface OnClickListener {
        fun onClickToMessage(friend: Friend)

        fun onClickUnfriendToSending(friend: Friend)
        fun onClickReceiveToConfirm(friend: Friend)
        fun onClickSendingToCancel(friend: Friend)
    }

    inner class FriendViewHolder(private val binding: ItemFriendBinding)
        : RecyclerView.ViewHolder(binding.root) {
            fun bindData(friend: Friend) {
                binding.tvName.text = friend.name
                when (friend.status) {
                    Constants.STATE_UNFRIEND -> {
                        binding.btnUnfriendToSending.visibility = View.VISIBLE
                        binding.btnReceiveToConfirm.visibility = View.GONE
                        binding.btnSendingToCancel.visibility = View.GONE
                    }
                    Constants.STATE_SEND -> {
                        binding.btnUnfriendToSending.visibility = View.GONE
                        binding.btnReceiveToConfirm.visibility = View.GONE
                        binding.btnSendingToCancel.visibility = View.VISIBLE
                    }
                    Constants.STATE_RECEIVE -> {
                        binding.btnUnfriendToSending.visibility = View.GONE
                        binding.btnReceiveToConfirm.visibility = View.VISIBLE
                        binding.btnSendingToCancel.visibility = View.VISIBLE
                    }
                    Constants.STATE_FRIEND -> {
                        binding.btnUnfriendToSending.visibility = View.GONE
                        binding.btnReceiveToConfirm.visibility = View.GONE
                        binding.btnSendingToCancel.visibility = View.GONE
                    }
                }
                binding.btnSendingToCancel.setOnClickListener {
                    onClickListener?.onClickSendingToCancel(friend)
                }
                binding.btnUnfriendToSending.setOnClickListener {
                    onClickListener?.onClickUnfriendToSending(friend)
                }
                binding.btnReceiveToConfirm.setOnClickListener {
                    onClickListener?.onClickReceiveToConfirm(friend)
                }
            }
        }

    class AllFriendCallback : DiffUtil.ItemCallback<Friend>() {
        override fun areItemsTheSame(oldItem: Friend, newItem: Friend): Boolean {
            return oldItem.id == newItem.id
        }

        override fun areContentsTheSame(oldItem: Friend, newItem: Friend): Boolean {
            return oldItem == newItem
        }

    }

}

