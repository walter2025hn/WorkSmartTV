package com.email.iptvplayer

import android.annotation.SuppressLint
import android.os.Bundle
import android.webkit.WebView
import android.webkit.WebViewClient
import androidx.appcompat.app.AppCompatActivity
import androidx.media3.common.MediaItem
import androidx.media3.exoplayer.ExoPlayer
import androidx.media3.ui.AspectRatioFrameLayout
import androidx.media3.ui.PlayerView

class PlayerActivity : AppCompatActivity() {

    private var player: ExoPlayer? = null
    private lateinit var playerView: PlayerView
    private lateinit var webView: WebView // Soluciona el error "Unresolved reference: 'webView'"

    @SuppressLint("SetJavaScriptEnabled")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_player)

        // Inicializar el WebView (si es para IPTV basado en web)
        webView = findViewById(R.id.webView) // Asegúrate de que este ID exista en tu activity_player.xml
        webView.settings.javaScriptEnabled = true
        webView.webViewClient = WebViewClient()

        // Inicializar el PlayerView
        playerView = findViewById(R.id.playerView) // Asegúrate de que este ID exista en tu activity_player.xml

        // Configurar el tamaño de visualización (RESIZE_MODE)
        // Usamos AspectRatioFrameLayout.RESIZE_MODE_ZOOM para pantalla completa sin bordes negros.
        // También existen: RESIZE_MODE_FIT, RESIZE_MODE_FILL, RESIZE_MODE_FIXED_WIDTH, etc.
        playerView.resizeMode = AspectRatioFrameLayout.RESIZE_MODE_ZOOM

        // Crear el ExoPlayer
        player = ExoPlayer.Builder(this).build()
        playerView.player = player

        // Obtener la URL del intent (ejemplo típico en IPTV)
        val videoUrl = intent.getStringExtra("video_url") ?: "https://ejemplo.com/stream.m3u8"

        // Cargar el video en el reproductor
        val mediaItem = MediaItem.fromUri(videoUrl)
        player?.setMediaItem(mediaItem)
        player?.prepare()
        player?.playWhenReady = true
        
        // Si en tu código usabas WebView para cargar la URL, puedes cargarla aquí:
        // webView.loadUrl(videoUrl)
    }

    override fun onStart() {
        super.onStart()
        player?.play()
    }

    override fun onStop() {
        super.onStop()
        player?.pause()
    }

    override fun onDestroy() {
        super.onDestroy()
        player?.release()
        player = null
    }
}
