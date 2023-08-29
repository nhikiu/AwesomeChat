package com.example.baseproject.ui.messages

import android.annotation.SuppressLint
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.baseproject.databinding.ItemImageGalleryBinding

class GalleryAdapter : ListAdapter<String, GalleryAdapter.ImageGalleryViewHolder>(
    UriDiffCallback()
) {
    private val selectedItems = mutableListOf<Int>()
    private var onSelectedListener: OnMultiSelectedListener? = null

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ImageGalleryViewHolder {
        val inflater = LayoutInflater.from(parent.context)
        return ImageGalleryViewHolder(ItemImageGalleryBinding.inflate(inflater, parent, false))
    }

    override fun onBindViewHolder(holder: ImageGalleryViewHolder, position: Int) {
        val currentImage = getItem(position)
        holder.bindData(currentImage)
    }

    inner class ImageGalleryViewHolder(private val binding: ItemImageGalleryBinding) :
        RecyclerView.ViewHolder(binding.root) {

        @SuppressLint("NotifyDataSetChanged", "SetTextI18n")
        fun bindData(string: String) {
            Glide.with(itemView.context).load(string)
                .into(binding.ivImage)

            if (selectedItems.contains(adapterPosition)) {
                binding.tvChoose.text = "${selectedItems.indexOf(adapterPosition) + 1}"
                binding.tvChoose.visibility = View.VISIBLE

            } else {
                binding.tvChoose.visibility = View.GONE
            }

            itemView.setOnClickListener {
                val position = adapterPosition
                if (position != RecyclerView.NO_POSITION) {
                    if (selectedItems.contains(position)) {
                        selectedItems.remove(position)
                    } else {
                        selectedItems.add(position)
                    }
                    notifyDataSetChanged()
                    onSelectedListener?.onSelectedItemChange(selectedItems)
                }
            }
        }
    }

    class UriDiffCallback : DiffUtil.ItemCallback<String>() {
        override fun areItemsTheSame(oldItem: String, newItem: String): Boolean {
            return oldItem == newItem
        }

        override fun areContentsTheSame(oldItem: String, newItem: String): Boolean {
            return oldItem == newItem
        }

    }

    interface OnMultiSelectedListener {
        fun onSelectedItemChange(selected: MutableList<Int>)
    }

    fun setOnMultiSelectedListener(listener: OnMultiSelectedListener) {
        onSelectedListener = listener
    }

    @SuppressLint("NotifyDataSetChanged")
    fun clearSelectedItem() {
        selectedItems.clear()
        notifyDataSetChanged()
        onSelectedListener?.onSelectedItemChange(selectedItems)

    }
}