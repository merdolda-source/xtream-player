// Android/app/src/main/kotlin/com/xtream/player/presentation/home/HomeScreen.kt
package xtream.emin.player.presentation.home

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.appcompat.app.AppCompatDelegate
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.History
import androidx.compose.material.icons.filled.Language
import androidx.compose.material.icons.filled.Logout
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FilterChip
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Tab
import androidx.compose.material3.TabRow
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.core.os.LocaleListCompat
import androidx.hilt.navigation.compose.hiltViewModel
import xtream.emin.player.R
import xtream.emin.player.domain.entities.Category
import xtream.emin.player.domain.entities.Stream
import xtream.emin.player.presentation.common.StreamLogoRow
import xtream.emin.player.presentation.common.StreamPosterCard
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HomeScreen(
    onStreamClick: (Stream) -> Unit,
    onNavigateToFavorites: () -> Unit,
    onNavigateToHistory: () -> Unit,
    onLoggedOut: () -> Unit,
    viewModel: HomeViewModel = hiltViewModel()
) {
    val uiState by viewModel.uiState.collectAsState()
    val coroutineScope = rememberCoroutineScope()

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text(stringRes(R.string.app_name)) },
                actions = {
                    LanguageSwitcher()
                    IconButton(onClick = onNavigateToFavorites) {
                        Icon(Icons.Filled.Favorite, contentDescription = stringRes(R.string.favorites_title))
                    }
                    IconButton(onClick = onNavigateToHistory) {
                        Icon(Icons.Filled.History, contentDescription = stringRes(R.string.history_title))
                    }
                    IconButton(onClick = {
                        coroutineScope.launch {
                            viewModel.logout()
                            onLoggedOut()
                        }
                    }) {
                        Icon(Icons.Filled.Logout, contentDescription = stringRes(R.string.action_logout))
                    }
                }
            )
        }
    ) { paddingValues ->
        Column(modifier = Modifier.fillMaxSize().padding(paddingValues)) {
            val tabs = listOf(HomeTab.LIVE, HomeTab.VOD, HomeTab.SERIES)
            val selectedIndex = tabs.indexOf(uiState.selectedTab)

            TabRow(selectedTabIndex = selectedIndex) {
                tabs.forEachIndexed { index, tab ->
                    Tab(
                        selected = selectedIndex == index,
                        onClick = { viewModel.selectTab(tab) },
                        text = { Text(tabLabel(tab)) }
                    )
                }
            }

            if (uiState.categories.isNotEmpty()) {
                CategoryChipRow(
                    categories = uiState.categories,
                    selectedCategoryId = uiState.selectedCategoryId,
                    onCategorySelected = viewModel::selectCategory
                )
            }

            Box(modifier = Modifier.fillMaxSize()) {
                when {
                    uiState.isLoading -> {
                        CircularProgressIndicator(modifier = Modifier.align(Alignment.Center))
                    }
                    uiState.errorMessage != null -> {
                        Text(
                            text = uiState.errorMessage.orEmpty(),
                            modifier = Modifier.align(Alignment.Center).padding(16.dp)
                        )
                    }
                    else -> {
                        val streams = when (uiState.selectedTab) {
                            HomeTab.LIVE -> uiState.liveStreams
                            HomeTab.VOD -> uiState.vodStreams
                            HomeTab.SERIES -> uiState.seriesStreams
                        }
                        if (uiState.selectedTab == HomeTab.LIVE) {
                            StreamListView(
                                streams = streams,
                                favoriteIds = uiState.favoriteIds,
                                onStreamClick = onStreamClick,
                                onToggleFavorite = viewModel::toggleFavorite
                            )
                        } else {
                            StreamGridView(
                                streams = streams,
                                favoriteIds = uiState.favoriteIds,
                                onStreamClick = onStreamClick,
                                onToggleFavorite = viewModel::toggleFavorite
                            )
                        }
                    }
                }
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun CategoryChipRow(
    categories: List<Category>,
    selectedCategoryId: String?,
    onCategorySelected: (String?) -> Unit
) {
    LazyRow(
        modifier = Modifier.fillMaxWidth(),
        contentPadding = PaddingValues(horizontal = 12.dp, vertical = 8.dp),
        horizontalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        item {
            FilterChip(
                selected = selectedCategoryId == null,
                onClick = { onCategorySelected(null) },
                label = { Text(stringRes(R.string.category_all)) }
            )
        }
        items(categories, key = { it.categoryId }) { category ->
            FilterChip(
                selected = selectedCategoryId == category.categoryId,
                onClick = { onCategorySelected(category.categoryId) },
                label = { Text(category.categoryName) }
            )
        }
    }
}

@Composable
private fun StreamListView(
    streams: List<Stream>,
    favoriteIds: Set<String>,
    onStreamClick: (Stream) -> Unit,
    onToggleFavorite: (Stream) -> Unit
) {
    if (streams.isEmpty()) {
        Text(text = stringRes(R.string.empty_list), modifier = Modifier.padding(16.dp))
        return
    }
    LazyColumn(
        modifier = Modifier.fillMaxWidth(),
        contentPadding = PaddingValues(12.dp)
    ) {
        items(streams, key = { it.streamId }) { stream ->
            StreamLogoRow(
                stream = stream,
                onClick = { onStreamClick(stream) },
                modifier = Modifier.padding(bottom = 8.dp),
                isFavorite = favoriteIds.contains(stream.streamId),
                onToggleFavorite = { onToggleFavorite(stream) }
            )
        }
    }
}

@Composable
private fun StreamGridView(
    streams: List<Stream>,
    favoriteIds: Set<String>,
    onStreamClick: (Stream) -> Unit,
    onToggleFavorite: (Stream) -> Unit
) {
    if (streams.isEmpty()) {
        Text(text = stringRes(R.string.empty_list), modifier = Modifier.padding(16.dp))
        return
    }
    LazyVerticalGrid(
        columns = GridCells.Fixed(3),
        modifier = Modifier.fillMaxSize(),
        contentPadding = PaddingValues(12.dp),
        horizontalArrangement = Arrangement.spacedBy(10.dp),
        verticalArrangement = Arrangement.spacedBy(10.dp)
    ) {
        items(streams, key = { it.streamId }) { stream ->
            StreamPosterCard(
                stream = stream,
                onClick = { onStreamClick(stream) },
                isFavorite = favoriteIds.contains(stream.streamId),
                onToggleFavorite = { onToggleFavorite(stream) }
            )
        }
    }
}

@Composable
private fun LanguageSwitcher() {
    var expanded by remember { mutableStateOf(false) }
    Box {
        IconButton(onClick = { expanded = true }) {
            Icon(Icons.Filled.Language, contentDescription = stringRes(R.string.action_language))
        }
        DropdownMenu(expanded = expanded, onDismissRequest = { expanded = false }) {
            DropdownMenuItem(
                text = { Text(stringRes(R.string.language_turkish)) },
                onClick = {
                    expanded = false
                    setAppLocale("tr")
                }
            )
            DropdownMenuItem(
                text = { Text(stringRes(R.string.language_english)) },
                onClick = {
                    expanded = false
                    setAppLocale("en")
                }
            )
        }
    }
}

private fun setAppLocale(languageTag: String) {
    AppCompatDelegate.setApplicationLocales(LocaleListCompat.forLanguageTags(languageTag))
}

@Composable
private fun tabLabel(tab: HomeTab): String = when (tab) {
    HomeTab.LIVE -> stringRes(R.string.tab_live)
    HomeTab.VOD -> stringRes(R.string.tab_vod)
    HomeTab.SERIES -> stringRes(R.string.tab_series)
}

@Composable
private fun stringRes(id: Int): String = androidx.compose.ui.res.stringResource(id)
