package com.worksmart.iptv

import android.content.Intent
import android.os.Bundle
import android.webkit.JavascriptInterface
import android.webkit.WebChromeClient
import android.webkit.WebSettings
import android.webkit.WebView
import android.webkit.WebViewClient
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.WindowCompat
import androidx.core.view.WindowInsetsCompat
import androidx.core.view.WindowInsetsControllerCompat
import com.worksmart.iptv.model.Playlist

class MainActivity : AppCompatActivity() {

    private lateinit var webView: WebView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        
        // Pantalla completa
        WindowCompat.setDecorFitsSystemWindows(window, false)
        WindowInsetsControllerCompat(window, window.decorView).let { controller ->
            controller.hide(WindowInsetsCompat.Type.systemBars())
            controller.systemBarsBehavior = WindowInsetsControllerCompat.BEHAVIOR_SHOW_TRANSIENT_BARS_BY_SWIPE
        }
        
        setContentView(R.layout.activity_main)
        
        webView = findViewById(R.id.webView)
        setupWebView()
    }

    private fun setupWebView() {
        webView.settings.apply {
            javaScriptEnabled = true
            domStorageEnabled = true
            databaseEnabled = true
            cacheMode = WebSettings.LOAD_DEFAULT
            mediaPlaybackRequiresUserGesture = false
            allowFileAccess = true
            allowContentAccess = true
        }
        
        webView.webChromeClient = WebChromeClient()
        webView.webViewClient = WebViewClient()
        
        // Interfaz JavaScript para comunicación nativa
        webView.addJavascriptInterface(WebAppInterface(), "Android")
        
        // Cargar interfaz
        webView.loadUrl("file:///android_asset/web/index.html")
    }

    inner class WebAppInterface {
        
        @JavascriptInterface
        fun playStream(url: String, title: String) {
            val intent = Intent(this@MainActivity, PlayerActivity::class.java).apply {
                putExtra("STREAM_URL", url)
                putExtra("TITLE", title)
            }
            startActivity(intent)
        }
        
        @JavascriptInterface
        fun addM3UPlaylist(url: String, name: String) {
            // Guardar playlist M3U
            val prefs = getSharedPreferences("playlists", MODE_PRIVATE)
            val playlists = prefs.getStringSet("m3u_list", mutableSetOf()) ?: mutableSetOf()
            playlists.add("$name|$url")
            prefs.edit().putStringSet("m3u_list", playlists).apply()
        }
        
        @JavascriptInterface
        fun addXtreamCodes(server: String, username: String, password: String, name: String) {
            // Guardar credenciales Xtream Codes
            val prefs = getSharedPreferences("playlists", MODE_PRIVATE)
            val xtream = prefs.getStringSet("xtream_list", mutableSetOf()) ?: mutableSetOf()
            xtream.add("$name|$server|$username|$password")
            prefs.edit().putStringSet("xtream_list", xtream).apply()
        }
        
        @JavascriptInterface
        fun getPlaylists(): String {
            val prefs = getSharedPreferences("playlists", MODE_PRIVATE)
            val m3u = prefs.getStringSet("m3u_list", setOf()) ?: setOf()
            val xtream = prefs.getStringSet("xtream_list", setOf()) ?: setOf()
            
            return buildString {
                append("{")
                append("\"m3u\":[")
                append(m3u.joinToString(",") { "\"$it\"" })
                append("],")
                append("\"xtream\":[")
                append(xtream.joinToString(",") { "\"$it\"" })
                append("]")
                append("}")
            }
        }
    }

    override fun onBackPressed() {
        if (webView.canGoBack()) {
            webView.goBack()
        } else {
            super.onBackPressed()
        }
    }
}
