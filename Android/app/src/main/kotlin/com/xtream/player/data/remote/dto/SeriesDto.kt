// Android/app/src/main/kotlin/com/xtream/player/data/remote/dto/SeriesDto.kt
package com.xtream.player.data.remote.dto

import com.google.gson.annotations.SerializedName
import com.xtream.player.domain.entities.Stream
import com.xtream.player.domain.entities.StreamType

/** Response item shape for `get_series`. */
data class SeriesDto(
    @SerializedName("series_id") val seriesId: Int? = null,
    @SerializedName("num") val num: Int? = null,
    @SerializedName("name") val name: String? = null,
    @SerializedName("category_id") val categoryId: String? = null,
    @SerializedName("cover") val cover: String? = null,
    @SerializedName("last_modified") val lastModified: String? = null,
    @SerializedName("rating") val rating: String? = null
)

fun SeriesDto.toDomain(): Stream = Stream(
    streamId = seriesId?.toString().orEmpty(),
    name = name.orEmpty(),
    streamType = StreamType.SERIES,
    categoryId = categoryId,
    streamIcon = cover,
    containerExtension = null,
    added = lastModified,
    rating = rating,
    isAdult = false
)
