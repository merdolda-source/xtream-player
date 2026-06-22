// Android/app/src/main/kotlin/com/xtream/player/data/repositories/StreamRepositoryImpl.kt
package com.erdin.player.data.repositories

import com.erdin.player.data.local.dao.FavoriteDao
import com.erdin.player.data.local.dao.WatchHistoryDao
import com.erdin.player.data.local.entities.toDomain
import com.erdin.player.data.local.entities.toFavoriteEntity
import com.erdin.player.data.local.entities.toWatchHistoryEntity
import com.erdin.player.data.remote.api.XtreamApiService
import com.erdin.player.data.remote.api.XtreamUrlBuilder
import com.erdin.player.data.remote.dto.toDomain
import com.erdin.player.data.session.SessionManager
import com.erdin.player.domain.entities.Category
import com.erdin.player.domain.entities.SeriesDetails
import com.erdin.player.domain.entities.Stream
import com.erdin.player.domain.entities.StreamType
import com.erdin.player.domain.repositories.StreamRepository
import com.erdin.player.domain.usecases.HistoryRecorder
import javax.inject.Inject

/**
 * Implements the existing [StreamRepository] contract: live/VOD/series
 * catalog reads go through Retrofit against the user's Xtream server,
 * favorites/history are persisted locally via Room. Also implements
 * [HistoryRecorder] so [com.erdin.player.domain.usecases.RecordWatchHistoryUseCase]
 * can append entries without StreamRepository's interface needing a new method.
 */
class StreamRepositoryImpl @Inject constructor(
    private val apiService: XtreamApiService,
    private val sessionManager: SessionManager,
    private val favoriteDao: FavoriteDao,
    private val watchHistoryDao: WatchHistoryDao
) : StreamRepository, HistoryRecorder {

    override suspend fun getLiveCategories(
        host: String,
        username: String,
        password: String
    ): List<Category> {
        val url = XtreamUrlBuilder.playerApiUrl(host)
        return apiService.getLiveCategories(url, username, password).map { it.toDomain() }
    }

    override suspend fun getVodCategories(
        host: String,
        username: String,
        password: String
    ): List<Category> {
        val url = XtreamUrlBuilder.playerApiUrl(host)
        return apiService.getVodCategories(url, username, password).map { it.toDomain() }
    }

    override suspend fun getSeriesCategories(
        host: String,
        username: String,
        password: String
    ): List<Category> {
        val url = XtreamUrlBuilder.playerApiUrl(host)
        return apiService.getSeriesCategories(url, username, password).map { it.toDomain() }
    }

    override suspend fun getLiveStreams(
        host: String,
        username: String,
        password: String,
        categoryId: String?
    ): List<Stream> {
        val url = XtreamUrlBuilder.playerApiUrl(host)
        return apiService.getLiveStreams(url, username, password, categoryId = categoryId)
            .map { it.toDomain(StreamType.LIVE) }
    }

    override suspend fun getVODStreams(
        host: String,
        username: String,
        password: String,
        categoryId: String?
    ): List<Stream> {
        val url = XtreamUrlBuilder.playerApiUrl(host)
        return apiService.getVodStreams(url, username, password, categoryId = categoryId)
            .map { it.toDomain(StreamType.VOD) }
    }

    override suspend fun getSeries(
        host: String,
        username: String,
        password: String,
        categoryId: String?
    ): List<Stream> {
        val url = XtreamUrlBuilder.playerApiUrl(host)
        return apiService.getSeries(url, username, password, categoryId = categoryId)
            .map { it.toDomain() }
    }

    override suspend fun getSeriesInfo(
        host: String,
        username: String,
        password: String,
        seriesId: String,
        fallbackName: String,
        fallbackCover: String?
    ): SeriesDetails {
        val url = XtreamUrlBuilder.playerApiUrl(host)
        return apiService.getSeriesInfo(url, username, password, seriesId = seriesId)
            .toDomain(seriesId, fallbackName, fallbackCover)
    }

    override suspend fun searchStreams(query: String): List<Stream> {
        val session = sessionManager.getSession() ?: return emptyList()
        val normalizedQuery = query.trim().lowercase()
        if (normalizedQuery.isBlank()) return emptyList()

        val url = XtreamUrlBuilder.playerApiUrl(session.host)
        val live = runCatching {
            apiService.getLiveStreams(url, session.username, session.password)
                .map { it.toDomain(StreamType.LIVE) }
        }.getOrDefault(emptyList())
        val vod = runCatching {
            apiService.getVodStreams(url, session.username, session.password)
                .map { it.toDomain(StreamType.VOD) }
        }.getOrDefault(emptyList())
        val series = runCatching {
            apiService.getSeries(url, session.username, session.password)
                .map { it.toDomain() }
        }.getOrDefault(emptyList())

        return (live + vod + series).filter { it.name.lowercase().contains(normalizedQuery) }
    }

    override suspend fun getFavorites(): List<Stream> =
        favoriteDao.getFavorites().map { it.toDomain() }

    override suspend fun addFavorite(stream: Stream) {
        favoriteDao.insert(stream.toFavoriteEntity())
    }

    override suspend fun removeFavorite(streamId: String) {
        favoriteDao.deleteByStreamId(streamId)
    }

    override suspend fun isFavorite(streamId: String): Boolean =
        favoriteDao.isFavorite(streamId)

    override suspend fun getWatchHistory(): List<Stream> =
        watchHistoryDao.getHistory().map { it.toDomain() }

    override suspend fun recordWatched(stream: Stream) {
        watchHistoryDao.insert(stream.toWatchHistoryEntity())
    }
}
