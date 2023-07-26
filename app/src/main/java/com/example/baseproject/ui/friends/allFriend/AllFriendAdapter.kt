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

class AllFriendAdapter : ListAdapter<Friend, RecyclerView.ViewHolder>(AllFriendCallback()){

    private var onClickListener: OnClickListener? = null


    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        val inflate = LayoutInflater.from(parent.context)
        Log.e("viewtype", "onCreateViewHolder: $viewType", )
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
            "UNFRIEND" -> 0
            "RECEIVE" -> 1
            "SENDING" -> 2
            "FRIEND" -> 3
            else -> 0
        }
    }

    fun setOnClickListener(onClickListener: OnClickListener) {
        this.onClickListener = onClickListener
    }

    interface OnClickListener {
        fun onClickToMessage(friend: Friend)

        fun onClickChange(friend: Friend, statusFriend: String)
    }

    inner class UnfriendFriendViewHolder(private val binding: ItemFriendUnfriendBinding)
        : RecyclerView.ViewHolder(binding.root) {
            fun bindData(friend: Friend) {
                binding.tvName.text = friend.name
                binding.btnAdd.setOnClickListener {
                    Log.d("database", "bindData:")
                    onClickListener?.onClickChange(friend, "UNFRIEND")
                    Log.d("database", "bindData: ")
                }
            }
        }

    class PendingReceiveFriendViewHolder(private val binding: ItemFriendPendingReceiveBinding)
        : RecyclerView.ViewHolder(binding.root) {
            fun bindData(friend: Friend) {
                binding.tvName.text = friend.name
            }
        }

    class PendingSendFriendViewHolder(private val binding: ItemFriendPendingSendBinding)
        : RecyclerView.ViewHolder(binding.root) {
            fun bindData(friend: Friend) {
                binding.tvName.text = friend.name
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

