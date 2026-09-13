package com.baba.israelitv.model

/**
 * Static definition of a channel we want to show, including where to look for it
 * in a fetched playlist and what to fall back to if it can't be found there.
 */
data class ChannelDefinition(
    val id: String,
    val displayName: String,
    val tvgIdCandidates: List<String>,
    val nameCandidates: List<String>,
    val fallbackUrl: String,
    val fallbackUserAgent: String? = null
)

enum class ResolutionSource {
    /** URL was just fetched live from the remote playlist. */
    REMOTE_PLAYLIST,

    /** Remote playlist didn't have a match (or couldn't be fetched); used the built-in default. */
    FALLBACK
}

/** A channel with a stream URL that is ready to hand to the player. */
data class ResolvedChannel(
    val id: String,
    val displayName: String,
    val streamUrl: String,
    val userAgent: String?,
    val source: ResolutionSource
)
