package com.example.demohilts.screens.detail

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.demohilts.R
import com.example.demohilts.base.MovieSummaryDiffCallback
import com.example.demohilts.base.MovieVH
import com.example.demohilts.data.entity.Cast
import com.example.demohilts.data.entity.CastsCrews
import com.example.demohilts.data.entity.Crew
import com.example.demohilts.data.entity.MovieSummary
import com.example.demohilts.databinding.ItemMovieHomeBinding
import com.example.demohilts.databinding.ItemPeopleBinding
import com.example.demohilts.utils.Constants.baseImageUrl
import com.example.demohilts.utils.Constants.baseImageUrlSmall
import javax.inject.Inject

class CrewAdapter @Inject constructor() :
    ListAdapter<Crew, CrewVH>(CrewDiffCallback()) {

    var onClickListener: ((Int) -> Any)? = null

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): CrewVH {
        val itemVB =
            ItemPeopleBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return CrewVH(itemVB, onClickListener)
    }

    override fun onBindViewHolder(holder: CrewVH, position: Int) {
        holder.renderData(position, getItem(position))
    }
}

class CrewVH(
    private val itemVB: ItemPeopleBinding,
    private val onClickListener: ((Int) -> Any)?
) : RecyclerView.ViewHolder(itemVB.root) {

    fun renderData(pos: Int, data: Crew) = with(itemVB) {
        Glide.with(itemView.context)
            .load(baseImageUrlSmall + data.profile_path)
            .placeholder(R.drawable.ic_person_place_holder)
            .into(imageAvatar)
        textName.text = "${data.name}\n(${data.job})"
    }
}

class CrewDiffCallback : DiffUtil.ItemCallback<Crew>() {

    override fun areContentsTheSame(oldItem: Crew, newItem: Crew) =
        oldItem.id == newItem.id

    override fun areItemsTheSame(oldItem: Crew, newItem: Crew) = oldItem === newItem
}
