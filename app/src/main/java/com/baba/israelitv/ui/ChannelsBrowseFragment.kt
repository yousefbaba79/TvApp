package com.baba.israelitv.ui

import android.os.Bundle
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
import com.baba.israelitv.model.ResolutionSource
import com.baba.israelitv.model.ResolvedChannel

class ChannelsBrowseFragment : BrowseSupportFragment() {

    private lateinit var rowsAdapter: ArrayObjectAdapter
    private lateinit var channelsRowAdapter: ArrayObjectAdapter

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)

        title = getString(R.string.browse_title)
        headersState = HEADERS_DISABLED
        isHeadersTransitionOnBackEnabled = false
        brandColor = ContextCompat.getColor(requireContext(), R.color.brand_blue)

        rowsAdapter = ArrayObjectAdapter(ListRowPresenter())
        adapter = rowsAdapter

        channelsRowAdapter = ArrayObjectAdapter(ChannelCardPresenter())
        val channelsHeader = HeaderItem(0, getString(R.string.browse_title))
        rowsAdapter.add(ListRow(channelsHeader, channelsRowAdapter))

        onItemViewClickedListener = OnItemViewClickedListener {
            _: Presenter.ViewHolder?, item: Any?, _: RowPresenter.ViewHolder?, _: Row? ->
            val channel = item as? ResolvedChannel ?: return@OnItemViewClickedListener
            startActivity(PlaybackActivity.newIntent(requireContext(), channel))
        }

        loadChannels()
    }

    private fun loadChannels() {
        channelsRowAdapter.clear()
        ChannelCatalog.definitions.forEach { definition ->
            channelsRowAdapter.add(
                ResolvedChannel(
                    id = definition.id,
                    displayName = definition.displayName,
                    streamUrl = definition.fallbackUrl,
                    userAgent = definition.fallbackUserAgent,
                    source = ResolutionSource.FALLBACK
                )
            )
        }
    }
}
