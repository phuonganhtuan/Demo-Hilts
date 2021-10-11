package com.example.demohilts.screens.detail

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.demohilts.databinding.ItemImageBinding
import com.example.demohilts.utils.Constants
import javax.inject.Inject

class ImageAdapter @Inject constructor() : RecyclerView.Adapter<ImageVH>() {

    var imageList = listOf<String>()

    override fun getItemCount() = imageList.size

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ImageVH {
        val itemVB = ItemImageBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return ImageVH(itemVB)
    }

    override fun onBindViewHolder(holder: ImageVH, position: Int) {
        holder.renderImage(imageList[position])
    }
}

class ImageVH(private val itemVB: ItemImageBinding) : RecyclerView.ViewHolder(itemVB.root) {

    fun renderImage(path: String) {
        //itemVB.image.setImageDrawable(null)
        Glide.with(itemView.context).load(Constants.baseImageUrl + path).into(itemVB.image)
    }
}
