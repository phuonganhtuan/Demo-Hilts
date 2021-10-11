package com.example.demohilts.screens.detail.reviews

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import androidx.viewbinding.ViewBinding
import com.bumptech.glide.Glide
import com.example.demohilts.R
import com.example.demohilts.data.entity.Comment
import com.example.demohilts.databinding.ItemReviewBinding
import com.example.demohilts.databinding.ItemReviewRightBinding
import com.example.demohilts.utils.Constants.baseImageUrlSmall
import com.example.demohilts.utils.formatDateWithTZ
import javax.inject.Inject

class ReviewsAdapter @Inject constructor() :
    ListAdapter<Comment, CommentVH>(CommentDiffCallback()) {

    var onClickListener: ((Int) -> Any)? = null

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): CommentVH {
        if (viewType == 0) {
            val itemVB =
                ItemReviewBinding.inflate(LayoutInflater.from(parent.context), parent, false)
            return CommentLeftVH(itemVB, onClickListener)
        } else {
            val itemVB =
                ItemReviewRightBinding.inflate(LayoutInflater.from(parent.context), parent, false)
            return CommentRightVH(itemVB, onClickListener)
        }
    }

    override fun getItemViewType(position: Int) = position % 2

    override fun onBindViewHolder(holder: CommentVH, position: Int) {
        holder.renderData(position, getItem(position))
    }
}

abstract class CommentVH(itemVB: ViewBinding) : RecyclerView.ViewHolder(itemVB.root) {
    abstract fun renderData(pos: Int, data: Comment)
}

class CommentLeftVH(
    private val itemVB: ItemReviewBinding,
    private val onClickListener: ((Int) -> Any)?
) : CommentVH(itemVB) {

    override fun renderData(pos: Int, data: Comment) = with(itemVB) {
        Glide.with(itemView.context)
            .load(baseImageUrlSmall + data.author_details?.avatar_path)
            .placeholder(R.drawable.ic_person_place_holder)
            .into(imageAuthor)
        textName.text = data.author ?: "---"
        textTime.text = data.updated_at?.split("T")?.get(0) ?: "---"
        textContent.text = data.content ?: "---"
    }
}

class CommentRightVH(
    private val itemVB: ItemReviewRightBinding,
    private val onClickListener: ((Int) -> Any)?
) : CommentVH(itemVB) {

    override fun renderData(pos: Int, data: Comment) = with(itemVB) {
        Glide.with(itemView.context)
            .load(baseImageUrlSmall + data.author_details?.avatar_path)
            .placeholder(R.drawable.ic_person_place_holder)
            .into(imageAuthor)
        textName.text = data.author ?: "---"
        textTime.text = data.updated_at?.split("T")?.get(0) ?: "---"
        textContent.text = data.content ?: "---"
    }
}

class CommentDiffCallback : DiffUtil.ItemCallback<Comment>() {

    override fun areContentsTheSame(oldItem: Comment, newItem: Comment) =
        oldItem.id == newItem.id

    override fun areItemsTheSame(oldItem: Comment, newItem: Comment) = oldItem === newItem
}
