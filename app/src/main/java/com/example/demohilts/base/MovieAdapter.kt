package com.example.demohilts.base

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.demohilts.data.entity.MovieSummary
import com.example.demohilts.databinding.ItemMovieHomeBinding
import com.example.demohilts.utils.Constants.baseImageUrl
import com.example.demohilts.utils.gone
import com.example.demohilts.utils.show
import javax.inject.Inject

class MovieAdapter @Inject constructor() :
    ListAdapter<MovieSummary, MovieVH>(MovieSummaryDiffCallback()) {

    var onClickListener: ((Int, String) -> Any)? = null

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MovieVH {
        val itemVB =
            ItemMovieHomeBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return MovieVH(itemVB, onClickListener)
    }

    override fun onBindViewHolder(holder: MovieVH, position: Int) {
        holder.renderData(position, getItem(position))
    }
}

class MovieVH(
    private val itemVB: ItemMovieHomeBinding,
    private val onClickListener: ((Int, String) -> Any)?
) : RecyclerView.ViewHolder(itemVB.root) {

    private var movie: MovieSummary? = null

    init {
        itemVB.imagePoster.setOnClickListener {
            movie?.id?.let {
                var type = movie?.media_type.toString()
                if (type == "null") type = "movie"
                onClickListener?.let { clickListener -> clickListener(it, type) }
            }
        }
    }

    fun renderData(pos: Int, data: MovieSummary) = with(itemVB) {
        Glide.with(itemView.context).load(baseImageUrl + data.backdrop_path).into(imagePoster)
        textName.text = data.title ?: "---"
        if (!data.name.isNullOrEmpty()) {
            textName.text = data.name
        }
        movie = data
        if (data.adult == true) textAdult.show() else textAdult.gone()
    }
}

class MovieSummaryDiffCallback : DiffUtil.ItemCallback<MovieSummary>() {

    override fun areContentsTheSame(oldItem: MovieSummary, newItem: MovieSummary) =
        oldItem.id == newItem.id

    override fun areItemsTheSame(oldItem: MovieSummary, newItem: MovieSummary) = oldItem === newItem
}
