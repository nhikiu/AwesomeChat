package com.example.baseproject.ui.messages

import android.annotation.SuppressLint
import android.content.Context
import android.util.Log
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
    private var onSelectedListener: OnSelectedListener? = null

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ImageGalleryViewHolder {
        val inflater = LayoutInflater.from(parent.context)
        return ImageGalleryViewHolder(ItemImageGalleryBinding.inflate(inflater, parent, false))
    }

    override fun onBindViewHolder(holder: ImageGalleryViewHolder, position: Int) {
        val currentImage = getItem(position)
        holder.bindData(currentImage, holder.itemView.context)
    }

    inner class ImageGalleryViewHolder(private val binding: ItemImageGalleryBinding) :
        RecyclerView.ViewHolder(binding.root) {

        @SuppressLint("NotifyDataSetChanged", "SetTextI18n")
        fun bindData(string: String, context: Context) {
            Glide.with(context).load(string)
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
                    Log.e("abc", "bindData: $selectedItems")
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

    interface OnSelectedListener {
        fun onSelectedItemChange(selected: MutableList<Int>)
    }

    fun setOnSelectedListener(listener: OnSelectedListener) {
        onSelectedListener = listener
    }

    @SuppressLint("NotifyDataSetChanged")
    fun clearSelectedItem() {
        selectedItems.clear()
        Log.e("abc", "clearSelectedItem: $selectedItems", )
        notifyDataSetChanged()
    }
}