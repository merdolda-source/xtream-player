// Android/app/src/main/kotlin/com/xtream/player/domain/entities/Stream.kt
package xtream.emin.player.domain.entities

/**
 * The kind of content a [Stream] represents. Xtream Codes exposes three
 * distinct catalog types via player_api.php; we normalize them into one
 * entity shape with a discriminator so the rest of the app (favorites,
 * history, player) can treat them uniformly.
 */
enum class StreamType {
    LIVE,
    VOD,
    SERIES
}

/**
 * A single playable (or browsable, for series) item from the Xtream Codes
 * catalog - a live channel, a VOD movie, or a series entry.
 */
data class Stream(
    val streamId: String,
    val name: String,
    val streamType: StreamType,
    val categoryId: String? = null,
    val streamIcon: String? = null,
    val containerExtension: String? = null,
    val added: String? = null,
    val rating: String? = null,
    val isAdult: Boolean = false
)
