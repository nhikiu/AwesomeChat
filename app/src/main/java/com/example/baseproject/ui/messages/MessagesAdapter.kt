package com.example.baseproject.ui.messages

import android.annotation.SuppressLint
import android.content.Context
import android.graphics.drawable.GradientDrawable
import android.util.TypedValue
import android.view.Gravity
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.baseproject.R
import com.example.baseproject.databinding.ItemMessageDayBinding
import com.example.baseproject.databinding.ItemMessageTextReceiveBinding
import com.example.baseproject.databinding.ItemMessageTextSendBinding
import com.example.baseproject.databinding.ItemMessageTimeBinding
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
        if (currentItem is String) {
            if (currentItem.contains("_")){
                return 0
            }
            return 1
        }
        if (currentItem is Message) {
            val currentUid = auth.currentUser?.uid
            if (currentItem.sendId == currentUid) {
                return 2
            }
        }
        return 3
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        val inflater = LayoutInflater.from(parent.context)
        return when (viewType) {
            0 -> MessageTimeSendViewHolder(ItemMessageTimeBinding.inflate(inflater, parent, false))
            1 -> MessageDaySendViewHolder(ItemMessageDayBinding.inflate(inflater, parent, false))
            2 -> MessagesTextSendViewHolder(
                ItemMessageTextSendBinding.inflate(
                    inflater,
                    parent,
                    false
                )
            )
            else -> MessagesTextReceiveViewHolder(
                ItemMessageTextReceiveBinding.inflate(
                    inflater,
                    parent,
                    false
                )
            )
        }

    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        val currentItem = getItem(position)
        when(holder) {
            is MessagesTextSendViewHolder -> holder.bindData(currentItem as Message, holder.itemView.context)
            is MessagesTextReceiveViewHolder -> holder.bindData(currentItem as Message, holder.itemView.context)
            is MessageTimeSendViewHolder -> holder.binData(currentItem as String)
            is MessageDaySendViewHolder -> holder.bindData(currentItem as String, holder.itemView.context)
        }
    }

    private class MessageDaySendViewHolder(private val binding: ItemMessageDayBinding)
        : RecyclerView.ViewHolder(binding.root) {
            fun bindData(day: String, context: Context) {
                binding.tvDay.text = when (day) {
                    Constants.IS_YESTERDAY -> context.resources.getString(R.string.is_yesterday)
                    Constants.IS_TODAY -> context.resources.getString(R.string.is_today)
                    else -> day
                }
            }
        }

    private class MessageTimeSendViewHolder(private val binding: ItemMessageTimeBinding)
        : RecyclerView.ViewHolder(binding.root){
            fun binData(userTime: String) {
                val sendId = userTime.split("_").first()
                val time = userTime.split("_").last()
                if (sendId == FirebaseAuth.getInstance().currentUser?.uid){
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
        fun bindData(message: Message, context: Context) {
            binding.tvSendtime.text = DateTimeUtils.convertTimestampToDateTime(message.sendAt.toLong())

            val gradientDrawable = GradientDrawable()
            val radius_30 = TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 30F, context.resources.displayMetrics)
            val radius_3 = TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 3F, context.resources.displayMetrics)
            val cornerRadii: FloatArray

            when (message.position) {
                Constants.POSITION_ONLY -> {
                    cornerRadii = floatArrayOf(radius_30, radius_30, radius_30, radius_30,
                        radius_30, radius_30, radius_30, radius_30
                    )
                }
                Constants.POSITION_FIRST -> {
                    cornerRadii = floatArrayOf(radius_30, radius_30, radius_30, radius_30,
                        radius_3, radius_3, radius_30, radius_30
                    )
                }
                Constants.POSITION_MIDDLE -> {
                    cornerRadii = floatArrayOf(radius_30, radius_30, radius_3, radius_3,
                        radius_3, radius_3, radius_30, radius_30
                    )
                }
                else -> {
                    cornerRadii = floatArrayOf(radius_30, radius_30, radius_3, radius_3,
                        radius_30, radius_30, radius_30, radius_30
                    )
                }
            }
            gradientDrawable.cornerRadii = cornerRadii
            binding.tvMessage.background = gradientDrawable
            binding.tvMessage.text = "${message.position} - ${message.content}"
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
        fun bindData(message: Message, context: Context) {
            binding.tvSendtime.text = DateTimeUtils.convertTimestampToDateTime(message.sendAt.toLong())
            val avatar = friendProfile?.avatar
            if (avatar != null && avatar.isNotEmpty() && (message.position == Constants.POSITION_FIRST || message.position == Constants.POSITION_ONLY)) {
                Glide.with(context).load(avatar)
                    .error(R.drawable.ic_error)
                    .placeholder(R.drawable.ic_avatar_default)
                    .into(binding.ivAvatar)
            } else if (message.position != Constants.POSITION_FIRST){
                binding.ivAvatar.visibility = View.INVISIBLE
            }

            val gradientDrawable = GradientDrawable()
            val radius_30 = TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 30F, context.resources.displayMetrics)
            val radius_3 = TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 3F, context.resources.displayMetrics)
            val cornerRadii: FloatArray

            when (message.position) {
                Constants.POSITION_ONLY -> {
                    cornerRadii = floatArrayOf(radius_30, radius_30, radius_30, radius_30,
                        radius_30, radius_30, radius_30, radius_30
                    )
                }
                Constants.POSITION_FIRST -> {
                    cornerRadii = floatArrayOf(radius_30, radius_30, radius_30, radius_30,
                        radius_30, radius_30, radius_3, radius_3
                    )
                }
                Constants.POSITION_MIDDLE -> {
                    cornerRadii = floatArrayOf(radius_3, radius_3, radius_30, radius_30,
                        radius_30, radius_30, radius_3, radius_3
                    )
                }
                else -> {
                    cornerRadii = floatArrayOf(radius_3, radius_3, radius_30, radius_30,
                        radius_30, radius_30, radius_30, radius_30
                    )
                }
            }
            gradientDrawable.cornerRadii = cornerRadii
            binding.tvMessage.background = gradientDrawable
            binding.tvMessage.text = "${message.position} - ${message.content}"
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
}