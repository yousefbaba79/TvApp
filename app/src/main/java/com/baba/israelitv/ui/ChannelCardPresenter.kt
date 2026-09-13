package com.baba.israelitv.ui

import android.graphics.drawable.ColorDrawable
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.leanback.widget.ImageCardView
import androidx.leanback.widget.Presenter
import com.baba.israelitv.R
import com.baba.israelitv.model.ResolutionSource
import com.baba.israelitv.model.ResolvedChannel

class ChannelCardPresenter : Presenter() {

    override fun onCreateViewHolder(parent: ViewGroup): ViewHolder {
        val cardView = ImageCardView(parent.context)
        cardView.isFocusable = true
        cardView.isFocusableInTouchMode = true
        cardView.setMainImageDimensions(CARD_WIDTH, CARD_HEIGHT)
        return ViewHolder(cardView)
    }

    override fun onBindViewHolder(viewHolder: ViewHolder, item: Any) {
        val channel = item as ResolvedChannel
        val cardView = viewHolder.view as ImageCardView
        cardView.titleText = channel.displayName
        cardView.contentText = when (channel.source) {
            ResolutionSource.REMOTE_PLAYLIST -> cardView.context.getString(R.string.status_live_link)
            ResolutionSource.FALLBACK -> cardView.context.getString(R.string.status_fallback_link)
        }
        cardView.setMainImageDimensions(CARD_WIDTH, CARD_HEIGHT)
        cardView.mainImage = ColorDrawable(ContextCompat.getColor(cardView.context, R.color.card_default))
    }

    override fun onUnbindViewHolder(viewHolder: ViewHolder) {
        val cardView = viewHolder.view as ImageCardView
        cardView.badgeImage = null
        cardView.mainImage = null
    }

    companion object {
        private const val CARD_WIDTH = 313
        private const val CARD_HEIGHT = 176
    }
}
