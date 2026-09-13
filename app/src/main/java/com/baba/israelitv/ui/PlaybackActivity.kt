package com.baba.israelitv.ui

import android.annotation.SuppressLint
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.view.View
import androidx.annotation.OptIn
import androidx.appcompat.app.AppCompatActivity
import androidx.media3.common.MediaItem
import androidx.media3.common.PlaybackException
import androidx.media3.common.Player
import androidx.media3.common.util.UnstableApi
import androidx.media3.datasource.DefaultHttpDataSource
import androidx.media3.exoplayer.ExoPlayer
import androidx.media3.exoplayer.hls.HlsMediaSource
import androidx.media3.ui.PlayerView
import com.baba.israelitv.R
import com.baba.israelitv.databinding.ActivityPlaybackBinding
import com.baba.israelitv.model.ResolvedChannel

class PlaybackActivity : AppCompatActivity() {

    private lateinit var binding: ActivityPlaybackBinding

    private var player: ExoPlayer? = null
    private lateinit var channelName: String
    private lateinit var streamUrl: String
    private var userAgent: String? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityPlaybackBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val url = intent.getStringExtra(EXTRA_STREAM_URL)
        if (url == null) {
            finish()
            return
        }
        streamUrl = url
        channelName = intent.getStringExtra(EXTRA_CHANNEL_NAME).orEmpty()
        userAgent = intent.getStringExtra(EXTRA_USER_AGENT)

        binding.playerView.useController = true
        setShowBuffering(binding.playerView)

        binding.retryButton.setOnClickListener {
            hideError()
            initializePlayer(streamUrl, userAgent)
        }
    }

    override fun onStart() {
        super.onStart()
        initializePlayer(streamUrl, userAgent)
    }

    override fun onStop() {
        super.onStop()
        releasePlayer()
    }

    @OptIn(UnstableApi::class)
    private fun initializePlayer(url: String, ua: String?) {
        hideError()
        val dataSourceFactory = DefaultHttpDataSource.Factory()
            .setUserAgent(ua ?: DEFAULT_USER_AGENT)
            .setAllowCrossProtocolRedirects(true)
        val mediaSourceFactory = HlsMediaSource.Factory(dataSourceFactory)

        val exoPlayer = ExoPlayer.Builder(this)
            .setMediaSourceFactory(mediaSourceFactory)
            .build()

        exoPlayer.addListener(object : Player.Listener {
            override fun onPlayerError(error: PlaybackException) {
                showError()
            }
        })

        exoPlayer.setMediaItem(MediaItem.fromUri(url))
        exoPlayer.playWhenReady = true
        exoPlayer.prepare()

        binding.playerView.player = exoPlayer
        player = exoPlayer
    }

    @OptIn(UnstableApi::class)
    private fun setShowBuffering(playerView: PlayerView) {
        playerView.setShowBuffering(PlayerView.SHOW_BUFFERING_WHEN_PLAYING)
    }

    private fun releasePlayer() {
        player?.release()
        player = null
    }

    @SuppressLint("StringFormatInvalid")
    private fun showError() {
        binding.errorOverlay.visibility = View.VISIBLE
        binding.errorTitle.text = getString(R.string.playback_error_title)
        binding.errorMessage.text = getString(R.string.playback_error_message, channelName)
    }

    private fun hideError() {
        binding.errorOverlay.visibility = View.GONE
    }

    companion object {
        private const val EXTRA_CHANNEL_NAME = "extra_channel_name"
        private const val EXTRA_STREAM_URL = "extra_stream_url"
        private const val EXTRA_USER_AGENT = "extra_user_agent"

        private const val DEFAULT_USER_AGENT =
            "Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 " +
                "(KHTML, like Gecko) Chrome/124.0.0.0 Safari/537.36"

        fun newIntent(context: Context, channel: ResolvedChannel): Intent =
            Intent(context, PlaybackActivity::class.java).apply {
                putExtra(EXTRA_CHANNEL_NAME, channel.displayName)
                putExtra(EXTRA_STREAM_URL, channel.streamUrl)
                putExtra(EXTRA_USER_AGENT, channel.userAgent)
            }
    }
}
