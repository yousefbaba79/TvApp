package com.baba.israelitv.ui

import android.os.Bundle
import android.widget.Toast
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
import androidx.lifecycle.lifecycleScope
import com.baba.israelitv.R
import com.baba.israelitv.data.ChannelRepository
import com.baba.israelitv.model.ResolvedChannel
import kotlinx.coroutines.launch

class ChannelsBrowseFragment : BrowseSupportFragment() {

    private val repository = ChannelRepository()
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

        val actionsAdapter = ArrayObjectAdapter(ActionCardPresenter())
        actionsAdapter.add(getString(R.string.action_refresh))
        val actionsHeader = HeaderItem(1, getString(R.string.actions_header))
        rowsAdapter.add(ListRow(actionsHeader, actionsAdapter))

        onItemViewClickedListener = OnItemViewClickedListener {
            _: Presenter.ViewHolder?, item: Any?, _: RowPresenter.ViewHolder?, _: Row? ->
            when (item) {
                is ResolvedChannel -> startActivity(PlaybackActivity.newIntent(requireContext(), item))
                is String -> loadChannels()
            }
        }

        loadChannels()
    }

    private fun loadChannels() {
        Toast.makeText(requireContext(), R.string.resolving_channels, Toast.LENGTH_SHORT).show()
        viewLifecycleOwner.lifecycleScope.launch {
            val resolved = repository.resolveChannels()
            channelsRowAdapter.clear()
            resolved.forEach { channelsRowAdapter.add(it) }
        }
    }
}
