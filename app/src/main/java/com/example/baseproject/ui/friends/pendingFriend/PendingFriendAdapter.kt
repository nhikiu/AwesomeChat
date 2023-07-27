package com.example.baseproject.ui.friends.pendingFriend

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.example.baseproject.databinding.ItemFriendPendingReceiveBinding
import com.example.baseproject.databinding.ItemFriendPendingSendBinding
import com.example.baseproject.models.Friend
import com.example.baseproject.utils.Constants

class PendingFriendAdapter : ListAdapter<Friend, RecyclerView.ViewHolder>(PendingFriendCallback()){

    private var onClickListener: OnClickListener? = null

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        val inflate = LayoutInflater.from(parent.context)
        return when(viewType){
            0 -> PendingReceiveFriendViewHolder(ItemFriendPendingReceiveBinding.inflate(inflate, parent, false))
            else -> PendingSendFriendViewHolder(ItemFriendPendingSendBinding.inflate(inflate, parent, false))
        }
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        val currentFriend = getItem(position)
        holder.apply {
            when(holder){
                is PendingReceiveFriendViewHolder -> {
                    holder.bindData(currentFriend)
                }
                is PendingSendFriendViewHolder -> {
                    holder.bindData(currentFriend)
                }
            }
            itemView.setOnClickListener{
                onClickListener?.onClickToMessage(currentFriend)
            }
        }
    }

    override fun getItemViewType(position: Int): Int {
        return when(getItem(position).status) {
            Constants.STATE_RECEIVE -> 0
            else -> 1
        }
    }

    fun setOnClickListener(onClickListener: OnClickListener) {
        this.onClickListener = onClickListener
    }

    interface OnClickListener {
        fun onClickToMessage(friend: Friend)

        fun onClickChange(friend: Friend)
    }

    inner class PendingReceiveFriendViewHolder(private val binding: ItemFriendPendingReceiveBinding)
        : RecyclerView.ViewHolder(binding.root) {
        fun bindData(friend: Friend) {
            binding.tvName.text = friend.name
            binding.btnConfirm.setOnClickListener {
                onClickListener?.onClickChange(friend)
            }
        }
    }

    inner class PendingSendFriendViewHolder(private val binding: ItemFriendPendingSendBinding)
        : RecyclerView.ViewHolder(binding.root) {
        fun bindData(friend: Friend) {
            binding.tvName.text = friend.name
            binding.btnCancel.setOnClickListener {
                onClickListener?.onClickChange(friend)
            }
        }
    }

    class PendingFriendCallback : DiffUtil.ItemCallback<Friend>() {
        override fun areItemsTheSame(oldItem: Friend, newItem: Friend): Boolean {
            return oldItem.id == newItem.id
        }

        override fun areContentsTheSame(oldItem: Friend, newItem: Friend): Boolean {
            return oldItem == newItem
        }

    }


}