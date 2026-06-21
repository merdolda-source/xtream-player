// Android/app/src/main/kotlin/com/xtream/player/data/local/entities/FavoriteEntity.kt
package com.xtream.player.data.local.entities

import androidx.room.Entity
import androidx.room.PrimaryKey
import com.xtream.player.domain.entities.Stream
import com.xtream.player.domain.entities.StreamType

@Entity(tableName = "favorites")
data class FavoriteEntity(
    @PrimaryKey val streamId: String,
    val name: String,
    val streamType: String,
    val categoryId: String?,
    val streamIcon: String?,
    val containerExtension: String?,
    val added: String?,
    val rating: String?,
    val isAdult: Boolean,
    val favoritedAt: Long = System.currentTimeMillis()
)

fun FavoriteEntity.toDomain(): Stream = Stream(
    streamId = streamId,
    name = name,
    streamType = StreamType.valueOf(streamType),
    categoryId = categoryId,
    streamIcon = streamIcon,
    containerExtension = containerExtension,
    added = added,
    rating = rating,
    isAdult = isAdult
)

fun Stream.toFavoriteEntity(): FavoriteEntity = FavoriteEntity(
    streamId = streamId,
    name = name,
    streamType = streamType.name,
    categoryId = categoryId,
    streamIcon = streamIcon,
    containerExtension = containerExtension,
    added = added,
    rating = rating,
    isAdult = isAdult
)
