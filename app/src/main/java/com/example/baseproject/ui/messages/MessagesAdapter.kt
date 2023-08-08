package com.example.baseproject.ui.messages

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.example.baseproject.databinding.ItemMessageTextSendBinding
import com.example.baseproject.models.Message

class MessagesAdapter : ListAdapter<Message, MessagesAdapter.MessagesViewHolder>(MessagesDiffCallback()) {
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MessagesViewHolder {
        val inflater = LayoutInflater.from(parent.context)
        return MessagesViewHolder(ItemMessageTextSendBinding.inflate(inflater, parent, false))
    }

    override fun onBindViewHolder(holder: MessagesViewHolder, position: Int) {
        val currentMessage = getItem(position)
        holder.bindData(currentMessage)
    }

    class MessagesViewHolder(private val binding: ItemMessageTextSendBinding) :
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