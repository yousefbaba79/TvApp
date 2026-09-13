package com.baba.israelitv.data

import com.baba.israelitv.model.ChannelDefinition

private const val CHROME_USER_AGENT =
    "Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 " +
        "(KHTML, like Gecko) Chrome/124.0.0.0 Safari/537.36"

/**
 * The four channels the app cares about, with built-in fallback stream URLs.
 *
 * The fallback URLs are only used when the remote playlist (see [ChannelRepository])
 * can't be reached or doesn't contain a match, so the app still works offline-first
 * and doesn't require an app update the moment a broadcaster rotates its CDN link.
 */
object ChannelCatalog {

    val definitions: List<ChannelDefinition> = listOf(
        ChannelDefinition(
            id = "kan11",
            displayName = "Kan 11",
            tvgIdCandidates = listOf("kan11.il@sd", "kan11.il", "kan11"),
            nameCandidates = listOf("kan 11", "kan11"),
            fallbackUrl = "https://kancdn.medonecdn.net/livehls/oil/kancdn-live/live/kan11/live.livx/playlist.m3u8"
        ),
        ChannelDefinition(
            id = "keshet12",
            displayName = "Channel 12 (Keshet)",
            tvgIdCandidates = listOf("keshet12.il@sd", "keshet12.il", "keshet12"),
            nameCandidates = listOf("keshet 12", "channel 12"),
            fallbackUrl = "http://stream.mcquack.net/294/index.m3u8",
            fallbackUserAgent = CHROME_USER_AGENT
        ),
        ChannelDefinition(
            id = "channel13",
            displayName = "Channel 13 (Reshet)",
            tvgIdCandidates = listOf("channel13.il@sd", "channel13.il", "channel13"),
            nameCandidates = listOf("channel 13", "reshet 13"),
            fallbackUrl = "https://d2xg1g9o5vns8m.cloudfront.net/out/v1/0855d703f7d5436fae6a9c7ce8ca5075/index.m3u8"
        ),
        ChannelDefinition(
            id = "channel14",
            displayName = "Channel 14",
            tvgIdCandidates = listOf("now14.il@sd", "now14.il", "now14", "channel14.il"),
            nameCandidates = listOf("now 14", "channel 14"),
            fallbackUrl = "https://r.il.cdn-redge.media/livehls/oil/ch14/live/ch14/live.livx/playlist.m3u8"
        )
    )
}
