// Android/app/src/main/kotlin/com/xtream/player/data/remote/dto/StreamDto.kt
package com.erdin.player.data.remote.dto

import com.google.gson.annotations.SerializedName
import com.erdin.player.domain.entities.Stream
import com.erdin.player.domain.entities.StreamType

/** Response item shape for `get_live_streams` and `get_vod_streams`. */
data class StreamDto(
    @SerializedName("stream_id") val streamId: Int? = null,
    @SerializedName("num") val num: Int? = null,
    @SerializedName("name") val name: String? = null,
    @SerializedName("stream_type") val streamType: String? = null,
    @SerializedName("category_id") val categoryId: String? = null,
    @SerializedName("stream_icon") val streamIcon: String? = null,
    @SerializedName("container_extension") val containerExtension: String? = null,
    @SerializedName("added") val added: String? = null,
    @SerializedName("rating") val rating: String? = null,
    @SerializedName("is_adult") val isAdult: String? = null
)

fun StreamDto.toDomain(type: StreamType): Stream = Stream(
    streamId = streamId?.toString().orEmpty(),
    name = name.orEmpty(),
    streamType = type,
    categoryId = categoryId,
    streamIcon = streamIcon,
    containerExtension = containerExtension,
    added = added,
    rating = rating,
    isAdult = isAdult == "1"
)
