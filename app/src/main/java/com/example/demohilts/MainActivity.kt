package com.example.demohilts

import android.os.Bundle
import android.view.WindowManager
import android.widget.Toast
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import com.bumptech.glide.Glide
import com.example.demohilts.base.MovieAdapter
import com.example.demohilts.data.current.CurrentData
import com.example.demohilts.data.entity.Genre
import com.example.demohilts.data.entity.MovieDetail
import com.example.demohilts.data.entity.MovieSummary
import com.example.demohilts.data.service.CoroutineState
import com.example.demohilts.databinding.ActivityMainBinding
import com.example.demohilts.screens.detail.DetailFragment
import com.example.demohilts.screens.genre.GenreMoviesFragment
import com.example.demohilts.screens.home.GenresAdapter
import com.example.demohilts.screens.home.TrendingFragment
import com.example.demohilts.screens.search.SearchFragment
import com.example.demohilts.utils.Constants
import com.example.demohilts.utils.SPUtils
import com.example.demohilts.utils.gone
import com.example.demohilts.utils.show
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.collect
import javax.inject.Inject

@AndroidEntryPoint
class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding
    private val mainViewModel: MainViewModel by viewModels()

    @Inject
    lateinit var popularAdapter: MovieAdapter

    @Inject
    lateinit var trendingAdapter: MovieAdapter

    @Inject
    lateinit var genresAdapter: GenresAdapter

    private var page = 1

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)
        setupEvents()
        initViews()
        initData()
        observeData()
    }

    private fun initViews() = with(binding) {
        layoutRefresh.isEnabled = false
        recyclerPopular.adapter = popularAdapter
        recyclerTrending.adapter = trendingAdapter
        recyclerGenres.adapter = genresAdapter
    }

    private fun initData() = with(mainViewModel) {
        CurrentData.currentId.value = SPUtils.getCurrentId(this@MainActivity)
        getTrendingMovies()
        getPopularMovies(page)
        getGenres()
    }

    private fun observeData() {
        lifecycleScope.launchWhenCreated {
            mainViewModel.trendingMovies.collect {
                when (it.status) {
                    CoroutineState.LOADING -> displayLoadingState()
                    CoroutineState.SUCCESS -> {
                        it.data?.results?.let { movies -> displaySuccessStateTrending(movies) }
                    }
                    CoroutineState.ERROR -> displayErrorState(it.message ?: "Error!")
                }
            }
        }
        lifecycleScope.launchWhenCreated {
            mainViewModel.popularMovies.collect {
                when (it.status) {
                    CoroutineState.LOADING -> displayLoadingState()
                    CoroutineState.SUCCESS -> {
                        it.data?.results?.let { movies -> displaySuccessStatePopular(movies) }
                    }
                    CoroutineState.ERROR -> displayErrorState(it.message ?: "Error!")
                }
            }
        }
        lifecycleScope.launchWhenCreated {
            mainViewModel.genres.collect {
                when (it.status) {
                    CoroutineState.LOADING -> displayLoadingState()
                    CoroutineState.SUCCESS -> {
                        it.data?.genres?.let { genres -> displaySuccessStateGenres(genres) }
                    }
                    CoroutineState.ERROR -> displayErrorState(it.message ?: "Error!")
                }
            }
        }
        lifecycleScope.launchWhenCreated {
            mainViewModel.detail.collect {
                when (it.status) {
                    CoroutineState.LOADING -> displayLoadingState()
                    CoroutineState.SUCCESS -> {
                        it.data?.let { movie -> displayCurrent(movie) }
                    }
                    CoroutineState.ERROR -> {
                        hideCurrent()
                    }
                }
            }
        }
        lifecycleScope.launchWhenCreated {
            CurrentData.currentId.collect {
                if (it != -1) {
                    getCurrentDetail(it)
                } else {
                    hideCurrent()
                }
            }
        }
    }

    private fun setupEvents() = with(binding) {
        layoutRefresh.setOnRefreshListener {
            mainViewModel.getPopularMovies(page)
            mainViewModel.getTrendingMovies()
            mainViewModel.getGenres()
        }
        popularAdapter.onClickListener = {
            openDetail(it)
        }
        trendingAdapter.onClickListener = {
            openDetail(it)
        }
        genresAdapter.onClickListener = {
            openGenreMovies(it)
        }
        layoutWatching.imageDetail.setOnClickListener {
            openDetail(CurrentData.currentId.value)
        }
        textPopularMore.setOnClickListener {
            openPopulars()
        }
        imageSearch.setOnClickListener {
            openSearch()
        }
    }

    private fun displayLoadingState() = with(binding) {

    }

    private fun displayErrorState(message: String) = with(binding) {
        layoutRefresh.isRefreshing = false
        Toast.makeText(this@MainActivity, message, Toast.LENGTH_SHORT).show()
    }

    private fun displaySuccessStatePopular(data: List<MovieSummary>) = with(binding) {
        layoutRefresh.isRefreshing = false
        popularAdapter.submitList(data)
    }

    private fun displaySuccessStateTrending(data: List<MovieSummary>) = with(binding) {
        layoutRefresh.isRefreshing = false
        trendingAdapter.submitList(data)
    }

    private fun displaySuccessStateGenres(data: List<Genre>) = with(binding) {
        layoutRefresh.isRefreshing = false
        genresAdapter.submitList(data)
    }

    private fun getCurrentDetail(id: Int) {
        mainViewModel.getDetail(id)
    }

    private fun displayCurrent(movie: MovieDetail) = with(binding) {
        layoutWatching.apply {
            layout.show()
            cardPlay.show()
            imageWatching.show()
            textName.show()
            Glide.with(this@MainActivity).load(Constants.baseImageUrl + movie.backdrop_path)
                .into(imageWatching)
            textName.text = movie.title ?: "---"
        }
    }

    private fun hideCurrent() {
        binding.layoutWatching.layout.gone()
        binding.layoutWatching.apply {
            cardPlay.gone()
            imageWatching.gone()
            textName.gone()
        }
    }

    private fun openDetail(id: Int) {
        val detailFragment = DetailFragment.getInstance(id)
        detailFragment.show(supportFragmentManager, detailFragment::class.java.simpleName)
        SPUtils.saveCurrentId(this, id)
    }

    private fun openPopulars() {
        TrendingFragment().show(supportFragmentManager, TrendingFragment::class.java.simpleName)
    }

    private fun openSearch() {
        SearchFragment().show(supportFragmentManager, SearchFragment::class.java.simpleName)
    }

    private fun openGenreMovies(genre: Genre) {
        val genreMoviesFragment = GenreMoviesFragment.getInstance(genre)
        genreMoviesFragment.show(supportFragmentManager, genreMoviesFragment::class.java.simpleName)
    }
}
