package com.example.demohilts.screens.home

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.example.demohilts.data.entity.Genre
import com.example.demohilts.databinding.ItemGenreBinding
import javax.inject.Inject

class GenresAdapter @Inject constructor() :
    ListAdapter<Genre, GenreVH>(GenreDiffCallback()) {

    var onClickListener: ((Genre) -> Any)? = null

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): GenreVH {
        val itemVB =
            ItemGenreBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return GenreVH(itemVB, onClickListener)
    }

    override fun onBindViewHolder(holder: GenreVH, position: Int) {
        holder.renderData(position, getItem(position))
    }
}

class GenreVH(
    private val itemVB: ItemGenreBinding,
    private val onClickListener: ((Genre) -> Any)?
) : RecyclerView.ViewHolder(itemVB.root) {

    private var genre: Genre? = null

    init {
        itemVB.textGenreName.setOnClickListener {
            genre?.let {
                onClickListener?.let { clickListener -> clickListener(it) }
            }
        }
    }

    fun renderData(pos: Int, data: Genre) = with(itemVB) {
        textGenreName.text = data.name ?: "---"
        this@GenreVH.genre = data
    }
}

class GenreDiffCallback : DiffUtil.ItemCallback<Genre>() {

    override fun areContentsTheSame(oldItem: Genre, newItem: Genre) =
        oldItem.id == newItem.id

    override fun areItemsTheSame(oldItem: Genre, newItem: Genre) = oldItem === newItem
}
