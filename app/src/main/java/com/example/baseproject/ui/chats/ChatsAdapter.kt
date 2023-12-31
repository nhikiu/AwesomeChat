package com.example.baseproject.ui.chats

import android.annotation.SuppressLint
import android.graphics.Typeface
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.baseproject.R
import com.example.baseproject.databinding.ItemChatBinding
import com.example.baseproject.models.Chat
import com.example.baseproject.utils.Constants
import com.example.baseproject.utils.DateTimeUtils
import com.example.core.utils.getPxFromDp
import com.google.firebase.auth.FirebaseAuth

class ChatsAdapter : ListAdapter<Chat, ChatsAdapter.ChatViewHolder>(ChatDiffCallback()) {
    private var onClickToMessage: OnClickToMessage? = null
    val unreadChat = mutableListOf<String>()
    private var unreadChatListener: UnreadChat? = null

    interface UnreadChat{
        fun unreadChatListener(unreadChat: MutableList<String>)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ChatViewHolder {
        val inflate = LayoutInflater.from(parent.context)
        return ChatViewHolder(ItemChatBinding.inflate(inflate, parent, false))
    }

    override fun onBindViewHolder(holder: ChatViewHolder, position: Int) {
        val currentChat = getItem(position)
        holder.bindData(currentChat)
    }

    interface OnClickToMessage {
        @SuppressLint("NotConstructor")
        fun onClickToMessage(id: String)
    }

    fun setOnClickListener(onClickListener: OnClickToMessage) {
        this.onClickToMessage = onClickListener
    }

    inner class ChatViewHolder(private val binding: ItemChatBinding) :
        RecyclerView.ViewHolder(binding.root) {

        @SuppressLint("ResourceAsColor")
        fun bindData(chat: Chat) {
            binding.tvUsername.text =
                if (chat.friendProfile.name.length < 23) chat.friendProfile.name else "${
                    chat.friendProfile.name.substring(
                        0,
                        10
                    )
                }..."
            val unread = chat.unread
            if (unread > 0 && chat.messages?.last()?.toId == FirebaseAuth.getInstance().currentUser?.uid) {
                if (!unreadChat.contains(chat.id)) unreadChat.add(chat.id)
                unreadChatListener?.unreadChatListener(unreadChat)
                binding.tvUnread.visibility = View.VISIBLE
                binding.tvUnread.text = if (unread > 9) "9+" else "$unread"
                binding.tvMessage.setTextColor(ContextCompat.getColor(itemView.context, R.color.black))
                binding.tvSendtime.setTextColor(ContextCompat.getColor(itemView.context, R.color.black))
                binding.tvMessage.setTypeface(null, Typeface.BOLD)
                binding.tvSendtime.setTypeface(null, Typeface.BOLD)
                val padding = itemView.context.getPxFromDp(3.0F)
                binding.frameAvatarBackground.setPadding(padding, padding, padding, padding)
            } else {
                if (unreadChat.contains(chat.id)) unreadChat.remove(chat.id)
                binding.tvUnread.visibility = View.GONE
                binding.tvMessage.setTextColor(ContextCompat.getColor(itemView.context, R.color.grey_393939))
                binding.tvSendtime.setTextColor(ContextCompat.getColor(itemView.context, R.color.grey_393939))
                binding.tvMessage.setTypeface(null, Typeface.NORMAL)
                binding.tvSendtime.setTypeface(null, Typeface.NORMAL)
                binding.frameAvatarBackground.setPadding(0, 0, 0, 0)
            }

            if (chat.friendProfile.avatar != null && chat.friendProfile.avatar.isNotEmpty() && chat.friendProfile.avatar != "null") {
                Glide.with(itemView.context).load(chat.friendProfile.avatar)
                    .error(R.drawable.ic_error)
                    .placeholder(R.drawable.ic_avatar_default)
                    .into(binding.ivAvatar)
            }

            val messages = chat.messages
            if (messages != null) {
                val lastMessage = messages[messages.size - 1]
                var lastMessageContent = ""

                // last message
                if (lastMessage.sendId == FirebaseAuth.getInstance().currentUser?.uid) {
                    lastMessageContent = itemView.context.resources.getString(R.string.you) + " "
                }
                lastMessageContent += when (lastMessage.type) {
                    Constants.TYPE_IMAGE -> {
                        itemView.context.resources.getString(R.string.sent_picture)
                    }
                    Constants.TYPE_STICKER -> {
                        itemView.context.resources.getString(R.string.sent_sticker)
                    }
                    else -> {
                        lastMessage.content
                    }
                }
                binding.tvMessage.text = if (lastMessageContent.length < 35) {
                    lastMessageContent
                } else {
                    "${lastMessageContent.substring(0, 32)}..."
                }

                // time send message
                val time =
                    DateTimeUtils.convertTimestampToDateTimeForLastMessage(messages[messages.size - 1].sendAt.toLong())
                if (time == Constants.IS_YESTERDAY) {
                    binding.tvSendtime.text = itemView.context.getText(R.string.is_yesterday)
                } else {
                    binding.tvSendtime.text = time
                }
            }

            // onclick to message screen
            itemView.setOnClickListener {
                var friendId = ""
                for (i in chat.id.split("-")) {
                    if (i != FirebaseAuth.getInstance().currentUser?.uid) {
                        friendId = i
                    }
                }
                onClickToMessage?.onClickToMessage(friendId)
            }
        }
    }

    class ChatDiffCallback : DiffUtil.ItemCallback<Chat>() {
        override fun areItemsTheSame(oldItem: Chat, newItem: Chat): Boolean {
            return oldItem.id == newItem.id
        }

        override fun areContentsTheSame(oldItem: Chat, newItem: Chat): Boolean {
            return oldItem == newItem
        }

    }

}