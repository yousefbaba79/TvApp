package com.baba.israelitv.data

import com.baba.israelitv.model.ChannelDefinition
import com.baba.israelitv.model.ResolutionSource
import com.baba.israelitv.model.ResolvedChannel
import java.io.IOException
import java.util.concurrent.TimeUnit
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import okhttp3.OkHttpClient
import okhttp3.Request

/**
 * Resolves each catalog channel to a live stream URL.
 *
 * The "find the stream URL" behaviour works by fetching a community-maintained,
 * regularly-updated public playlist at runtime and matching entries against the
 * known channels, instead of hard-coding URLs that go stale whenever a broadcaster
 * moves its CDN. If the fetch fails, or a channel isn't listed, we fall back to the
 * last-known-good URL baked into [ChannelCatalog].
 */
class ChannelRepository(
    private val playlistUrl: String = DEFAULT_PLAYLIST_URL,
    private val definitions: List<ChannelDefinition> = ChannelCatalog.definitions
) {

    private val client = OkHttpClient.Builder()
        .connectTimeout(10, TimeUnit.SECONDS)
        .readTimeout(10, TimeUnit.SECONDS)
        .build()

    private val parser = M3uPlaylistParser()

    suspend fun resolveChannels(): List<ResolvedChannel> = withContext(Dispatchers.IO) {
        val entries = try {
            fetchPlaylistEntries()
        } catch (e: IOException) {
            emptyList()
        }
        definitions.map { definition -> resolve(definition, entries) }
    }

    suspend fun resolveChannel(id: String): ResolvedChannel? =
        resolveChannels().firstOrNull { it.id == id }

    private fun fetchPlaylistEntries(): List<M3uEntry> {
        val request = Request.Builder().url(playlistUrl).build()
        client.newCall(request).execute().use { response ->
            if (!response.isSuccessful) {
                throw IOException("Unexpected response ${response.code} for $playlistUrl")
            }
            val body = response.body?.string() ?: throw IOException("Empty playlist body")
            return parser.parse(body)
        }
    }

    private fun resolve(definition: ChannelDefinition, entries: List<M3uEntry>): ResolvedChannel {
        val match = entries.firstOrNull { matchesTvgId(it, definition) }
            ?: entries.firstOrNull { matchesName(it, definition) }

        return if (match != null) {
            ResolvedChannel(
                id = definition.id,
                displayName = definition.displayName,
                streamUrl = match.url,
                userAgent = match.userAgent ?: definition.fallbackUserAgent,
                source = ResolutionSource.REMOTE_PLAYLIST
            )
        } else {
            ResolvedChannel(
                id = definition.id,
                displayName = definition.displayName,
                streamUrl = definition.fallbackUrl,
                userAgent = definition.fallbackUserAgent,
                source = ResolutionSource.FALLBACK
            )
        }
    }

    private fun matchesTvgId(entry: M3uEntry, definition: ChannelDefinition): Boolean {
        val tvgId = entry.tvgId?.lowercase() ?: return false
        return definition.tvgIdCandidates.any { candidate -> tvgId == candidate.lowercase() }
    }

    private fun matchesName(entry: M3uEntry, definition: ChannelDefinition): Boolean {
        val name = entry.name.lowercase()
        return definition.nameCandidates.any { candidate -> name.contains(candidate.lowercase()) }
    }

    companion object {
        const val DEFAULT_PLAYLIST_URL =
            "https://raw.githubusercontent.com/iptv-org/iptv/master/streams/il.m3u"
    }
}
