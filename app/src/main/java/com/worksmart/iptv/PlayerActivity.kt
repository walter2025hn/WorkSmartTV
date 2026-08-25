package com.worksmart.iptv

import android.os.Bundle
import android.view.View
import android.widget.ProgressBar
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.WindowCompat
import androidx.core.view.WindowInsetsCompat
import androidx.core.view.WindowInsetsControllerCompat
import androidx.media3.common.MediaItem
import androidx.media3.common.Player
import androidx.media3.exoplayer.ExoPlayer
import androidx.media3.ui.PlayerView

class PlayerActivity : AppCompatActivity() {

    private var player: ExoPlayer? = null
    private lateinit var playerView: PlayerView
    private lateinit var loadingSpinner: ProgressBar

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        
        WindowCompat.setDecorFitsSystemWindows(window, false)
        WindowInsetsControllerCompat(window, window.decorView).let { controller ->
            controller.hide(WindowInsetsCompat.Type.systemBars())
        }
        
        setContentView(R.layout.activity_player)
        
        playerView = findViewById(R.id.playerView)
        loadingSpinner = findViewById(R.id.loadingSpinner)
        
        val streamUrl = intent.getStringExtra("STREAM_URL") ?: return
        val title = intent.getStringExtra("TITLE") ?: "Canal"
        
        initializePlayer(streamUrl, title)
    }

    private fun initializePlayer(url: String, title: String) {
        player = ExoPlayer.Builder(this).build().apply {
            setMediaItem(MediaItem.fromUri(url))
            addListener(object : Player.Listener {
                override fun onPlaybackStateChanged(state: Int) {
                    when (state) {
                        Player.STATE_BUFFERING -> loadingSpinner.visibility = View.VISIBLE
                        Player.STATE_READY -> loadingSpinner.visibility = View.GONE
                        Player.STATE_ENDED -> finish()
                    }
                }
                
                override fun onIsPlayingChanged(isPlaying: Boolean) {
                    if (isPlaying) loadingSpinner.visibility = View.GONE
                }
            })
            prepare()
            play()
        }
        
        playerView.player = player
    }

    override fun onPause() {
        super.onPause()
        player?.pause()
    }

    override fun onResume() {
        super.onResume()
        player?.play()
    }

    override fun onDestroy() {
        super.onDestroy()
        player?.release()
        player = null
    }
}
