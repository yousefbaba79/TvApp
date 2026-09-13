package com.baba.israelitv.data

/** One playable entry parsed out of an extended M3U playlist. */
data class M3uEntry(
    val name: String,
    val tvgId: String?,
    val userAgent: String?,
    val url: String
)

/**
 * Minimal parser for the extended-M3U format used by public IPTV playlists:
 *
 * ```
 * #EXTINF:-1 tvg-id="Kan11.il@SD",Kan 11 (1080p)
 * #EXTVLCOPT:http-user-agent=Mozilla/5.0 ...
 * https://.../playlist.m3u8
 * ```
 */
class M3uPlaylistParser {

    private val tvgIdRegex = Regex("tvg-id=\"([^\"]*)\"")
    private val userAgentRegex = Regex("#EXTVLCOPT:http-user-agent=(.*)", RegexOption.IGNORE_CASE)

    fun parse(playlistText: String): List<M3uEntry> {
        val entries = mutableListOf<M3uEntry>()

        var pendingName: String? = null
        var pendingTvgId: String? = null
        var pendingUserAgent: String? = null

        for (rawLine in playlistText.lineSequence()) {
            val line = rawLine.trim()
            if (line.isEmpty()) continue

            when {
                line.startsWith("#EXTINF") -> {
                    pendingTvgId = tvgIdRegex.find(line)?.groupValues?.get(1)
                    pendingName = line.substringAfterLast(',', missingDelimiterValue = "").trim()
                    pendingUserAgent = null
                }
                line.startsWith("#EXTVLCOPT:http-user-agent", ignoreCase = true) -> {
                    pendingUserAgent = userAgentRegex.find(line)?.groupValues?.get(1)?.trim()
                }
                line.startsWith("#") -> {
                    // Ignore other directives (#EXTM3U, #EXT-X-*, etc).
                }
                else -> {
                    // A non-comment, non-empty line is the stream URL for the pending entry.
                    entries += M3uEntry(
                        name = pendingName ?: line,
                        tvgId = pendingTvgId,
                        userAgent = pendingUserAgent,
                        url = line
                    )
                    pendingName = null
                    pendingTvgId = null
                    pendingUserAgent = null
                }
            }
        }

        return entries
    }
}
