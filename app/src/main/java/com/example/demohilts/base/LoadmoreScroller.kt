package com.example.demohilts.base

import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView

abstract class LoadmoreScroller(private val gridLayoutManager: GridLayoutManager) :
    RecyclerView.OnScrollListener() {

    override fun onScrolled(recyclerView: RecyclerView, dx: Int, dy: Int) {
        super.onScrolled(recyclerView, dx, dy)
        if (isLastPage || isLoading) return
        val totalItemCount = gridLayoutManager.itemCount
        if (gridLayoutManager.findLastCompletelyVisibleItemPosition() == totalItemCount - 1) {
            loadMoreItem()
        }
    }

    abstract fun loadMoreItem()
    abstract val isLoading: Boolean
    abstract val isLastPage: Boolean
}
