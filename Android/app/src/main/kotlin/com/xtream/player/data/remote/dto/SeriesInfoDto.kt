// Android/app/src/main/kotlin/com/xtream/player/data/remote/dto/SeriesInfoDto.kt
package com.xtream.player.data.remote.dto

import com.google.gson.annotations.SerializedName
import com.xtream.player.domain.entities.Episode
import com.xtream.player.domain.entities.Season
import com.xtream.player.domain.entities.SeriesDetails

/** Response shape for `get_series_info`. */
data class SeriesInfoResponseDto(
    @SerializedName("seasons") val seasons: List<SeasonDto>? = null,
    @SerializedName("info") val info: SeriesInfoMetaDto? = null,
    // Xtream keys this map by season number as a string, e.g. {"1": [...], "2": [...]}.
    @SerializedName("episodes") val episodes: Map<String, List<EpisodeDto>>? = null
)

data class SeasonDto(
    @SerializedName("season_number") val seasonNumber: Int? = null,
    @SerializedName("name") val name: String? = null,
    @SerializedName("cover") val cover: String? = null,
    @SerializedName("episode_count") val episodeCount: Int? = null
)

data class SeriesInfoMetaDto(
    @SerializedName("name") val name: String? = null,
    @SerializedName("cover") val cover: String? = null,
    @SerializedName("plot") val plot: String? = null
)

data class EpisodeDto(
    @SerializedName("id") val id: String? = null,
    @SerializedName("episode_num") val episodeNum: Int? = null,
    @SerializedName("title") val title: String? = null,
    @SerializedName("container_extension") val containerExtension: String? = null,
    @SerializedName("season") val season: Int? = null,
    @SerializedName("info") val info: EpisodeInfoDto? = null
)

data class EpisodeInfoDto(
    @SerializedName("plot") val plot: String? = null,
    @SerializedName("duration_secs") val durationSecs: Int? = null,
    @SerializedName("movie_image") val movieImage: String? = null
)

private fun EpisodeDto.toDomain(fallbackSeason: Int): Episode? {
    val episodeId = id ?: return null
    val num = episodeNum ?: 0
    return Episode(
        episodeId = episodeId,
        episodeNum = num,
        title = title?.takeUnless { it.isBlank() } ?: "Bölüm $num",
        season = season ?: fallbackSeason,
        containerExtension = containerExtension,
        plot = info?.plot,
        durationSecs = info?.durationSecs,
        cover = info?.movieImage
    )
}

/**
 * Maps the raw `get_series_info` response into a [SeriesDetails]. Some Xtream
 * panels omit the `seasons` array while still returning grouped `episodes` -
 * in that case season metadata is derived from the episode groups themselves
 * so the UI always has something to show season tabs for.
 */
fun SeriesInfoResponseDto.toDomain(seriesId: String, fallbackName: String, fallbackCover: String?): SeriesDetails {
    val episodesBySeason = episodes.orEmpty()
        .mapNotNull { (seasonKey, episodeDtos) ->
            val seasonNumber = seasonKey.toIntOrNull() ?: return@mapNotNull null
            seasonNumber to episodeDtos.mapNotNull { it.toDomain(seasonNumber) }.sortedBy { it.episodeNum }
        }
        .toMap()

    val declaredSeasons = seasons.orEmpty()
        .mapNotNull { dto ->
            val num = dto.seasonNumber ?: return@mapNotNull null
            Season(
                seasonNumber = num,
                name = dto.name?.takeUnless { it.isBlank() } ?: "Sezon $num",
                cover = dto.cover,
                episodeCount = dto.episodeCount ?: episodesBySeason[num]?.size ?: 0
            )
        }

    val resolvedSeasons = (declaredSeasons.ifEmpty {
        episodesBySeason.keys.map { num ->
            Season(seasonNumber = num, name = "Sezon $num", episodeCount = episodesBySeason[num]?.size ?: 0)
        }
    }).sortedBy { it.seasonNumber }

    return SeriesDetails(
        seriesId = seriesId,
        name = info?.name?.takeUnless { it.isBlank() } ?: fallbackName,
        cover = info?.cover ?: fallbackCover,
        plot = info?.plot,
        seasons = resolvedSeasons,
        episodesBySeason = episodesBySeason
    )
}
