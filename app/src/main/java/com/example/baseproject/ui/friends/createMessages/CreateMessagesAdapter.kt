package com.example.baseproject.ui.friends.createMessages

import android.annotation.SuppressLint
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.CompoundButton
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.baseproject.R
import com.example.baseproject.databinding.ItemFriendBinding
import com.example.baseproject.models.Friend
import com.example.baseproject.ui.friends.FriendCallback

class CreateMessagesAdapter : ListAdapter<Friend, CreateMessagesAdapter.CreateMessagesViewHolder>(FriendCallback()){
    private var _selectedFriend: Friend? = null
    private var onSingleSelectedListener: OnSingleSelectedListener? = null

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): CreateMessagesViewHolder {
        val inflater = LayoutInflater.from(parent.context)
        return CreateMessagesViewHolder(ItemFriendBinding.inflate(inflater, parent, false))
    }

    override fun onBindViewHolder(holder: CreateMessagesViewHolder, position: Int) {
        val currentFriend = getItem(position)
        holder.bindData(currentFriend)
    }

    inner class CreateMessagesViewHolder(private val binding: ItemFriendBinding)
        : RecyclerView.ViewHolder(binding.root) {
        fun bindData(friend: Friend) {
            binding.btnDecline.visibility = View.GONE
            binding.btnReceiveToConfirm.visibility = View.GONE
            binding.btnUnfriendToSending.visibility = View.GONE
            binding.btnSendingToCancel.visibility = View.GONE
            binding.cbFriend.visibility = View.VISIBLE
            binding.bottomLine.visibility = View.VISIBLE

            binding.cbFriend.isChecked = _selectedFriend != null && _selectedFriend == friend

            binding.tvName.text = friend.name
            if (friend.avatar != null && friend.avatar.isNotEmpty()) {
                Glide.with(itemView.context).load(friend.avatar)
                    .error(R.drawable.ic_error)
                    .placeholder(R.drawable.ic_avatar_default)
                    .into(binding.ivAvatar)
            }

            binding.cbFriend.setOnCheckedChangeListener(object : CompoundButton.OnCheckedChangeListener{
                @SuppressLint("NotifyDataSetChanged")
                override fun onCheckedChanged(buttonView: CompoundButton?, isChecked: Boolean) {
                    val position = adapterPosition
                    if (isChecked && position != RecyclerView.NO_POSITION) {
                        _selectedFriend = getItem(position)
                        notifyDataSetChanged()
                        onSingleSelectedListener?.onSingleSelectedListener(_selectedFriend)

                    }
                }
            })
        }
    }

    interface OnSingleSelectedListener {
        fun onSingleSelectedListener(selectedFriend: Friend?)
    }

    fun setOnSingleSelectedListener(listener: OnSingleSelectedListener) {
        onSingleSelectedListener = listener
    }
}