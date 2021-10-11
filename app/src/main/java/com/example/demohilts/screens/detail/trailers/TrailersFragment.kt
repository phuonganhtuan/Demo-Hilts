package com.example.demohilts.screens.detail.trailers

import android.annotation.SuppressLint
import android.content.ActivityNotFoundException
import android.content.Intent
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.net.Uri
import android.os.Bundle
import android.util.DisplayMetrics
import android.util.SparseArray
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.view.children
import androidx.fragment.app.DialogFragment
import at.huber.youtubeExtractor.VideoMeta
import at.huber.youtubeExtractor.YouTubeExtractor
import at.huber.youtubeExtractor.YtFile
import com.example.demohilts.R
import com.example.demohilts.data.entity.Video
import com.example.demohilts.databinding.LayoutTrailerBinding
import com.google.android.exoplayer2.C
import com.google.android.exoplayer2.MediaItem
import com.google.android.exoplayer2.SimpleExoPlayer
import com.google.android.material.chip.Chip
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class TrailersFragment : DialogFragment() {

    private lateinit var viewBinding: LayoutTrailerBinding
    private var videos = listOf<Video>()

    private var currentKey = ""

    val exoPlayer by lazy { SimpleExoPlayer.Builder(requireContext()).build() }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        viewBinding = LayoutTrailerBinding.inflate(inflater, container, false)
        dialog?.window?.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
        return viewBinding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        videos.forEach {
            val chip = layoutInflater.inflate(
                R.layout.chip_trailer, viewBinding.trailerGroup, false
            ) as Chip
            chip.apply {
                tag = it.key.toString()
                text = it.name
                isClickable = true
                isCheckable = false
                setOnClickListener {
                    openTrailer(tag.toString())
                    currentKey = tag.toString()
                    resetChipColor()
                    setChipBackgroundColorResource(R.color.design_default_color_error)
                }
            }
            viewBinding.trailerGroup.addView(chip)
        }
        viewBinding.videoView.player = exoPlayer
        viewBinding.imageYoutube.setOnClickListener {
            openYoutube()
        }
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
        dialog?.window?.setLayout(
            (outMetrics.widthPixels * 0.9).toInt(),
            ViewGroup.LayoutParams.WRAP_CONTENT
        )
    }

    override fun onPause() {
        super.onPause()
        if (exoPlayer.isPlaying) exoPlayer.pause()
    }

    override fun onDestroy() {
        super.onDestroy()
        if (exoPlayer.isPlaying) exoPlayer.release()
    }

    private fun setupEvents() = with(viewBinding) {

    }

    private fun openYoutube() {
        if (currentKey.isEmpty()) return
        val intentApp = Intent(Intent.ACTION_VIEW, Uri.parse("vnd.youtube:" + currentKey))
        val intentBrowser =
            Intent(Intent.ACTION_VIEW, Uri.parse("http://www.youtube.com/watch?v=" + currentKey))
        try {
            this.startActivity(intentApp)
        } catch (ex: ActivityNotFoundException) {
            this.startActivity(intentBrowser)
        }
    }

    private fun openTrailer(key: String) {
        val extractor = @SuppressLint("StaticFieldLeak")
        object : YouTubeExtractor(requireContext()) {
            override fun onExtractionComplete(
                ytFiles: SparseArray<YtFile>?,
                vMeta: VideoMeta?
            ) {
                if (ytFiles != null) {
                    val itag = 22
                    val downloadUrl: String = ytFiles[itag].url
                    val trailer = MediaItem.fromUri(Uri.parse(downloadUrl))
                    exoPlayer.setMediaItems(mutableListOf(trailer))
                    exoPlayer.seekTo(0, C.TIME_UNSET)
                    exoPlayer.prepare()
                    exoPlayer.play()
                }
            }
        }
        extractor.extract("https://youtu.be/" + key)
    }

    private fun resetChipColor() {
        viewBinding.trailerGroup.children.forEach {
            (it as? Chip)?.setChipBackgroundColorResource(R.color.color_bg_def)
        }
    }

    companion object {
        fun getInstance(videos: List<Video>) = TrailersFragment().apply {
            this.videos = videos
        }
    }
}
