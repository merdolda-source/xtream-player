// Android/app/src/main/kotlin/com/xtream/player/domain/repositories/StreamRepository.kt
package com.erdin.player.domain.repositories

import com.erdin.player.domain.entities.Category
import com.erdin.player.domain.entities.SeriesDetails
import com.erdin.player.domain.entities.Stream

interface StreamRepository {
    suspend fun getLiveCategories(host: String, username: String, password: String): List<Category>

    suspend fun getVodCategories(host: String, username: String, password: String): List<Category>

    suspend fun getSeriesCategories(host: String, username: String, password: String): List<Category>

    suspend fun getLiveStreams(
        host: String,
        username: String,
        password: String,
        categoryId: String? = null
    ): List<Stream>
    
    suspend fun getVODStreams(
        host: String,
        username: String,
        password: String,
        categoryId: String? = null
    ): List<Stream>
    
    suspend fun getSeries(
        host: String,
        username: String,
        password: String,
        categoryId: String? = null
    ): List<Stream>

    suspend fun getSeriesInfo(
        host: String,
        username: String,
        password: String,
        seriesId: String,
        fallbackName: String,
        fallbackCover: String?
    ): SeriesDetails

    suspend fun searchStreams(query: String): List<Stream>
    
    suspend fun getFavorites(): List<Stream>

    suspend fun addFavorite(stream: Stream)

    suspend fun removeFavorite(streamId: String)

    suspend fun isFavorite(streamId: String): Boolean
    
    suspend fun getWatchHistory(): List<Stream>
}
