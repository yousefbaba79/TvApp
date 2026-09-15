package com.baba.israelitv.ui

import android.graphics.drawable.ColorDrawable
import android.util.Log
import android.util.TypedValue
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
        val (widthPx, heightPx) = cardDimensionsPx(parent)
        Log.d(TAG, "onCreateViewHolder: ${widthPx}x${heightPx}px")
        cardView.setMainImageDimensions(widthPx, heightPx)
        return ViewHolder(cardView)
    }

    override fun onBindViewHolder(viewHolder: ViewHolder, item: Any) {
        val channel = item as ResolvedChannel
        Log.d(TAG, "onBindViewHolder: ${channel.displayName}")
        val cardView = viewHolder.view as ImageCardView
        cardView.titleText = channel.displayName
        cardView.contentText = when (channel.source) {
            ResolutionSource.REMOTE_PLAYLIST -> cardView.context.getString(R.string.status_live_link)
            ResolutionSource.FALLBACK -> cardView.context.getString(R.string.status_fallback_link)
        }
        cardView.mainImage = ColorDrawable(ContextCompat.getColor(cardView.context, R.color.card_highlight))
    }

    override fun onUnbindViewHolder(viewHolder: ViewHolder) {
        val cardView = viewHolder.view as ImageCardView
        cardView.badgeImage = null
        cardView.mainImage = null
    }

    private fun cardDimensionsPx(view: ViewGroup): Pair<Int, Int> {
        val widthPx = TypedValue.applyDimension(
            TypedValue.COMPLEX_UNIT_DIP, CARD_WIDTH_DP, view.resources.displayMetrics
        ).toInt()
        val heightPx = TypedValue.applyDimension(
            TypedValue.COMPLEX_UNIT_DIP, CARD_HEIGHT_DP, view.resources.displayMetrics
        ).toInt()
        return widthPx to heightPx
    }

    companion object {
        private const val TAG = "IsraelTV"
        private const val CARD_WIDTH_DP = 313f
        private const val CARD_HEIGHT_DP = 176f
    }
}
