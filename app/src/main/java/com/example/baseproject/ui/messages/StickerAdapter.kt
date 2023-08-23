package com.example.baseproject.ui.messages

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.baseproject.databinding.ItemImageGalleryBinding
import com.example.core.adapter.OnItemClickListener

class StickerAdapter : ListAdapter<Int, StickerAdapter.StickerViewHolder>(IntDiffCallback()) {
    private var onClickListener: OnItemClickListener? = null

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): StickerViewHolder {
        val inflater = LayoutInflater.from(parent.context)
        return StickerViewHolder(ItemImageGalleryBinding.inflate(inflater, parent, false))
    }

    override fun onBindViewHolder(holder: StickerViewHolder, position: Int) {
        val currentItem = getItem(position)
        holder.bindData(currentItem)
    }

    inner class StickerViewHolder(private val binding: ItemImageGalleryBinding) :
        RecyclerView.ViewHolder(binding.root) {
        fun bindData(identifier: Int) {
            Glide.with(itemView.context)
                .load(identifier)
                .into(binding.ivImage)
            itemView.setOnClickListener {
                onClickListener?.onItemClick(adapterPosition)
            }
        }

    }

    class IntDiffCallback : DiffUtil.ItemCallback<Int>() {
        override fun areItemsTheSame(oldItem: Int, newItem: Int): Boolean {
            return oldItem == newItem
        }

        override fun areContentsTheSame(oldItem: Int, newItem: Int): Boolean {
            return oldItem == newItem
        }
    }

    fun onClickItem(onClick: OnItemClickListener) {
        onClickListener = onClick
    }

}