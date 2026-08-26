package com.senal.iptvplayer

import android.os.Bundle
import android.webkit.JavascriptInterface
import android.webkit.WebView
import android.webkit.WebViewClient
import androidx.appcompat.app.AppCompatActivity
import com.google.android.exoplayer2.ExoPlayer
import com.google.android.exoplayer2.MediaItem
import com.google.android.exoplayer2.PlaybackException
import com.google.android.exoplayer2.Player
import com.google.android.exoplayer2.source.hls.HlsMediaSource
import com.google.android.exoplayer2.trackselection.DefaultTrackSelector
import com.google.android.exoplayer2.ui.PlayerView
import com.google.android.exoplayer2.upstream.DefaultHttpDataSource

class MainActivity : AppCompatActivity() {

    private lateinit var webView: WebView
    private lateinit var playerView: PlayerView
    private lateinit var exoPlayer: ExoPlayer
    private lateinit var trackSelector: DefaultTrackSelector

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        // Inicializar WebView
        webView = findViewById(R.id.webView)
        webView.settings.javaScriptEnabled = true
        webView.settings.domStorageEnabled = true
        webView.addJavascriptInterface(AndroidInterface(), "Android")
        webView.webViewClient = object : WebViewClient() {
            override fun onPageFinished(view: WebView?, url: String?) {
                // Cargar la interfaz
            }
        }
        webView.loadUrl("file:///android_asset/www/index.html")

        // Inicializar ExoPlayer
        playerView = findViewById(R.id.playerView)
        trackSelector = DefaultTrackSelector(this)
        exoPlayer = ExoPlayer.Builder(this)
            .setTrackSelector(trackSelector)
            .build()
        playerView.player = exoPlayer
        playerView.useController = true

        exoPlayer.addListener(object : Player.Listener {
            override fun onPlayerError(error: PlaybackException) {
                // Notificar error a JS
                webView.evaluateJavascript("window.onPlayerError('${error.message}')", null)
            }
        })
    }

    inner class AndroidInterface {
        @JavascriptInterface
        fun play(url: String) {
            runOnUiThread {
                val dataSourceFactory = DefaultHttpDataSource.Factory()
                val mediaSource = HlsMediaSource.Factory(dataSourceFactory)
                    .createMediaSource(MediaItem.fromUri(url))
                exoPlayer.setMediaSource(mediaSource)
                exoPlayer.prepare()
                exoPlayer.playWhenReady = true
            }
        }

        @JavascriptInterface
        fun stop() {
            runOnUiThread {
                exoPlayer.stop()
                exoPlayer.clearMediaItems()
            }
        }

        @JavascriptInterface
        fun setScaleMode(mode: String) {
            runOnUiThread {
                // "fit", "fill", "zoom", "stretch"
                when (mode) {
                    "fit" -> playerView.resizeMode = PlayerView.RESIZE_MODE_FIT
                    "fill" -> playerView.resizeMode = PlayerView.RESIZE_MODE_FILL
                    "zoom" -> playerView.resizeMode = PlayerView.RESIZE_MODE_ZOOM
                    else -> playerView.resizeMode = PlayerView.RESIZE_MODE_FIT
                }
            }
        }

        @JavascriptInterface
        fun setResolution(level: Int) {
            // level: 0=low, 1=medium, 2=high, 3=auto
            runOnUiThread {
                val renderer = trackSelector.getParameters().buildUpon()
                    .setMaxVideoSize(if (level == 0) 640 else if (level == 1) 1280 else if (level == 2) 1920 else Int.MAX_VALUE,
                                      if (level == 0) 360 else if (level == 1) 720 else if (level == 2) 1080 else Int.MAX_VALUE)
                    .setMaxVideoBitrate(if (level == 0) 500000 else if (level == 1) 2000000 else if (level == 2) 8000000 else Int.MAX_VALUE)
                    .build()
                trackSelector.setParameters(renderer)
            }
        }

        @JavascriptInterface
        fun getCategories(): String {
            // Retorna las categorías desde la lista cargada (ejemplo)
            return "[]"
        }

        @JavascriptInterface
        fun getChannels(category: String): String {
            // Retorna canales filtrados por categoría
            return "[]"
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        exoPlayer.release()
    }
}
