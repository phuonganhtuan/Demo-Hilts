package com.example.demohilts.screens.detail

import android.annotation.SuppressLint
import android.content.ActivityNotFoundException
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.util.DisplayMetrics
import android.util.SparseArray
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.Toast
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import at.huber.youtubeExtractor.VideoMeta
import at.huber.youtubeExtractor.YouTubeExtractor
import at.huber.youtubeExtractor.YtFile
import com.bumptech.glide.Glide
import com.example.demohilts.R
import com.example.demohilts.base.FullScreenBottomSheetDialogFragment
import com.example.demohilts.base.MovieAdapter
import com.example.demohilts.data.entity.*
import com.example.demohilts.data.service.CoroutineState
import com.example.demohilts.databinding.LayoutDetailBinding
import com.example.demohilts.screens.detail.trailers.TrailersFragment
import com.example.demohilts.screens.genre.GenreMoviesFragment
import com.example.demohilts.utils.Constants
import com.example.demohilts.utils.SPUtils
import com.example.demohilts.utils.gone
import com.example.demohilts.utils.show
import com.google.android.exoplayer2.C
import com.google.android.exoplayer2.MediaItem
import com.google.android.exoplayer2.SimpleExoPlayer
import com.google.android.material.chip.Chip
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.collect
import javax.inject.Inject


@AndroidEntryPoint
class DetailFragment : FullScreenBottomSheetDialogFragment<LayoutDetailBinding>() {

    private val viewModel: DetailViewModel by viewModels()

    @Inject
    lateinit var similarAdapter: MovieAdapter

    @Inject
    lateinit var castAdapter: CastAdapter

    @Inject
    lateinit var crewAdapter: CrewAdapter

    val exoPlayer by lazy { SimpleExoPlayer.Builder(requireContext()).build() }

    private var movieId: Int? = null
    private var reviewPage = 1
    private var similarPage = 1

    override fun inflateViewBinding(container: ViewGroup?) =
        LayoutDetailBinding.inflate(layoutInflater, container, false)

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        initViews()
        initData()
        observeData()
        setupEvents()
    }

    override fun onResume() {
        super.onResume()
        val outMetrics = DisplayMetrics()
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.R) {
            val display = activity?.display
            display?.getRealMetrics(outMetrics)
        } else {
            @Suppress("DEPRECATION")
            val display = activity?.windowManager?.defaultDisplay
            @Suppress("DEPRECATION")
            display?.getMetrics(outMetrics)
        }
        dialog?.window?.setLayout(outMetrics.widthPixels, outMetrics.heightPixels)
    }

    override fun onDestroy() {
        super.onDestroy()
        exoPlayer.release()
    }

    private fun initViews() = with(viewBinding) {
        recyclerSimilars.adapter = similarAdapter
        recyclerCasts.adapter = castAdapter
        recyclerCrews.adapter = crewAdapter
        layoutHeaderTop.gone()
        layoutHeader.videoView.player = exoPlayer
        layoutHeader.videoView.hideController()
    }

    private fun initData() = with(viewModel) {
        movieId?.let {
            getDetail(it)
            getCastsAndCrews(it)
            getReviews(it, reviewPage)
            getSimilarMovies(it, similarPage)
            getVideos(it)
        }
    }

    private fun observeData() = with(viewModel) {
        lifecycleScope.launchWhenCreated {
            detail.collect {
                when (it.status) {
                    CoroutineState.LOADING -> displayLoadingState()
                    CoroutineState.SUCCESS -> {
                        it.data?.let { movie -> displaySuccessStateDetail(movie) }
                    }
                    CoroutineState.ERROR -> {
                        displayErrorState(it.message ?: "Error!")
                        dismiss()
                    }
                }
            }
        }
        lifecycleScope.launchWhenCreated {
            castsCrews.collect {
                when (it.status) {
                    CoroutineState.LOADING -> displayLoadingState()
                    CoroutineState.SUCCESS -> {
                        it.data?.let { movie -> displaySuccessStateCasts(movie) }
                    }
                    CoroutineState.ERROR -> displayErrorState(it.message ?: "Error!")
                }
            }
        }
        lifecycleScope.launchWhenCreated {
            similars.collect {
                when (it.status) {
                    CoroutineState.LOADING -> displayLoadingState()
                    CoroutineState.SUCCESS -> {
                        it.data?.results?.let { movies -> displaySuccessStateSimilars(movies) }
                    }
                    CoroutineState.ERROR -> displayErrorState(it.message ?: "Error!")
                }
            }
        }
        lifecycleScope.launchWhenCreated {
            reviews.collect {
                when (it.status) {
                    CoroutineState.LOADING -> displayLoadingState()
                    CoroutineState.SUCCESS -> {
                        it.data?.results?.let { reviews -> displaySuccessStateReviews(reviews) }
                    }
                    CoroutineState.ERROR -> displayErrorState(it.message ?: "Error!")
                }
            }
        }
        lifecycleScope.launchWhenCreated {
            videos.collect {
                when (it.status) {
                    CoroutineState.LOADING -> displayLoadingState()
                    CoroutineState.SUCCESS -> {
                        it.data?.results?.let { videos -> displaySuccessStateVideos(videos) }
                    }
                    CoroutineState.ERROR -> return@collect
                }
            }
        }
    }

    private fun setupEvents() = with(viewBinding) {
        layoutMain.setOnScrollChangeListener { _, _, scrollY, _, _ ->
            val scale = scrollY / 300f
            layoutHeaderTop.alpha = scale
            layoutHeader.imageBack.visibility = if (scale == 0f) View.VISIBLE else View.GONE
            layoutHeaderTop.visibility = if (scale == 0f) View.GONE else View.VISIBLE
        }
        layoutHeader.imageBack.setOnClickListener { dismiss() }
        imageBackOut.setOnClickListener { dismiss() }
        similarAdapter.onClickListener = { id ->
            openSimilar(id)
        }
        layoutHeader.textTrailer.setOnClickListener {
            val videos = viewModel.videos.value.data?.results ?: return@setOnClickListener
            if (videos.isNotEmpty()) {
                if (videos.size == 1) {
                    val videoKey = videos[0].key ?: return@setOnClickListener
                    openYoutube(videoKey)
                } else {
                    openTrailerChooser(videos)
                }
            }
        }
    }

    private fun openTrailerChooser(videos: List<Video>) {
        val trailerFragment = TrailersFragment.getInstance(videos)
        trailerFragment.show(
            requireActivity().supportFragmentManager,
            trailerFragment::class.java.simpleName
        )
    }

    private fun displayLoadingState() = with(viewBinding) {

    }

    private fun displayErrorState(message: String) = with(viewBinding) {
        Toast.makeText(requireContext(), message, Toast.LENGTH_SHORT).show()
    }

    private fun displaySuccessStateDetail(data: MovieDetail) = with(viewBinding) {
        textItemName.text = data.title
        textHeader.text = data.title
        (data.overview + "\n\nReleased: ${data.release_date}\n\nCompanies: ${data.getCompaniesText()}").also {
            textDesc.text = it
        }
        data.vote_count?.let {
            textVoteNum.text = "($it)"
        }
        data.vote_average?.let {
            displayRating(it)
        }
        Glide.with(requireContext()).load(Constants.baseImageUrl + data.backdrop_path)
            .into(layoutHeader.imagePoster)
        if (!data.genres.isNullOrEmpty()) {
            data.genres.forEach {
                val chip = layoutInflater.inflate(
                    R.layout.chip_genre, viewBinding.genresChipGroup, false
                ) as Chip
                chip.apply {
                    tag = it.id.toString()
                    text = it.name
                    isClickable = true
                    isCheckable = false
                    setOnClickListener {
                        val id = it.tag.toString().toIntOrNull() ?: return@setOnClickListener
                        val genre = data.genres.filter { it.id == id }[0]
                        openGenreMovies(genre)
                    }
                }
                viewBinding.genresChipGroup.addView(chip)
            }
        }
    }

    private fun displaySuccessStateCasts(castsCrews: CastsCrews) = with(viewBinding) {
        if ((castsCrews.cast ?: emptyList()).isNotEmpty()) textCasts.show()
        if ((castsCrews.crew ?: emptyList()).isNotEmpty()) textCrews.show()
        castAdapter.submitList(castsCrews.cast ?: emptyList())
        textCasts.text = "Casts (${(castsCrews.cast ?: emptyList()).size})"
        crewAdapter.submitList(castsCrews.crew ?: emptyList())
        textCrews.text = "Crews (${(castsCrews.crew ?: emptyList()).size})"
    }

    private fun displaySuccessStateSimilars(movies: List<MovieSummary>) = with(viewBinding) {
        textSimilars.show()
        similarAdapter.submitList(movies)
    }

    private fun displaySuccessStateReviews(reviews: List<Comment>) = with(viewBinding) {

    }

    @SuppressLint("StaticFieldLeak")
    private fun displaySuccessStateVideos(videos: List<Video>) {
        if (videos.isNotEmpty()) {
            viewBinding.layoutHeader.cardPlay.show()
            val videoUrl = videos.random().key
            val extracor = object : YouTubeExtractor(requireContext()) {
                override fun onExtractionComplete(
                    ytFiles: SparseArray<YtFile>?,
                    vMeta: VideoMeta?
                ) {
                    if (ytFiles != null) {
                        val itag = 22
                        val downloadUrl: String = ytFiles[itag].url
                        val trailer = MediaItem.fromUri(Uri.parse(downloadUrl))
                        viewBinding.layoutHeader.videoView.show()
                        exoPlayer.setMediaItems(mutableListOf(trailer))
                        exoPlayer.seekTo(0, C.TIME_UNSET)
                        exoPlayer.prepare()
                        exoPlayer.play()
                    }
                }
            }
            extracor.extract("https://youtu.be/" + videoUrl, true, true)
        }
    }

    private fun openYoutube(key: String) {
        val intentApp = Intent(Intent.ACTION_VIEW, Uri.parse("vnd.youtube:" + key))
        val intentBrowser =
            Intent(Intent.ACTION_VIEW, Uri.parse("http://www.youtube.com/watch?v=" + key))
        try {
            this.startActivity(intentApp)
        } catch (ex: ActivityNotFoundException) {
            this.startActivity(intentBrowser)
        }
    }

    private fun displayRating(votePoint: Double) {
        var hasHalf = false
        val pointInFive = (votePoint / 2f)
        var pointInt = (votePoint / 2f).toInt()
        val pointFloat = pointInFive - pointInt
        pointInt += when {
            pointFloat > 0.3 && pointFloat < 0.8 -> {
                hasHalf = true
                0
            }
            pointFloat >= 0.8 -> 1
            else -> 0
        }
        val numOfEmptyStar = if (hasHalf) 5 - pointInt - 1 else 5 - pointInt
        viewBinding.layoutRating.removeAllViews()
        for (i in 0 until pointInt) {
            val star = ImageView(requireContext()).apply {
                setImageResource(R.drawable.ic_baseline_star_24)
            }
            viewBinding.layoutRating.addView(star)
        }
        if (hasHalf) {
            val star = ImageView(requireContext()).apply {
                setImageResource(R.drawable.ic_baseline_star_half_24)
            }
            viewBinding.layoutRating.addView(star)
        }
        for (i in 0 until numOfEmptyStar) {
            val star = ImageView(requireContext()).apply {
                setImageResource(R.drawable.ic_baseline_star_border_24)
            }
            viewBinding.layoutRating.addView(star)
        }
    }

    private fun openSimilar(id: Int) {
        val detailFragment = getInstance(id)
        detailFragment.show(
            requireActivity().supportFragmentManager,
            detailFragment::class.java.simpleName
        )
        SPUtils.saveCurrentId(requireContext(), id)
    }

    private fun openGenreMovies(genre: Genre) {
        val genreMoviesFragment = GenreMoviesFragment.getInstance(genre)
        genreMoviesFragment.show(
            requireActivity().supportFragmentManager,
            genreMoviesFragment::class.java.simpleName
        )
    }

    companion object {
        fun getInstance(id: Int) = DetailFragment().apply {
            movieId = id
        }
    }
}
