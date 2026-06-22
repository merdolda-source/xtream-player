// Android/app/src/main/kotlin/com/xtream/player/domain/entities/Episode.kt
package com.xtream.player.domain.entities

/** A single playable episode within a series season, from `get_series_info`. */
data class Episode(
    val episodeId: String,
    val episodeNum: Int,
    val title: String,
    val season: Int,
    val containerExtension: String?,
    val plot: String? = null,
    val durationSecs: Int? = null,
    val cover: String? = null
)

/** Season metadata for a series, from `get_series_info`. */
data class Season(
    val seasonNumber: Int,
    val name: String,
    val cover: String? = null,
    val episodeCount: Int = 0
)

/** Full series detail: metadata plus seasons/episodes resolved via `get_series_info`. */
data class SeriesDetails(
    val seriesId: String,
    val name: String,
    val cover: String?,
    val plot: String?,
    val seasons: List<Season>,
    val episodesBySeason: Map<Int, List<Episode>>
)
