// Android/app/src/main/kotlin/com/xtream/player/presentation/navigation/XtreamNavHost.kt
package com.xtream.player.presentation.navigation

import androidx.compose.runtime.Composable
import androidx.navigation.NavHostController
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.navArgument
import com.xtream.player.domain.entities.Stream
import com.xtream.player.domain.entities.StreamType
import com.xtream.player.presentation.favorites.FavoritesScreen
import com.xtream.player.presentation.history.HistoryScreen
import com.xtream.player.presentation.home.HomeScreen
import com.xtream.player.presentation.login.LoginScreen
import com.xtream.player.presentation.player.PlayerScreen
import com.xtream.player.presentation.series.SeriesDetailScreen

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
                onStreamClick = { stream -> navController.navigateToStream(stream) },
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
                    navController.navigate(
                        NavRoutes.playerRoute(
                            streamId = episode.episodeId,
                            streamType = StreamType.SERIES.name,
                            name = "$name - S${episode.season}E${episode.episodeNum} - ${episode.title}",
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
