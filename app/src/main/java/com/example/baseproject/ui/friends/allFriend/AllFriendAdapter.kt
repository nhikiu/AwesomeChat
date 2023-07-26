package com.example.baseproject.ui.friends.allFriend

import android.util.Log
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.example.baseproject.databinding.ItemFriendPendingReceiveBinding
import com.example.baseproject.databinding.ItemFriendPendingSendBinding
import com.example.baseproject.databinding.ItemFriendRealBinding
import com.example.baseproject.databinding.ItemFriendUnfriendBinding
import com.example.baseproject.models.Friend
import com.example.baseproject.utils.Constants

class AllFriendAdapter : ListAdapter<Friend, RecyclerView.ViewHolder>(AllFriendCallback()){

    private var onClickListener: OnClickListener? = null


    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        val inflate = LayoutInflater.from(parent.context)
        return when(viewType){
            0 -> UnfriendFriendViewHolder(ItemFriendUnfriendBinding.inflate(inflate, parent, false))
            1 -> PendingReceiveFriendViewHolder(ItemFriendPendingReceiveBinding.inflate(inflate, parent, false))
            2 -> PendingSendFriendViewHolder(ItemFriendPendingSendBinding.inflate(inflate, parent, false))
            3 -> RealFriendViewHolder(ItemFriendRealBinding.inflate(inflate, parent, false))
            else -> UnfriendFriendViewHolder(ItemFriendUnfriendBinding.inflate(inflate, parent, false))
        }
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        val currentFriend = getItem(position)
        holder.apply {
            when(holder){
                is UnfriendFriendViewHolder -> {
                    holder.bindData(currentFriend)
                }
                is PendingReceiveFriendViewHolder -> {
                    holder.bindData(currentFriend)
                }
                is PendingSendFriendViewHolder -> {
                    holder.bindData(currentFriend)
                }
                is RealFriendViewHolder -> {
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
            Constants.STATE_UNFRIEND -> 0
            Constants.STATE_RECEIVE -> 1
            Constants.STATE_SEND -> 2
            Constants.STATE_FRIEND -> 3
            else -> 0
        }
    }

    fun setOnClickListener(onClickListener: OnClickListener) {
        this.onClickListener = onClickListener
    }

    interface OnClickListener {
        fun onClickToMessage(friend: Friend)

        fun onClickChange(friend: Friend)
    }

    inner class UnfriendFriendViewHolder(private val binding: ItemFriendUnfriendBinding)
        : RecyclerView.ViewHolder(binding.root) {
            fun bindData(friend: Friend) {
                binding.tvName.text = friend.name
                binding.btnAdd.setOnClickListener {
                    onClickListener?.onClickChange(friend)
                }
            }
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

    class RealFriendViewHolder(private val binding: ItemFriendRealBinding)
        : RecyclerView.ViewHolder(binding.root) {
        fun bindData(friend: Friend) {
            binding.tvName.text = friend.name
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

