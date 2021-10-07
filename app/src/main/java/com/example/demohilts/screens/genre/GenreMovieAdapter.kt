package com.example.demohilts.screens.genre

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.demohilts.base.MovieSummaryDiffCallback
import com.example.demohilts.base.MovieVH
import com.example.demohilts.data.entity.MovieSummary
import com.example.demohilts.databinding.ItemMovieBinding
import com.example.demohilts.databinding.ItemMovieHomeBinding
import com.example.demohilts.utils.Constants.baseImageUrl
import javax.inject.Inject

class GenreMovieAdapter @Inject constructor() :
    ListAdapter<MovieSummary, GenreMovieVH>(MovieSummaryDiffCallback()) {

    var onClickListener: ((Int) -> Any)? = null

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): GenreMovieVH {
        val itemVB =
            ItemMovieBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return GenreMovieVH(itemVB, onClickListener)
    }

    override fun onBindViewHolder(holder: GenreMovieVH, position: Int) {
        holder.renderData(position, getItem(position))
    }
}

class GenreMovieVH(
    private val itemVB: ItemMovieBinding,
    private val onClickListener: ((Int) -> Any)?
) : RecyclerView.ViewHolder(itemVB.root) {

    private var movie: MovieSummary? = null

    init {
        itemVB.imagePoster.setOnClickListener {
            movie?.id?.let {
                onClickListener?.let { clickListener -> clickListener(it) }
            }
        }
    }

    fun renderData(pos: Int, data: MovieSummary) = with(itemVB) {
        Glide.with(itemView.context).load(baseImageUrl + data.backdrop_path).into(imagePoster)
        textName.text = data.title ?: "---"
        movie = data
    }
}
