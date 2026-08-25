package com.worksmart.iptv

import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.core.content.ContextCompat
import androidx.fragment.app.FragmentActivity
import androidx.leanback.app.BrowseSupportFragment
import androidx.leanback.widget.ArrayObjectAdapter
import androidx.leanback.widget.HeaderItem
import androidx.leanback.widget.ListRow
import androidx.leanback.widget.ListRowPresenter
import androidx.leanback.widget.OnItemViewClickedListener
import androidx.leanback.widget.Presenter
import androidx.leanback.widget.Row
import androidx.leanback.widget.RowPresenter
import androidx.core.app.ActivityOptionsCompat

class TvActivity : FragmentActivity() {

    private lateinit var mBrowseFragment: BrowseSupportFragment

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_tv)

        mBrowseFragment = supportFragmentManager.findFragmentById(R.id.browse_fragment) as BrowseSupportFragment
        
        setupUI()
        loadData()
    }

    private fun setupUI() {
        mBrowseFragment.apply {
            title = "Work Smart TV"
            headersState = BrowseSupportFragment.HEADERS_ENABLED
            
            // Logo en el header
            badgeDrawable = ContextCompat.getDrawable(this@TvActivity, R.drawable.logo)
            
            // Setup adapter
            adapter = ArrayObjectAdapter(ListRowPresenter())
            
            // Click listener
            onItemViewClickedListener = ItemViewClickedListener()
        }
    }

    private fun loadData() {
        val rowsAdapter = ArrayObjectAdapter(ListRowPresenter())
        
        // Sección: TV en Vivo
        val liveHeader = HeaderItem(0, "TV en Vivo")
        val liveAdapter = ArrayObjectAdapter(CardPresenter())
        
        // Aquí cargarías tus canales
        // Por ahora datos de ejemplo:
        liveAdapter.add(ChannelItem("Canal 1", "https://logo.png", "http://stream.m3u8"))
        liveAdapter.add(ChannelItem("Canal 2", "https://logo.png", "http://stream.m3u8"))
        liveAdapter.add(ChannelItem("Canal 3", "https://logo.png", "http://stream.m3u8"))
        
        rowsAdapter.add(ListRow(liveHeader, liveAdapter))
        
        // Sección: Películas
        val moviesHeader = HeaderItem(1, "Películas")
        val moviesAdapter = ArrayObjectAdapter(CardPresenter())
        
        moviesAdapter.add(ChannelItem("Película 1", "https://poster.jpg", "http://movie.mp4"))
        moviesAdapter.add(ChannelItem("Película 2", "https://poster.jpg", "http://movie.mp4"))
        
        rowsAdapter.add(ListRow(moviesHeader, moviesAdapter))
        
        // Sección: Series
        val seriesHeader = HeaderItem(2, "Series")
        val seriesAdapter = ArrayObjectAdapter(CardPresenter())
        
        seriesAdapter.add(ChannelItem("Serie 1", "https://poster.jpg", "http://serie.mp4"))
        
        rowsAdapter.add(ListRow(seriesHeader, seriesAdapter))
        
        mBrowseFragment.adapter = rowsAdapter
    }

    private inner class ItemViewClickedListener : OnItemViewClickedListener {
        override fun onItemClicked(
            itemViewHolder: Presenter.ViewHolder?,
            item: Any?,
            rowViewHolder: RowPresenter.ViewHolder?,
            row: Row?
        ) {
            if (item is ChannelItem) {
                val intent = Intent(this@TvActivity, PlayerActivity::class.java).apply {
                    putExtra("STREAM_URL", item.streamUrl)
                    putExtra("TITLE", item.name)
                }
                startActivity(intent)
            }
        }
    }

    data class ChannelItem(
        val name: String,
        val logo: String,
        val streamUrl: String
    )

    // Presenter para las tarjetas
    inner class CardPresenter : Presenter() {
        override fun onCreateViewHolder(parent: ViewGroup): ViewHolder {
            val view = layoutInflater.inflate(R.layout.tv_card, parent, false)
            return ViewHolder(view)
        }

        override fun onBindViewHolder(viewHolder: ViewHolder, item: Any?) {
            val channel = item as ChannelItem
            // Configurar la vista con los datos del canal
        }

        override fun onUnbindViewHolder(viewHolder: ViewHolder?) {
            // Limpiar recursos
        }
    }
}
