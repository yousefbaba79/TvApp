package com.baba.israelitv.ui

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.baba.israelitv.R
import com.baba.israelitv.model.ResolutionSource
import com.baba.israelitv.model.ResolvedChannel

class MobileChannelAdapter(
    private val channels: List<ResolvedChannel>,
    private val onChannelClicked: (ResolvedChannel) -> Unit
) : RecyclerView.Adapter<MobileChannelAdapter.ViewHolder>() {

    class ViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val nameView: TextView = view.findViewById(R.id.channel_name)
        val statusView: TextView = view.findViewById(R.id.channel_status)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_channel_mobile, parent, false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val channel = channels[position]
        holder.nameView.text = channel.displayName
        holder.statusView.text = when (channel.source) {
            ResolutionSource.REMOTE_PLAYLIST -> holder.itemView.context.getString(R.string.status_live_link)
            ResolutionSource.FALLBACK -> holder.itemView.context.getString(R.string.status_fallback_link)
        }
        holder.itemView.setOnClickListener { onChannelClicked(channel) }
    }

    override fun getItemCount(): Int = channels.size
}
