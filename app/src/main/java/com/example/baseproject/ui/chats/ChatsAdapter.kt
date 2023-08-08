package com.example.baseproject.ui.chats

import android.content.Context
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.example.baseproject.R
import com.example.baseproject.databinding.ItemChatBinding
import com.example.baseproject.models.Chat
import com.example.baseproject.utils.Constants
import com.example.baseproject.utils.DateTimeUtils

class ChatsAdapter : ListAdapter<Chat, ChatsAdapter.ChatViewHolder>(ChatDiffCallback()) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ChatViewHolder {
        val inflate = LayoutInflater.from(parent.context)
        return ChatViewHolder(ItemChatBinding.inflate(inflate, parent, false))
    }

    override fun onBindViewHolder(holder: ChatViewHolder, position: Int) {
        val currentChat = getItem(position)
        holder.bindData(currentChat, holder.itemView.context)
    }

    class ChatViewHolder(private val binding: ItemChatBinding) : RecyclerView.ViewHolder(binding.root) {
        fun bindData(chat: Chat, context: Context) {
            binding.tvUsername.text = chat.id
            val messages = chat.messages
            if (messages != null) {
                binding.tvMessage.text = messages[messages.size - 1].content
                val time = DateTimeUtils.convertTimestampToDateTime(messages[messages.size - 1].sendAt.toLong())
                if (time == Constants.IS_YESTERDAY) {
                    binding.tvSendtime.text = context.getText(R.string.is_yesterday)
                } else {
                    binding.tvSendtime.text = time
                }
            }
        }
    }

    class ChatDiffCallback: DiffUtil.ItemCallback<Chat>() {
        override fun areItemsTheSame(oldItem: Chat, newItem: Chat): Boolean {
            return oldItem.id == newItem.id
        }

        override fun areContentsTheSame(oldItem: Chat, newItem: Chat): Boolean {
            return oldItem == newItem
        }

    }

}