package com.baba.israelitv.ui

import android.os.Bundle
import android.util.Log
import androidx.core.content.ContextCompat
import androidx.leanback.app.BrowseSupportFragment
import androidx.leanback.widget.ArrayObjectAdapter
import androidx.leanback.widget.HeaderItem
import androidx.leanback.widget.ListRow
import androidx.leanback.widget.ListRowPresenter
import androidx.leanback.widget.OnItemViewClickedListener
import androidx.leanback.widget.Presenter
import androidx.leanback.widget.Row
import androidx.leanback.widget.RowPresenter
import com.baba.israelitv.R
import com.baba.israelitv.data.ChannelCatalog
import com.baba.israelitv.model.ResolvedChannel

class ChannelsBrowseFragment : BrowseSupportFragment() {

    private lateinit var rowsAdapter: ArrayObjectAdapter
    private lateinit var channelsRowAdapter: ArrayObjectAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        // Must be set before the fragment's view hierarchy is created, otherwise the
        // rows pane can be left sized/laid out as if headers were still enabled.
        headersState = HEADERS_DISABLED
        isHeadersTransitionOnBackEnabled = false
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        Log.d(TAG, "onActivityCreated")

        title = getString(R.string.browse_title)
        brandColor = ContextCompat.getColor(requireContext(), R.color.brand_blue)

        rowsAdapter = ArrayObjectAdapter(ListRowPresenter())
        adapter = rowsAdapter

        channelsRowAdapter = ArrayObjectAdapter(ChannelCardPresenter())
        val channelsHeader = HeaderItem(0, getString(R.string.browse_title))
        rowsAdapter.add(ListRow(channelsHeader, channelsRowAdapter))
        Log.d(TAG, "rowsAdapter row count after add: ${rowsAdapter.size()}")

        onItemViewClickedListener = OnItemViewClickedListener {
            _: Presenter.ViewHolder?, item: Any?, _: RowPresenter.ViewHolder?, _: Row? ->
            val channel = item as? ResolvedChannel ?: return@OnItemViewClickedListener
            startActivity(PlaybackActivity.newIntent(requireContext(), channel))
        }

        loadChannels()
    }

    private fun loadChannels() {
        channelsRowAdapter.clear()
        val channels = ChannelCatalog.asFallbackChannels()
        Log.d(TAG, "loadChannels: catalog has ${channels.size} channels")
        channels.forEach { channelsRowAdapter.add(it) }
        Log.d(TAG, "loadChannels: channelsRowAdapter now has ${channelsRowAdapter.size()} items")
    }

    companion object {
        private const val TAG = "IsraelTV"
    }
}
