// Android/app/src/main/kotlin/com/xtream/player/data/remote/api/XtreamUrlBuilder.kt
package com.erdin.player.data.remote.api

import com.erdin.player.domain.entities.Stream
import com.erdin.player.domain.entities.StreamType

/**
 * Builds the various Xtream Codes URLs from a server host + credentials.
 * Host is expected as e.g. "http://example.com:8080" (no trailing slash).
 */
object XtreamUrlBuilder {

    // HLS/transport-stream playlists are only ever served by Xtream panels
    // for live channels; movie/series endpoints don't generate them, so a
    // container_extension of "m3u8"/"ts" there resolves to a 404 instead of
    // playable media. Force a real file container in that case.
    private val LIVE_ONLY_EXTENSIONS = setOf("m3u8", "ts")

    fun playerApiUrl(host: String): String = "${host.trimEnd('/')}/player_api.php"

    /** Direct playback URL for a stream, per Xtream's standard URL scheme. */
    fun streamPlaybackUrl(host: String, username: String, password: String, stream: Stream): String {
        val base = host.trimEnd('/')
        return when (stream.streamType) {
            StreamType.LIVE -> {
                val ext = stream.containerExtension ?: "ts"
                "$base/live/$username/$password/${stream.streamId}.$ext"
            }
            StreamType.VOD -> {
                val ext = stream.containerExtension
                    ?.takeUnless { it.lowercase() in LIVE_ONLY_EXTENSIONS } ?: "mp4"
                "$base/movie/$username/$password/${stream.streamId}.$ext"
            }
            StreamType.SERIES -> {
                // Series entries are containers; episode playback would use
                // get_series_info to resolve concrete episode stream ids.
                // For MVP we link straight through using the same scheme.
                val ext = stream.containerExtension
                    ?.takeUnless { it.lowercase() in LIVE_ONLY_EXTENSIONS } ?: "mp4"
                "$base/series/$username/$password/${stream.streamId}.$ext"
            }
        }
    }
}
