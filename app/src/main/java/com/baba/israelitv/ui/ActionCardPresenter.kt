package com.baba.israelitv.ui

import android.view.Gravity
import android.view.ViewGroup
import android.widget.TextView
import androidx.core.content.ContextCompat
import androidx.leanback.widget.Presenter
import com.baba.israelitv.R

/** Simple text-only card used for one-off actions in a row, e.g. "Refresh Channels". */
class ActionCardPresenter : Presenter() {

    override fun onCreateViewHolder(parent: ViewGroup): ViewHolder {
        val textView = TextView(parent.context).apply {
            isFocusable = true
            isFocusableInTouchMode = true
            gravity = Gravity.CENTER
            setBackgroundResource(R.drawable.channel_card_background)
            setTextColor(ContextCompat.getColor(parent.context, R.color.text_light))
            setPadding(32, 0, 32, 0)
            layoutParams = ViewGroup.LayoutParams(CARD_WIDTH, CARD_HEIGHT)
        }
        return ViewHolder(textView)
    }

    override fun onBindViewHolder(viewHolder: ViewHolder, item: Any) {
        (viewHolder.view as TextView).text = item as String
    }

    override fun onUnbindViewHolder(viewHolder: ViewHolder) = Unit

    companion object {
        private const val CARD_WIDTH = 313
        private const val CARD_HEIGHT = 176
    }
}
