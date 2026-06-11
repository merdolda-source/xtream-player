// Android/app/src/main/kotlin/com/xtream/player/domain/repositories/StreamRepository.kt
package com.xtream.player.domain.repositories

import com.xtream.player.domain.entities.Stream

interface StreamRepository {
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
    
    suspend fun searchStreams(query: String): List<Stream>
    
    suspend fun getFavorites(): List<Stream>
    
    suspend fun addFavorite(stream: Stream)
    
    suspend fun removeFavorite(streamId: String)
    
    suspend fun getWatchHistory(): List<Stream>
}
