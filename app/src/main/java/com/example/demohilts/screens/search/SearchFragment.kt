package com.example.demohilts.screens.search

import android.content.Context
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.text.Editable
import android.text.TextWatcher
import android.view.View
import android.view.ViewGroup
import android.view.WindowManager
import android.view.inputmethod.InputMethodManager
import android.widget.Toast
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.RecyclerView
import com.example.demohilts.R
import com.example.demohilts.base.FullScreenBottomSheetDialogFragment
import com.example.demohilts.data.entity.KeyWord
import com.example.demohilts.data.entity.MovieSummary
import com.example.demohilts.data.service.CoroutineState
import com.example.demohilts.databinding.LayoutSearchBinding
import com.example.demohilts.screens.detail.DetailFragment
import com.example.demohilts.screens.genre.GenreMovieAdapter
import com.example.demohilts.utils.SPUtils
import com.example.demohilts.utils.gone
import com.example.demohilts.utils.show
import com.google.android.material.chip.Chip
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.collect
import javax.inject.Inject

@AndroidEntryPoint
class SearchFragment : FullScreenBottomSheetDialogFragment<LayoutSearchBinding>() {

    private val viewModel: SearchViewModel by viewModels()

    @Inject
    lateinit var adapter: GenreMovieAdapter

    private var page = 1
    private var isLoading = false

    private var searchHandler = Handler(Looper.getMainLooper())
    private var searchRunnable: Runnable? = null

    override fun inflateViewBinding(container: ViewGroup?) =
        LayoutSearchBinding.inflate(layoutInflater, container, false)

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        initView()
        initData()
        setupEvents()
        observeData()
    }

    private fun initView() = with(viewBinding) {
        recyclerResults.adapter = adapter
        progress.gone()
        textLoadMore.gone()
        layoutSearchHeader.editSearch.requestFocus()
    }

    private fun initData() {

    }

    private fun observeData() = with(viewModel) {
        lifecycleScope.launchWhenCreated {
            results.collect {
                when (it.status) {
                    CoroutineState.LOADING -> displayLoadingState()
                    CoroutineState.SUCCESS -> {
                        it.data?.results?.let { movies ->
                            displaySuccessStateDetail(
                                movies,
                                it.data.total_results ?: 0
                            )
                        }
                    }
                    CoroutineState.ERROR -> {
                        displayErrorState(it.message ?: "Error!")
                    }
                }
            }
        }
        lifecycleScope.launchWhenCreated {
            keyWords.collect {
                when (it.status) {
                    CoroutineState.LOADING -> displayLoadingState()
                    CoroutineState.SUCCESS -> {
                        it.data?.results?.let { kWs ->
                            displaySuccessStateKWs(kWs)
                        }
                    }
                    CoroutineState.ERROR -> {
                        displayErrorState(it.message ?: "Error!")
                    }
                }
            }
        }
    }

    private fun setupEvents() = with(viewBinding) {
        layoutSearchHeader.imageBack.setOnClickListener { dismiss() }
        textLoadMore.setOnClickListener {
            if (isLoading) return@setOnClickListener
            page += 1
            progress.show()
            viewModel.searchMovies(page, viewBinding.layoutSearchHeader.editSearch.text.toString())
        }
        adapter.onClickListener = { id, type ->
            openDetail(id, type)
        }
        recyclerResults.addOnScrollListener(object : RecyclerView.OnScrollListener() {
            override fun onScrolled(recyclerView: RecyclerView, dx: Int, dy: Int) {
                super.onScrolled(recyclerView, dx, dy)
                //layoutSearchHeader.editSearch.clearFocus()
                hideKeyboard()
            }
        })
        layoutSearchHeader.editSearch.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {
            }

            override fun onTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {
                hideLoadMoreUIs()
                stopCurrentQuery()
                startNewQuery()
            }

            override fun afterTextChanged(p0: Editable?) {
            }
        })
    }

    private fun displayLoadingState() = with(viewBinding) {
        isLoading = true
    }

    private fun displayErrorState(message: String) = with(viewBinding) {
        isLoading = false
        progress.gone()
        Toast.makeText(requireContext(), message, Toast.LENGTH_SHORT).show()
    }

    private fun displaySuccessStateDetail(data: List<MovieSummary>, count: Int) =
        with(viewBinding) {
            isLoading = false
            showLoadMoreUIs()
            if (data.size < 20) {
                hideLoadMoreUIs()
            }
            if (page == 1) {
                adapter.submitList(data)
            } else {
                val currentList = adapter.currentList.toMutableList()
                currentList.addAll(data)
                adapter.submitList(currentList)
            }
            textTitle.text = if (page == 1 && data.isEmpty()) {
                "No result for: \"${layoutSearchHeader.editSearch.text}\""
            } else {
                "$count result(s) for: \"${layoutSearchHeader.editSearch.text}\""
            }
        }

    private fun displaySuccessStateKWs(data: List<KeyWord>) = with(viewBinding) {
        var finalList = data.toMutableList()
        if (data.size > 10) {
            finalList = data.subList(0, 10).toMutableList()
        }
        recentGroup.removeAllViews()
        finalList.forEach {
            val chip = layoutInflater.inflate(
                R.layout.chip_genre, recentGroup, false
            ) as Chip
            chip.apply {
                text = it.name
                isClickable = true
                isCheckable = false
                setOnClickListener {
                    layoutSearchHeader.editSearch.setText(text)
                }
            }
            recentGroup.addView(chip)
        }
    }

    private fun stopCurrentQuery() {
        searchHandler.removeCallbacksAndMessages(null)
        searchRunnable = null
        page = 1
    }

    private fun startNewQuery() {
        searchRunnable = Runnable {
            viewModel.searchMovies(page, viewBinding.layoutSearchHeader.editSearch.text.toString())
            viewModel.searchKWs(1, viewBinding.layoutSearchHeader.editSearch.text.toString())
        }
        searchRunnable?.let { searchHandler.postDelayed(it, 500) }
    }

    private fun hideLoadMoreUIs() = with(viewBinding) {
        textLoadMore.gone()
        progress.gone()
    }

    private fun showLoadMoreUIs() = with(viewBinding) {
        textLoadMore.show()
        progress.gone()
    }

    private fun openDetail(id: Int, type: String) {
        val detailFragment = DetailFragment.getInstance(id, type)
        detailFragment.show(
            requireActivity().supportFragmentManager,
            detailFragment::class.java.simpleName
        )
        SPUtils.saveCurrentId(requireContext(), id, type)
    }

    private fun hideKeyboard() {
        requireActivity().currentFocus?.let { view ->
            val imm =
                requireActivity().getSystemService(Context.INPUT_METHOD_SERVICE) as? InputMethodManager
            imm?.hideSoftInputFromWindow(view.windowToken, 0)
            view.clearFocus()
        }
        requireActivity().window?.setSoftInputMode(
            WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN
        );
        viewBinding.layoutSearchHeader.editSearch.clearFocus()
    }
}
