// Android/app/src/main/kotlin/com/xtream/player/data/remote/api/XtreamApiService.kt
package com.erdin.player.data.remote.api

import com.erdin.player.data.remote.dto.CategoryDto
import com.erdin.player.data.remote.dto.LoginResponseDto
import com.erdin.player.data.remote.dto.SeriesDto
import com.erdin.player.data.remote.dto.SeriesInfoResponseDto
import com.erdin.player.data.remote.dto.StreamDto
import retrofit2.http.GET
import retrofit2.http.Query
import retrofit2.http.Url

/**
 * Xtream Codes `player_api.php` endpoints.
 *
 * Every call needs the full server URL (host, e.g. "http://example.com:8080")
 * because Xtream servers are user-supplied at login time - there's no single
 * fixed base URL for this API, so we pass the full `player_api.php` URL via
 * [Url] on every request and let Retrofit treat the configured base URL as a
 * fallback that is never actually used in practice.
 */
interface XtreamApiService {

    /** Implicit login: calling player_api.php with just credentials returns account info. */
    @GET
    suspend fun login(
        @Url url: String,
        @Query("username") username: String,
        @Query("password") password: String
    ): LoginResponseDto

    @GET
    suspend fun getLiveCategories(
        @Url url: String,
        @Query("username") username: String,
        @Query("password") password: String,
        @Query("action") action: String = "get_live_categories"
    ): List<CategoryDto>

    @GET
    suspend fun getVodCategories(
        @Url url: String,
        @Query("username") username: String,
        @Query("password") password: String,
        @Query("action") action: String = "get_vod_categories"
    ): List<CategoryDto>

    @GET
    suspend fun getSeriesCategories(
        @Url url: String,
        @Query("username") username: String,
        @Query("password") password: String,
        @Query("action") action: String = "get_series_categories"
    ): List<CategoryDto>

    @GET
    suspend fun getLiveStreams(
        @Url url: String,
        @Query("username") username: String,
        @Query("password") password: String,
        @Query("action") action: String = "get_live_streams",
        @Query("category_id") categoryId: String? = null
    ): List<StreamDto>

    @GET
    suspend fun getVodStreams(
        @Url url: String,
        @Query("username") username: String,
        @Query("password") password: String,
        @Query("action") action: String = "get_vod_streams",
        @Query("category_id") categoryId: String? = null
    ): List<StreamDto>

    @GET
    suspend fun getSeries(
        @Url url: String,
        @Query("username") username: String,
        @Query("password") password: String,
        @Query("action") action: String = "get_series",
        @Query("category_id") categoryId: String? = null
    ): List<SeriesDto>

    /** Resolves the season/episode breakdown for a single series entry. */
    @GET
    suspend fun getSeriesInfo(
        @Url url: String,
        @Query("username") username: String,
        @Query("password") password: String,
        @Query("action") action: String = "get_series_info",
        @Query("series_id") seriesId: String
    ): SeriesInfoResponseDto
}
