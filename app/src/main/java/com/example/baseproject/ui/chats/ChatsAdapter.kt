package com.example.baseproject.ui.chats

import android.annotation.SuppressLint
import android.content.Context
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.baseproject.R
import com.example.baseproject.databinding.ItemChatBinding
import com.example.baseproject.models.Chat
import com.example.baseproject.utils.Constants
import com.example.baseproject.utils.DateTimeUtils
import com.google.firebase.auth.FirebaseAuth

class ChatsAdapter : ListAdapter<Chat, ChatsAdapter.ChatViewHolder>(ChatDiffCallback()) {
    private var onClickToMessage: OnClickToMessage? = null

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ChatViewHolder {
        val inflate = LayoutInflater.from(parent.context)
        return ChatViewHolder(ItemChatBinding.inflate(inflate, parent, false))
    }

    override fun onBindViewHolder(holder: ChatViewHolder, position: Int) {
        val currentChat = getItem(position)
        holder.bindData(currentChat, holder.itemView.context)
        var friendId = ""
        for (i in currentChat.id.split("-")) {
            if (i != FirebaseAuth.getInstance().currentUser?.uid) {
                friendId = i
            }
        }
        holder.itemView.setOnClickListener {
            onClickToMessage?.onClickToMessage(friendId)
        }
    }

    interface OnClickToMessage{
        @SuppressLint("NotConstructor")
        fun onClickToMessage(id: String)
    }

    fun setOnClickListener(onClickListener: OnClickToMessage) {
        this.onClickToMessage = onClickListener
    }


    class ChatViewHolder(private val binding: ItemChatBinding) : RecyclerView.ViewHolder(binding.root) {
        fun bindData(chat: Chat, context: Context) {
            binding.tvUsername.text = chat.friendProfile.name

            if (chat.friendProfile.avatar != null && chat.friendProfile.avatar.isNotEmpty() && chat.friendProfile.avatar != "null") {
                Glide.with(context).load(chat.friendProfile.avatar)
                    .error(R.drawable.ic_error)
                    .placeholder(R.drawable.ic_avatar_default)
                    .into(binding.ivAvatar)
            }

            val messages = chat.messages
            if (messages != null) {
                binding.tvMessage.text = messages[messages.size - 1].content
                val time = DateTimeUtils.convertTimestampToDateTimeForLastMessage(messages[messages.size - 1].sendAt.toLong())
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