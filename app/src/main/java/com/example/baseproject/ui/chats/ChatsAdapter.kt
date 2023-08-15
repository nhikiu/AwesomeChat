package com.example.baseproject.ui.chats

import android.annotation.SuppressLint
import android.content.Context
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

    interface OnClickToMessage {
        @SuppressLint("NotConstructor")
        fun onClickToMessage(id: String)
    }

    fun setOnClickListener(onClickListener: OnClickToMessage) {
        this.onClickToMessage = onClickListener
    }


    class ChatViewHolder(private val binding: ItemChatBinding) :
        RecyclerView.ViewHolder(binding.root) {

        @SuppressLint("ResourceAsColor")
        fun bindData(chat: Chat, context: Context) {
            binding.tvUsername.text =
                if (chat.friendProfile.name.length < 23) chat.friendProfile.name else "${
                    chat.friendProfile.name.substring(
                        0,
                        10
                    )
                }..."
            val unread = chat.unread
            if (unread > 0 && chat.messages?.last()?.toId == FirebaseAuth.getInstance().currentUser?.uid) {
                binding.tvUnread.visibility = View.VISIBLE
                binding.tvUnread.text = "$unread"
                binding.tvMessage.setTextColor(ContextCompat.getColor(context, R.color.black))
                binding.tvSendtime.setTextColor(ContextCompat.getColor(context, R.color.black))
                binding.tvMessage.setTypeface(null, Typeface.BOLD)
                binding.tvSendtime.setTypeface(null, Typeface.BOLD)
                val padding = context.getPxFromDp(3.0F)
                binding.frameAvatarBackground.setPadding(padding, padding, padding, padding)
            } else {
                binding.tvUnread.visibility = View.GONE
                binding.tvMessage.setTextColor(ContextCompat.getColor(context, R.color.grey_393939))
                binding.tvSendtime.setTextColor(ContextCompat.getColor(context, R.color.grey_393939))
                binding.tvMessage.setTypeface(null, Typeface.NORMAL)
                binding.tvSendtime.setTypeface(null, Typeface.NORMAL)
                binding.frameAvatarBackground.setPadding(0, 0, 0, 0)
            }

            if (chat.friendProfile.avatar != null && chat.friendProfile.avatar.isNotEmpty() && chat.friendProfile.avatar != "null") {
                Glide.with(context).load(chat.friendProfile.avatar)
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
                    lastMessageContent = context.resources.getString(R.string.you) + " "
                }
                lastMessageContent += if (lastMessage.type == Constants.TYPE_IMAGE) {
                    context.resources.getString(R.string.sent_picture)
                } else {
                    lastMessage.content
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
                    binding.tvSendtime.text = context.getText(R.string.is_yesterday)
                } else {
                    binding.tvSendtime.text = time
                }
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