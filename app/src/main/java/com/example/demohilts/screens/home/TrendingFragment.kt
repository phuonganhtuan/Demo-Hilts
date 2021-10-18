package com.example.demohilts.screens.home

import android.os.Bundle
import android.util.Log
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import com.example.demohilts.MainViewModel
import com.example.demohilts.base.FullScreenBottomSheetDialogFragment
import com.example.demohilts.data.entity.MovieSummary
import com.example.demohilts.data.service.CoroutineState
import com.example.demohilts.databinding.LayoutGenreMovieBinding
import com.example.demohilts.screens.detail.DetailFragment
import com.example.demohilts.screens.genre.GenreMovieAdapter
import com.example.demohilts.utils.SPUtils
import com.example.demohilts.utils.gone
import com.example.demohilts.utils.show
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.collect
import javax.inject.Inject


@AndroidEntryPoint
class TrendingFragment : FullScreenBottomSheetDialogFragment<LayoutGenreMovieBinding>() {

    private val viewModel: MainViewModel by viewModels()

    @Inject
    lateinit var moviesAdapter: GenreMovieAdapter

    private var page = 1
    private var isEnd = false
    private var isLoading = false

    override fun inflateViewBinding(container: ViewGroup?) =
        LayoutGenreMovieBinding.inflate(layoutInflater, container, false)

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        initViews()
        initData()
        observeData()
        setupEvents()
    }

    private fun initViews() = with(viewBinding) {
        recyclerGenreMovies.adapter = moviesAdapter
        textGenre.text = "Popular movies"
        layoutHeader.textTitle.text = "Popular movies"
        layoutHeader.header.gone()
    }

    private fun initData() = with(viewModel) {
        getPopularMovies(page)
    }

    private fun observeData() = with(viewModel) {
        lifecycleScope.launchWhenCreated {
            popularMovies.collect {
                when (it.status) {
                    CoroutineState.LOADING -> displayLoadingState()
                    CoroutineState.SUCCESS -> {
                        it.data?.results?.let { movies -> displaySuccessStateDetail(movies) }
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
        moviesAdapter.onClickListener = { id, key -> openDetail(id, key) }
        textLoadMore.setOnClickListener {
            if (isLoading) return@setOnClickListener
            page += 1
            viewModel.getPopularMovies(page)
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

    private fun displaySuccessStateDetail(data: List<MovieSummary>) = with(viewBinding) {
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
    }

    private fun openDetail(id: Int, type: String) {
        val detailFragment = DetailFragment.getInstance(id, type)
        detailFragment.show(
            requireActivity().supportFragmentManager,
            detailFragment::class.java.simpleName
        )
        SPUtils.saveCurrentId(requireContext(), id, type)
    }
}
