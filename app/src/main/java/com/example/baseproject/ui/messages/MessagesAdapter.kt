package com.example.baseproject.ui.messages

import android.annotation.SuppressLint
import android.graphics.drawable.Drawable
import android.graphics.drawable.GradientDrawable
import android.util.TypedValue
import android.view.Gravity
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.bumptech.glide.load.DataSource
import com.bumptech.glide.load.engine.GlideException
import com.bumptech.glide.request.RequestListener
import com.bumptech.glide.request.target.Target
import com.example.baseproject.R
import com.example.baseproject.databinding.*
import com.example.baseproject.models.Message
import com.example.baseproject.models.User
import com.example.baseproject.ui.friends.ItemCallback
import com.example.baseproject.utils.Constants
import com.example.baseproject.utils.DateTimeUtils
import com.google.firebase.auth.FirebaseAuth

class MessagesAdapter : ListAdapter<Any, RecyclerView.ViewHolder>(ItemCallback()) {
    private var friendProfile: User? = null
    private var currentUser: User? = null

    private val auth = FirebaseAuth.getInstance()

    fun getFriendProfile(friendProfile: User, currentUser: User) {
        this.friendProfile = friendProfile
        this.currentUser = currentUser
    }

    override fun getItemViewType(position: Int): Int {
        val currentItem = getItem(position)
        val currentUid = auth.currentUser?.uid

        if (currentItem is String) {
            if (currentItem.contains("_")) {
                return Constants.VIEW_TYPE_TIME_SEND
            }
            return Constants.VIEW_TYPE_DAY_SEND
        }
        if (currentItem is Message) {
            if (currentItem.sendId == currentUid) {
                if (currentItem.type == Constants.TYPE_TEXT) {
                    return Constants.VIEW_TYPE_MESSAGE_TEXT_SEND
                } else if (currentItem.type == Constants.TYPE_STICKER) {
                    return Constants.VIEW_TYPE_MESSAGE_STICKER_SEND
                }
                return Constants.VIEW_TYPE_MESSAGE_IMAGE_SEND
            } else {
                if (currentItem.type == Constants.TYPE_TEXT) {
                    return Constants.VIEW_TYPE_MESSAGE_TEXT_RECEIVE
                } else if (currentItem.type == Constants.TYPE_STICKER) {
                    return Constants.VIEW_TYPE_MESSAGE_STICKER_RECEIVE
                }
                return Constants.VIEW_TYPE_MESSAGE_IMAGE_RECEIVE
            }
        }
        return Constants.VIEW_TYPE_MESSAGE_TEXT_RECEIVE
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        val inflater = LayoutInflater.from(parent.context)
        return when (viewType) {
            Constants.VIEW_TYPE_TIME_SEND -> MessageTimeSendViewHolder(
                ItemMessageTimeBinding.inflate(
                    inflater, parent, false
                )
            )
            Constants.VIEW_TYPE_DAY_SEND -> MessageDaySendViewHolder(
                ItemMessageDayBinding.inflate(
                    inflater, parent, false
                )
            )
            Constants.VIEW_TYPE_MESSAGE_TEXT_SEND -> MessagesTextSendViewHolder(
                ItemMessageTextSendBinding.inflate(
                    inflater, parent, false
                )
            )
            Constants.VIEW_TYPE_MESSAGE_TEXT_RECEIVE -> MessagesTextReceiveViewHolder(
                ItemMessageTextReceiveBinding.inflate(
                    inflater, parent, false
                )
            )
            Constants.VIEW_TYPE_MESSAGE_IMAGE_SEND -> MessagesImageSendViewHolder(
                ItemMessageImageSendBinding.inflate(
                    inflater, parent, false
                )
            )
            Constants.VIEW_TYPE_MESSAGE_IMAGE_RECEIVE -> MessagesImageReceiveViewHolder(
                ItemMessageImageReceiveBinding.inflate(
                    inflater, parent, false
                )
            )
            Constants.VIEW_TYPE_MESSAGE_STICKER_SEND -> MessagesStickerSendViewHolder(
                ItemMessageStickerSendBinding.inflate(
                    inflater, parent, false
                )
            )
            else -> MessagesStickerReceiveViewHolder(
                ItemMessageStickerReceiveBinding.inflate(
                    inflater, parent, false
                )
            )
        }

    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        val currentItem = getItem(position)
        when (holder) {
            is MessagesTextSendViewHolder -> holder.bindData(currentItem as Message)
            is MessagesTextReceiveViewHolder -> holder.bindData(currentItem as Message)
            is MessageTimeSendViewHolder -> holder.binData(currentItem as String)
            is MessageDaySendViewHolder -> holder.bindData(currentItem as String)
            is MessagesImageSendViewHolder -> holder.bindData(currentItem as Message)
            is MessagesImageReceiveViewHolder -> holder.bindData(currentItem as Message)
            is MessagesStickerSendViewHolder-> holder.bindData(currentItem as Message)
            is MessagesStickerReceiveViewHolder -> holder.bindData(currentItem as Message)
        }
    }

    private class MessageDaySendViewHolder(private val binding: ItemMessageDayBinding) :
        RecyclerView.ViewHolder(binding.root) {
        fun bindData(day: String) {
            binding.tvDay.text = when (day) {
                Constants.IS_YESTERDAY -> itemView.context.resources.getString(R.string.is_yesterday)
                Constants.IS_TODAY -> itemView.context.resources.getString(R.string.is_today)
                else -> day
            }
        }
    }

    private class MessageTimeSendViewHolder(private val binding: ItemMessageTimeBinding) :
        RecyclerView.ViewHolder(binding.root) {
        fun binData(userTime: String) {
            val sendId = userTime.split("_").first()
            val time = userTime.split("_").last()
            if (sendId == FirebaseAuth.getInstance().currentUser?.uid) {
                binding.tvTime.gravity = Gravity.END
            } else {
                binding.tvTime.gravity = Gravity.START
            }
            binding.tvTime.text = time
        }
    }

    private class MessagesTextSendViewHolder(private val binding: ItemMessageTextSendBinding) :
        RecyclerView.ViewHolder(binding.root) {
        @SuppressLint("SetTextI18n")
        fun bindData(message: Message) {
            binding.tvSendtime.text =
                DateTimeUtils.convertTimestampToDateTime(message.sendAt.toLong())

            val gradientDrawable = GradientDrawable()
            val radius_30 = TypedValue.applyDimension(
                TypedValue.COMPLEX_UNIT_DIP, 30F, itemView.context.resources.displayMetrics
            )
            val radius_3 = TypedValue.applyDimension(
                TypedValue.COMPLEX_UNIT_DIP, 3F, itemView.context.resources.displayMetrics
            )
            val cornerRadii: FloatArray

            when (message.position) {
                Constants.POSITION_ONLY -> {
                    cornerRadii = floatArrayOf(
                        radius_30,
                        radius_30,
                        radius_30,
                        radius_30,
                        radius_30,
                        radius_30,
                        radius_30,
                        radius_30
                    )
                }
                Constants.POSITION_FIRST -> {
                    cornerRadii = floatArrayOf(
                        radius_30,
                        radius_30,
                        radius_30,
                        radius_30,
                        radius_3,
                        radius_3,
                        radius_30,
                        radius_30
                    )
                }
                Constants.POSITION_MIDDLE -> {
                    cornerRadii = floatArrayOf(
                        radius_30,
                        radius_30,
                        radius_3,
                        radius_3,
                        radius_3,
                        radius_3,
                        radius_30,
                        radius_30
                    )
                }
                else -> {
                    cornerRadii = floatArrayOf(
                        radius_30,
                        radius_30,
                        radius_3,
                        radius_3,
                        radius_30,
                        radius_30,
                        radius_30,
                        radius_30
                    )
                }
            }
            gradientDrawable.cornerRadii = cornerRadii
            binding.tvMessage.background = gradientDrawable
            binding.tvMessage.text = message.content
            if (message.position != Constants.POSITION_LAST) {
                binding.tvMessage.setOnClickListener {
                    if (binding.tvSendtime.visibility == View.VISIBLE) {
                        binding.tvSendtime.visibility = View.GONE
                    } else {
                        binding.tvSendtime.visibility = View.VISIBLE
                    }
                }
            }

        }
    }

    private inner class MessagesTextReceiveViewHolder(private val binding: ItemMessageTextReceiveBinding) :
        RecyclerView.ViewHolder(binding.root) {
        @SuppressLint("SetTextI18n")
        fun bindData(message: Message) {
            binding.tvSendtime.text =
                DateTimeUtils.convertTimestampToDateTime(message.sendAt.toLong())
            val avatar = friendProfile?.avatar
            if (avatar != null && avatar.isNotEmpty() && (message.position == Constants.POSITION_FIRST || message.position == Constants.POSITION_ONLY)) {
                Glide.with(itemView.context).load(avatar).error(R.drawable.ic_error)
                    .placeholder(R.drawable.ic_avatar_default).into(binding.ivAvatar)
            } else if (message.position != Constants.POSITION_FIRST && message.position != Constants.POSITION_ONLY) {
                binding.ivAvatar.visibility = View.INVISIBLE
            }

            val gradientDrawable = GradientDrawable()
            val radius_30 = TypedValue.applyDimension(
                TypedValue.COMPLEX_UNIT_DIP, 30F, itemView.context.resources.displayMetrics
            )
            val radius_3 = TypedValue.applyDimension(
                TypedValue.COMPLEX_UNIT_DIP, 3F, itemView.context.resources.displayMetrics
            )
            val cornerRadii: FloatArray

            when (message.position) {
                Constants.POSITION_ONLY -> {
                    cornerRadii = floatArrayOf(
                        radius_30,
                        radius_30,
                        radius_30,
                        radius_30,
                        radius_30,
                        radius_30,
                        radius_30,
                        radius_30
                    )
                }
                Constants.POSITION_FIRST -> {
                    cornerRadii = floatArrayOf(
                        radius_30,
                        radius_30,
                        radius_30,
                        radius_30,
                        radius_30,
                        radius_30,
                        radius_3,
                        radius_3
                    )
                }
                Constants.POSITION_MIDDLE -> {
                    cornerRadii = floatArrayOf(
                        radius_3,
                        radius_3,
                        radius_30,
                        radius_30,
                        radius_30,
                        radius_30,
                        radius_3,
                        radius_3
                    )
                }
                else -> {
                    cornerRadii = floatArrayOf(
                        radius_3,
                        radius_3,
                        radius_30,
                        radius_30,
                        radius_30,
                        radius_30,
                        radius_30,
                        radius_30
                    )
                }
            }
            gradientDrawable.cornerRadii = cornerRadii
            binding.tvMessage.background = gradientDrawable
            binding.tvMessage.text = message.content
            if (message.position != Constants.POSITION_LAST) {
                binding.tvMessage.setOnClickListener {
                    if (binding.tvSendtime.visibility == View.VISIBLE) {
                        binding.tvSendtime.visibility = View.GONE
                    } else {
                        binding.tvSendtime.visibility = View.VISIBLE
                    }
                }
            }
        }
    }

    private class MessagesImageSendViewHolder(private val binding: ItemMessageImageSendBinding) :
        RecyclerView.ViewHolder(binding.root) {
        fun bindData(message: Message) {
            binding.tvSendtime.text =
                DateTimeUtils.convertTimestampToDateTime(message.sendAt.toLong())
            Glide.with(itemView.context).load(message.content)
                .listener(object : RequestListener<Drawable> {
                    override fun onResourceReady(
                        resource: Drawable?,
                        model: Any?,
                        target: Target<Drawable>?,
                        dataSource: DataSource?,
                        isFirstResource: Boolean
                    ): Boolean {
                        binding.progressBar.visibility = View.GONE
                        return false
                    }

                    override fun onLoadFailed(
                        e: GlideException?,
                        model: Any?,
                        target: Target<Drawable>?,
                        isFirstResource: Boolean
                    ): Boolean {
                        binding.progressBar.visibility = View.GONE
                        return false
                    }
                }).into(binding.ivMessageImage)
        }
    }

    private inner class MessagesImageReceiveViewHolder(private val binding: ItemMessageImageReceiveBinding) :
        RecyclerView.ViewHolder(binding.root) {
        fun bindData(message: Message) {
            binding.tvSendtime.text =
                DateTimeUtils.convertTimestampToDateTime(message.sendAt.toLong())

            Glide.with(itemView.context).load(message.content)
                .listener(object : RequestListener<Drawable> {
                    override fun onResourceReady(
                        resource: Drawable?,
                        model: Any?,
                        target: Target<Drawable>?,
                        dataSource: DataSource?,
                        isFirstResource: Boolean
                    ): Boolean {
                        binding.progressBar.visibility = View.GONE
                        return false
                    }

                    override fun onLoadFailed(
                        e: GlideException?,
                        model: Any?,
                        target: Target<Drawable>?,
                        isFirstResource: Boolean
                    ): Boolean {
                        binding.progressBar.visibility = View.GONE
                        return false
                    }
                }).into(binding.ivMessageImage)

            binding.ivAvatar.visibility = View.VISIBLE
            if (friendProfile?.avatar != null && friendProfile?.avatar?.isNotEmpty() == true) {
                Glide.with(itemView.context).load(friendProfile?.avatar)
                    .listener(object : RequestListener<Drawable> {
                        override fun onResourceReady(
                            resource: Drawable?,
                            model: Any?,
                            target: Target<Drawable>?,
                            dataSource: DataSource?,
                            isFirstResource: Boolean
                        ): Boolean {
                            binding.progressBar.visibility = View.GONE
                            return false
                        }

                        override fun onLoadFailed(
                            e: GlideException?,
                            model: Any?,
                            target: Target<Drawable>?,
                            isFirstResource: Boolean
                        ): Boolean {
                            binding.progressBar.visibility = View.GONE
                            return false
                        }
                    }).into(binding.ivAvatar)
            }
        }
    }

    private class MessagesStickerSendViewHolder(private val binding: ItemMessageStickerSendBinding) :
        RecyclerView.ViewHolder(binding.root) {
        fun bindData(message: Message) {
            binding.tvSendtime.text =
                DateTimeUtils.convertTimestampToDateTime(message.sendAt.toLong())
            binding.ivMessageImage.setImageResource(message.content.toInt())
        }
    }

    private inner class MessagesStickerReceiveViewHolder(private val binding: ItemMessageStickerReceiveBinding) :
        RecyclerView.ViewHolder(binding.root) {
        fun bindData(message: Message) {
            binding.tvSendtime.text =
                DateTimeUtils.convertTimestampToDateTime(message.sendAt.toLong())

            binding.ivMessageImage.setImageResource(message.content.toInt())

            binding.ivAvatar.visibility = View.VISIBLE
            if (friendProfile?.avatar != null && friendProfile?.avatar?.isNotEmpty() == true) {
                Glide.with(itemView.context).load(friendProfile?.avatar)
                    .into(binding.ivAvatar)
            }
        }
    }

}