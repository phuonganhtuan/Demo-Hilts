package com.example.demohilts.screens.detail.trailers

import android.content.ActivityNotFoundException
import android.content.Intent
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.net.Uri
import android.os.Bundle
import android.util.DisplayMetrics
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.DialogFragment
import com.example.demohilts.R
import com.example.demohilts.data.entity.Video
import com.example.demohilts.databinding.LayoutTrailerBinding
import com.google.android.material.chip.Chip
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class TrailersFragment : DialogFragment() {

    private lateinit var viewBinding: LayoutTrailerBinding
    private var videos = listOf<Video>()

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
                    openYoutube(tag.toString())
                }
            }
            viewBinding.trailerGroup.addView(chip)
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
            (outMetrics.widthPixels * 0.7).toInt(),
            ViewGroup.LayoutParams.WRAP_CONTENT
        )
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

    companion object {
        fun getInstance(videos: List<Video>) = TrailersFragment().apply {
            this.videos = videos
        }
    }
}
