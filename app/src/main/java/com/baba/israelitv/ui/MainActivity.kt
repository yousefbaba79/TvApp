package com.baba.israelitv.ui

import android.app.UiModeManager
import android.content.pm.ActivityInfo
import android.content.res.Configuration
import android.os.Bundle
import androidx.fragment.app.FragmentActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.baba.israelitv.R
import com.baba.israelitv.data.ChannelCatalog

class MainActivity : FragmentActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        if (isTvDevice()) {
            requestedOrientation = ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE
            setContentView(R.layout.activity_main)
        } else {
            setContentView(R.layout.activity_main_mobile)
            setUpMobileChannelList()
        }
    }

    private fun isTvDevice(): Boolean {
        val uiModeManager = getSystemService(UI_MODE_SERVICE) as? UiModeManager
        return uiModeManager?.currentModeType == Configuration.UI_MODE_TYPE_TELEVISION
    }

    private fun setUpMobileChannelList() {
        val channels = ChannelCatalog.asFallbackChannels()

        findViewById<RecyclerView>(R.id.mobile_channel_list).apply {
            layoutManager = LinearLayoutManager(this@MainActivity)
            adapter = MobileChannelAdapter(channels) { channel ->
                startActivity(PlaybackActivity.newIntent(this@MainActivity, channel))
            }
        }
    }
}
