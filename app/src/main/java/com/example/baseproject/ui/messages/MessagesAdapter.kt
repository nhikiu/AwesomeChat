package com.example.baseproject.ui.messages

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.example.baseproject.databinding.ItemMessageTextReceiveBinding
import com.example.baseproject.databinding.ItemMessageTextSendBinding
import com.example.baseproject.models.Message
import com.google.firebase.auth.FirebaseAuth

class MessagesAdapter : ListAdapter<Message, RecyclerView.ViewHolder>(MessagesDiffCallback()) {

    private val auth = FirebaseAuth.getInstance()

    override fun getItemViewType(position: Int): Int {
        val currentMessage = getItem(position)
        val currentUid = auth.currentUser?.uid
        if (currentMessage.sendId == currentUid) {
            return 0
        }
        return 1
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        val inflater = LayoutInflater.from(parent.context)
        if (viewType == 0) {
            return MessagesSendViewHolder(ItemMessageTextSendBinding.inflate(inflater, parent, false))
        }
        return MessagesReceiveViewHolder(ItemMessageTextReceiveBinding.inflate(inflater, parent, false))
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        val currentMessage = getItem(position)
        when(holder) {
            is MessagesSendViewHolder -> holder.bindData(currentMessage)
            is MessagesReceiveViewHolder -> holder.bindData(currentMessage)
        }
    }

    class MessagesSendViewHolder(private val binding: ItemMessageTextSendBinding) :
        RecyclerView.ViewHolder(binding.root) {
        fun bindData(message: Message) {
            binding.tvMessage.text = message.content
        }
    }

    class MessagesReceiveViewHolder(private val binding: ItemMessageTextReceiveBinding) :
        RecyclerView.ViewHolder(binding.root) {
        fun bindData(message: Message) {
            binding.tvMessage.text = message.content
        }
    }

    class MessagesDiffCallback : DiffUtil.ItemCallback<Message>() {
        override fun areItemsTheSame(oldItem: Message, newItem: Message): Boolean {
            return oldItem.id == newItem.id
        }

        override fun areContentsTheSame(oldItem: Message, newItem: Message): Boolean {
            return oldItem == newItem
        }

    }
}