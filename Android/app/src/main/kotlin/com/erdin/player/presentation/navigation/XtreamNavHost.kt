// Android/app/src/main/kotlin/com/xtream/player/presentation/navigation/XtreamNavHost.kt
package com.erdin.player.presentation.navigation

import androidx.compose.runtime.Composable
import androidx.compose.ui.platform.LocalContext
import androidx.navigation.NavHostController
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.navArgument
import com.erdin.player.common.ads.InterstitialAdManager
import com.erdin.player.domain.entities.Stream
import com.erdin.player.domain.entities.StreamType
import com.erdin.player.presentation.favorites.FavoritesScreen
import com.erdin.player.presentation.history.HistoryScreen
import com.erdin.player.presentation.home.HomeScreen
import com.erdin.player.presentation.login.LoginScreen
import com.erdin.player.presentation.player.PlayerScreen
import com.erdin.player.presentation.series.SeriesDetailScreen

private fun NavHostController.navigateToStream(stream: Stream) {
    if (stream.streamType == StreamType.SERIES) {
        navigate(NavRoutes.seriesDetailRoute(stream.streamId, stream.name, stream.streamIcon))
    } else {
        navigate(
            NavRoutes.playerRoute(
                streamId = stream.streamId,
                streamType = stream.streamType.name,
                name = stream.name,
                containerExt = stream.containerExtension
            )
        )
    }
}

@Composable
fun XtreamNavHost(navController: NavHostController, startDestination: String) {
    val context = LocalContext.current

    NavHost(navController = navController, startDestination = startDestination) {
        composable(NavRoutes.LOGIN) {
            LoginScreen(
                onLoginSuccess = {
                    navController.navigate(NavRoutes.HOME) {
                        popUpTo(NavRoutes.LOGIN) { inclusive = true }
                    }
                }
            )
        }

        composable(NavRoutes.HOME) {
            HomeScreen(
                onStreamClick = { stream ->
                    if (stream.streamType == StreamType.LIVE) {
                        InterstitialAdManager.onChannelSwitched(context)
                    }
                    navController.navigateToStream(stream)
                },
                onNavigateToFavorites = { navController.navigate(NavRoutes.FAVORITES) },
                onNavigateToHistory = { navController.navigate(NavRoutes.HISTORY) },
                onLoggedOut = {
                    navController.navigate(NavRoutes.LOGIN) {
                        popUpTo(0) { inclusive = true }
                    }
                }
            )
        }

        composable(NavRoutes.FAVORITES) {
            FavoritesScreen(
                onStreamClick = { stream -> navController.navigateToStream(stream) },
                onBack = { navController.popBackStack() }
            )
        }

        composable(NavRoutes.HISTORY) {
            HistoryScreen(
                onStreamClick = { stream -> navController.navigateToStream(stream) },
                onBack = { navController.popBackStack() }
            )
        }

        composable(
            route = NavRoutes.SERIES_DETAIL_ROUTE,
            arguments = listOf(
                navArgument(NavRoutes.SERIES_DETAIL_ARG_ID) { type = NavType.StringType },
                navArgument(NavRoutes.SERIES_DETAIL_ARG_NAME) { type = NavType.StringType },
                navArgument(NavRoutes.SERIES_DETAIL_ARG_COVER) { type = NavType.StringType }
            )
        ) { backStackEntry ->
            val args = backStackEntry.arguments
            val seriesId = args?.getString(NavRoutes.SERIES_DETAIL_ARG_ID).orEmpty()
            val encodedName = args?.getString(NavRoutes.SERIES_DETAIL_ARG_NAME).orEmpty()
            val encodedCover = args?.getString(NavRoutes.SERIES_DETAIL_ARG_COVER).orEmpty()

            val name = java.net.URLDecoder.decode(encodedName, "UTF-8")
            val cover = java.net.URLDecoder.decode(encodedCover, "UTF-8").takeUnless { it == "none" }

            SeriesDetailScreen(
                seriesId = seriesId,
                seriesName = name,
                seriesCover = cover,
                onEpisodeClick = { episode ->
                    // Some Xtream panels echo the series name back as a
                    // placeholder episode title, which would otherwise show
                    // up twice (e.g. "After Life - S1E1 - After Life").
                    val episodeLabel = "S${episode.season}E${episode.episodeNum}" +
                        if (episode.title.isNotBlank() && episode.title != name) " - ${episode.title}" else ""
                    navController.navigate(
                        NavRoutes.playerRoute(
                            streamId = episode.episodeId,
                            streamType = StreamType.SERIES.name,
                            name = "$name - $episodeLabel",
                            containerExt = episode.containerExtension
                        )
                    )
                },
                onBack = { navController.popBackStack() }
            )
        }

        composable(
            route = NavRoutes.PLAYER_ROUTE,
            arguments = listOf(
                navArgument(NavRoutes.PLAYER_ARG_STREAM_ID) { type = NavType.StringType },
                navArgument(NavRoutes.PLAYER_ARG_STREAM_TYPE) { type = NavType.StringType },
                navArgument(NavRoutes.PLAYER_ARG_NAME) { type = NavType.StringType },
                navArgument(NavRoutes.PLAYER_ARG_CONTAINER_EXT) { type = NavType.StringType }
            )
        ) { backStackEntry ->
            val args = backStackEntry.arguments
            val streamId = args?.getString(NavRoutes.PLAYER_ARG_STREAM_ID).orEmpty()
            val streamTypeRaw = args?.getString(NavRoutes.PLAYER_ARG_STREAM_TYPE).orEmpty()
            val encodedName = args?.getString(NavRoutes.PLAYER_ARG_NAME).orEmpty()
            val containerExtRaw = args?.getString(NavRoutes.PLAYER_ARG_CONTAINER_EXT)

            val name = java.net.URLDecoder.decode(encodedName, "UTF-8")
            val streamType = runCatching { StreamType.valueOf(streamTypeRaw) }.getOrDefault(StreamType.LIVE)
            val containerExt = containerExtRaw?.takeUnless { it == "none" }

            PlayerScreen(
                streamId = streamId,
                streamType = streamType,
                name = name,
                containerExtension = containerExt,
                onBack = { navController.popBackStack() }
            )
        }
    }
}
