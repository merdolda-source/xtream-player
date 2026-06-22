// Android/app/src/main/kotlin/com/xtream/player/presentation/home/HomeScreen.kt
package xtream.emin.player.presentation.home

import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.slideInHorizontally
import androidx.compose.animation.slideOutHorizontally
import androidx.compose.animation.with
import androidx.activity.compose.BackHandler
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.GridItemSpan
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.appcompat.app.AppCompatDelegate
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.ChevronRight
import androidx.compose.material.icons.filled.DarkMode
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.History
import androidx.compose.material.icons.filled.Language
import androidx.compose.material.icons.filled.LightMode
import androidx.compose.material.icons.filled.Logout
import androidx.compose.material.icons.filled.BugReport
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
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
import android.app.Activity
import android.content.Intent
import androidx.core.content.FileProvider
import xtream.emin.player.common.utils.Logger
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.core.os.LocaleListCompat
import androidx.hilt.navigation.compose.hiltViewModel
import xtream.emin.player.R
import xtream.emin.player.common.utils.LocaleHelper
import xtream.emin.player.domain.entities.Category
import xtream.emin.player.domain.entities.Stream
import xtream.emin.player.presentation.common.NativeAdCard
import xtream.emin.player.presentation.common.StreamLogoRow
import xtream.emin.player.presentation.common.StreamPosterCard
import xtream.emin.player.presentation.common.ThemePreferences
import kotlinx.coroutines.launch

private const val NATIVE_AD_INSERT_INTERVAL = 8

@OptIn(ExperimentalMaterial3Api::class, androidx.compose.animation.ExperimentalAnimationApi::class)
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
    val context = LocalContext.current

    BackHandler(enabled = uiState.viewMode == HomeViewMode.STREAMS) {
        viewModel.backToCategories()
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Column {
                        Text(stringRes(R.string.app_name))
                        if (uiState.accountIsTrial) {
                            Text(
                                text = stringRes(R.string.login_trial_badge),
                                style = MaterialTheme.typography.labelSmall
                            )
                        } else {
                            uiState.accountExpiry?.let { expiry ->
                                Text(
                                    text = stringRes(R.string.login_expires_label).format(expiry),
                                    style = MaterialTheme.typography.labelSmall
                                )
                            }
                        }
                    }
                },
                actions = {
                    ThemeSwitcher()
                    LanguageSwitcher()
                    IconButton(onClick = { shareLogs(context) }) {
                        Icon(Icons.Filled.BugReport, contentDescription = stringRes(R.string.action_share_logs))
                    }
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
                        AnimatedContent(
                            targetState = uiState.viewMode,
                            transitionSpec = {
                                if (targetState == HomeViewMode.STREAMS) {
                                    (slideInHorizontally(animationSpec = tween(300)) { it } + fadeIn()) with
                                        (slideOutHorizontally(animationSpec = tween(300)) { -it } + fadeOut())
                                } else {
                                    (slideInHorizontally(animationSpec = tween(300)) { -it } + fadeIn()) with
                                        (slideOutHorizontally(animationSpec = tween(300)) { it } + fadeOut())
                                }
                            },
                            label = "home_view_mode"
                        ) { viewMode ->
                            when (viewMode) {
                                HomeViewMode.CATEGORIES -> CategoryListView(
                                    categories = uiState.categories,
                                    onCategorySelected = { id, name -> viewModel.selectCategory(id, name) }
                                )
                                HomeViewMode.STREAMS -> {
                                    val streams = when (uiState.selectedTab) {
                                        HomeTab.LIVE -> uiState.liveStreams
                                        HomeTab.VOD -> uiState.vodStreams
                                        HomeTab.SERIES -> uiState.seriesStreams
                                    }
                                    Column(modifier = Modifier.fillMaxSize()) {
                                        StreamsHeader(
                                            categoryName = uiState.selectedCategoryName
                                                ?: stringRes(R.string.category_all),
                                            onBack = viewModel::backToCategories
                                        )
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
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun CategoryListView(
    categories: List<Category>,
    onCategorySelected: (String?, String?) -> Unit
) {
    LazyColumn(
        modifier = Modifier.fillMaxSize(),
        contentPadding = PaddingValues(12.dp),
        verticalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        item(key = "all") {
            CategoryRow(
                name = stringRes(R.string.category_all),
                onClick = { onCategorySelected(null, null) }
            )
        }
        items(categories, key = { it.categoryId }) { category ->
            CategoryRow(
                name = category.categoryName,
                onClick = { onCategorySelected(category.categoryId, category.categoryName) }
            )
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun CategoryRow(name: String, onClick: () -> Unit) {
    Card(
        onClick = onClick,
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
    ) {
        Box(
            modifier = Modifier.fillMaxWidth().padding(horizontal = 16.dp, vertical = 16.dp)
        ) {
            Text(
                text = name,
                style = MaterialTheme.typography.titleSmall,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis,
                modifier = Modifier.align(Alignment.CenterStart).padding(end = 24.dp)
            )
            Icon(
                Icons.Filled.ChevronRight,
                contentDescription = null,
                tint = MaterialTheme.colorScheme.onSurfaceVariant,
                modifier = Modifier.align(Alignment.CenterEnd)
            )
        }
    }
}

@Composable
private fun StreamsHeader(categoryName: String, onBack: () -> Unit) {
    Row(
        modifier = Modifier.fillMaxWidth().padding(start = 4.dp, end = 12.dp, top = 4.dp, bottom = 4.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        IconButton(onClick = onBack) {
            Icon(Icons.Filled.ArrowBack, contentDescription = null)
        }
        Text(
            text = categoryName,
            style = MaterialTheme.typography.titleMedium,
            maxLines = 1,
            overflow = TextOverflow.Ellipsis,
            modifier = Modifier.padding(start = 4.dp)
        )
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
        itemsIndexed(streams, key = { _, stream -> stream.streamId }) { index, stream ->
            StreamLogoRow(
                stream = stream,
                onClick = { onStreamClick(stream) },
                modifier = Modifier.padding(bottom = 8.dp),
                isFavorite = favoriteIds.contains(stream.streamId),
                onToggleFavorite = { onToggleFavorite(stream) }
            )
            if (index > 0 && (index + 1) % NATIVE_AD_INSERT_INTERVAL == 0) {
                NativeAdCard(modifier = Modifier.padding(bottom = 8.dp))
            }
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
        streams.forEachIndexed { index, stream ->
            item(key = stream.streamId) {
                StreamPosterCard(
                    stream = stream,
                    onClick = { onStreamClick(stream) },
                    isFavorite = favoriteIds.contains(stream.streamId),
                    onToggleFavorite = { onToggleFavorite(stream) }
                )
            }
            if (index > 0 && (index + 1) % NATIVE_AD_INSERT_INTERVAL == 0) {
                item(key = "ad_$index", span = { GridItemSpan(maxLineSpan) }) {
                    NativeAdCard()
                }
            }
        }
    }
}

@Composable
private fun ThemeSwitcher() {
    val context = LocalContext.current
    val darkOverride by ThemePreferences.darkOverride.collectAsState()
    val isDark = darkOverride ?: androidx.compose.foundation.isSystemInDarkTheme()
    IconButton(onClick = { ThemePreferences.toggle(context, isDark) }) {
        Icon(
            imageVector = if (isDark) Icons.Filled.LightMode else Icons.Filled.DarkMode,
            contentDescription = stringRes(R.string.action_theme)
        )
    }
}

@Composable
private fun LanguageSwitcher() {
    var expanded by remember { mutableStateOf(false) }
    val context = LocalContext.current
    Box {
        IconButton(onClick = { expanded = true }) {
            Icon(Icons.Filled.Language, contentDescription = stringRes(R.string.action_language))
        }
        DropdownMenu(expanded = expanded, onDismissRequest = { expanded = false }) {
            DropdownMenuItem(
                text = { Text(stringRes(R.string.language_turkish)) },
                onClick = {
                    expanded = false
                    setAppLocale(context, "tr")
                }
            )
            DropdownMenuItem(
                text = { Text(stringRes(R.string.language_english)) },
                onClick = {
                    expanded = false
                    setAppLocale(context, "en")
                }
            )
        }
    }
}

// LocaleHelper wraps the Activity's base Context with the saved locale on
// every API level (AppCompatDelegate alone only auto-applies on API 33+ or
// inside an AppCompatActivity); recreate() re-runs attachBaseContext so the
// new locale takes effect immediately, on every Android version.
private fun setAppLocale(context: android.content.Context, languageTag: String) {
    LocaleHelper.setLanguageTag(context, languageTag)
    AppCompatDelegate.setApplicationLocales(LocaleListCompat.forLanguageTags(languageTag))
    (context as? Activity)?.recreate()
}

private fun shareLogs(context: android.content.Context) {
    val logFile = Logger.exportLogs() ?: return
    val uri = FileProvider.getUriForFile(context, "xtream.emin.player.fileprovider", logFile)
    val intent = Intent(Intent.ACTION_SEND).apply {
        type = "text/plain"
        putExtra(Intent.EXTRA_STREAM, uri)
        addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)
    }
    context.startActivity(Intent.createChooser(intent, null))
}

@Composable
private fun tabLabel(tab: HomeTab): String = when (tab) {
    HomeTab.LIVE -> stringRes(R.string.tab_live)
    HomeTab.VOD -> stringRes(R.string.tab_vod)
    HomeTab.SERIES -> stringRes(R.string.tab_series)
}

@Composable
private fun stringRes(id: Int): String = androidx.compose.ui.res.stringResource(id)
