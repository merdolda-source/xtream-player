// Android/app/src/main/kotlin/com/xtream/player/data/remote/api/XtreamUrlBuilder.kt
package com.xtream.player.data.remote.api

import com.xtream.player.domain.entities.Stream
import com.xtream.player.domain.entities.StreamType

/**
 * Builds the various Xtream Codes URLs from a server host + credentials.
 * Host is expected as e.g. "http://example.com:8080" (no trailing slash).
 */
object XtreamUrlBuilder {

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
                val ext = stream.containerExtension ?: "mp4"
                "$base/movie/$username/$password/${stream.streamId}.$ext"
            }
            StreamType.SERIES -> {
                // Series entries are containers; episode playback would use
                // get_series_info to resolve concrete episode stream ids.
                // For MVP we link straight through using the same scheme.
                val ext = stream.containerExtension ?: "mp4"
                "$base/series/$username/$password/${stream.streamId}.$ext"
            }
        }
    }
}
