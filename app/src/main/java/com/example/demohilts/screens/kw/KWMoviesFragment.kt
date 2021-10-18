package com.example.demohilts.screens.kw

import android.os.Bundle
import android.util.Log
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import com.example.demohilts.base.FullScreenBottomSheetDialogFragment
import com.example.demohilts.data.entity.MovieSummary
import com.example.demohilts.data.service.CoroutineState
import com.example.demohilts.databinding.LayoutKeywordMovieBinding
import com.example.demohilts.screens.detail.DetailFragment
import com.example.demohilts.screens.genre.GenreMovieAdapter
import com.example.demohilts.utils.SPUtils
import com.example.demohilts.utils.gone
import com.example.demohilts.utils.show
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.collect
import javax.inject.Inject


@AndroidEntryPoint
class KWMoviesFragment : FullScreenBottomSheetDialogFragment<LayoutKeywordMovieBinding>() {

    private val viewModel: KWMoviesVM by viewModels()

    @Inject
    lateinit var moviesAdapter: GenreMovieAdapter

    private var kw = ""
    private var page = 1
    private var isEnd = false
    private var isLoading = false

    override fun inflateViewBinding(container: ViewGroup?) =
        LayoutKeywordMovieBinding.inflate(layoutInflater, container, false)

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        initViews()
        initData()
        observeData()
        setupEvents()
    }

    private fun initViews() = with(viewBinding) {
        recyclerKWMovies.adapter = moviesAdapter
        textKW.text = "Movies for: " + kw
        layoutHeader.textTitle.text = "Movies for: " + kw
        layoutHeader.header.gone()
    }

    private fun initData() = with(viewModel) {
        getKWMovies(page, kw)
    }

    private fun observeData() = with(viewModel) {
        lifecycleScope.launchWhenCreated {
            movies.collect {
                when (it.status) {
                    CoroutineState.LOADING -> displayLoadingState()
                    CoroutineState.SUCCESS -> {
                        it.data?.results?.let { movies -> displaySuccessStateDetail(movies, it.data.total_results ?: 0) }
                    }
                    CoroutineState.ERROR -> {
                        displayErrorState(it.message ?: "Error!")
                        dismiss()
                    }
                }
            }
        }
    }

    private fun setupEvents() = with(viewBinding) {
        layoutMain.setOnScrollChangeListener { _, _, scrollY, _, _ ->
            val scale = scrollY / 80f
            layoutHeader.header.alpha = scale
            layoutHeader.header.visibility = if (scale == 0f) View.GONE else View.VISIBLE
            imageBackOut.visibility = if (scale == 0f) View.VISIBLE else View.GONE
        }
        layoutHeader.imageBack.setOnClickListener { dismiss() }
        imageBackOut.setOnClickListener { dismiss() }
        moviesAdapter.onClickListener = { id, type -> openDetail(id, type) }
        textLoadMore.setOnClickListener {
            if (isLoading) return@setOnClickListener
            page += 1
            viewModel.getKWMovies(page, kw)
            progress.show()
            Log.i("aaa", "loadmore page $page")
        }
    }

    private fun displayLoadingState() = with(viewBinding) {
        isLoading = true
    }

    private fun displayErrorState(message: String) = with(viewBinding) {
        isLoading = false
        progress.gone()
        Toast.makeText(requireContext(), message, Toast.LENGTH_SHORT).show()
    }

    private fun displaySuccessStateDetail(data: List<MovieSummary>, count: Int) = with(viewBinding) {
        isLoading = false
        progress.gone()
        textLoadMore.show()
        if (data.size < 20) {
            isEnd = true
            textLoadMore.gone()
        }
        val currentList = moviesAdapter.currentList.toMutableList()
        currentList.addAll(data)
        moviesAdapter.submitList(currentList)
        textKW.text = if (page == 1 && data.isEmpty()) {
            "No movie for: " + kw
        } else {
            "$count movie(s) for: " + kw
        }
    }

    private fun openDetail(id: Int, type: String) {
        val detailFragment = DetailFragment.getInstance(id, type)
        detailFragment.show(
            requireActivity().supportFragmentManager,
            detailFragment::class.java.simpleName
        )
        SPUtils.saveCurrentId(requireContext(), id, type)
    }

    companion object {
        fun getInstance(kw: String) = KWMoviesFragment().apply {
            this.kw = kw
        }
    }
}
