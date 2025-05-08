package com.irosinfo.feature.home.presentation.adapter

import android.annotation.SuppressLint
import android.content.Context
import android.graphics.Bitmap
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.irosinfo.databinding.ItemPreviewImageBinding
import com.utils.utils_module.CommonUtils.load

class PreviewCaptureImageAdapter(
    private val bitmapList: MutableList<Bitmap>, private val onItemClicked: (Bitmap) -> Unit
) : RecyclerView.Adapter<PreviewCaptureImageAdapter.ViewHolder>() {


    override fun onCreateViewHolder(
        parent: ViewGroup, viewType: Int
    ): PreviewCaptureImageAdapter.ViewHolder {
        val binding =
            ItemPreviewImageBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return ViewHolder(binding)
    }

    override fun getItemCount() = bitmapList.size

    override fun onBindViewHolder(
        viewHolder: PreviewCaptureImageAdapter.ViewHolder, position: Int
    ) {
        val item = bitmapList[position]
        viewHolder.bind(item = item)
    }

    inner class ViewHolder(private val binding: ItemPreviewImageBinding) :
        RecyclerView.ViewHolder(binding.root) {
        val context: Context = binding.root.context
        fun bind(item: Bitmap) {
            binding.setupViews(item = item)
        }

        private fun ItemPreviewImageBinding.setupViews(item: Bitmap) {
            profileIv.load(context = context, image = item)
            profileIv.setOnClickListener { onItemClicked(item) }
        }
    }

    @SuppressLint("NotifyDataSetChanged")
    fun resetPreviewCaptureAdapter() {
        bitmapList.clear()
        notifyDataSetChanged()
    }

    fun addBitmaps(newBitmaps: List<Bitmap>) {
        val startPosition = bitmapList.size
        bitmapList.addAll(newBitmaps)
        notifyItemRangeInserted(startPosition, newBitmaps.size)
    }
}