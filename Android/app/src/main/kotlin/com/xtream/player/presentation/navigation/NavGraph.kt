// Android/app/src/main/kotlin/com/xtream/player/presentation/navigation/NavGraph.kt
package com.xtream.player.presentation.navigation

/** Route definitions for the app's single Compose NavHost. */
object NavRoutes {
    const val LOGIN = "login"
    const val HOME = "home"
    const val FAVORITES = "favorites"
    const val HISTORY = "history"

    const val PLAYER = "player"
    const val PLAYER_ARG_STREAM_ID = "streamId"
    const val PLAYER_ARG_STREAM_TYPE = "streamType"
    const val PLAYER_ARG_NAME = "name"
    const val PLAYER_ARG_CONTAINER_EXT = "containerExt"
    const val PLAYER_ROUTE =
        "$PLAYER/{$PLAYER_ARG_STREAM_ID}/{$PLAYER_ARG_STREAM_TYPE}/{$PLAYER_ARG_NAME}/{$PLAYER_ARG_CONTAINER_EXT}"

    fun playerRoute(streamId: String, streamType: String, name: String, containerExt: String?): String {
        val encodedName = java.net.URLEncoder.encode(name, "UTF-8")
        val ext = containerExt ?: "none"
        return "$PLAYER/$streamId/$streamType/$encodedName/$ext"
    }

    const val SERIES_DETAIL = "seriesDetail"
    const val SERIES_DETAIL_ARG_ID = "seriesId"
    const val SERIES_DETAIL_ARG_NAME = "seriesName"
    const val SERIES_DETAIL_ARG_COVER = "seriesCover"
    const val SERIES_DETAIL_ROUTE =
        "$SERIES_DETAIL/{$SERIES_DETAIL_ARG_ID}/{$SERIES_DETAIL_ARG_NAME}/{$SERIES_DETAIL_ARG_COVER}"

    fun seriesDetailRoute(seriesId: String, name: String, cover: String?): String {
        val encodedName = java.net.URLEncoder.encode(name, "UTF-8")
        val encodedCover = java.net.URLEncoder.encode(cover ?: "none", "UTF-8")
        return "$SERIES_DETAIL/$seriesId/$encodedName/$encodedCover"
    }
}
