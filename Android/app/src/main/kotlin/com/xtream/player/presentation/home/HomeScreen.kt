// Android/app/src/main/kotlin/com/xtream/player/presentation/home/HomeScreen.kt
package com.xtream.player.presentation.home

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Divider
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ListItem
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Tab
import androidx.compose.material3.TabRow
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.xtream.player.R
import com.xtream.player.domain.entities.Stream
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
                    TextButton(onClick = onNavigateToFavorites) {
                        Text(stringRes(R.string.favorites_title))
                    }
                    TextButton(onClick = onNavigateToHistory) {
                        Text(stringRes(R.string.history_title))
                    }
                    TextButton(onClick = {
                        coroutineScope.launch {
                            viewModel.logout()
                            onLoggedOut()
                        }
                    }) {
                        Text(stringRes(R.string.action_logout))
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
                        val streams = when (uiState.selectedTab) {
                            HomeTab.LIVE -> uiState.liveStreams
                            HomeTab.VOD -> uiState.vodStreams
                            HomeTab.SERIES -> uiState.seriesStreams
                        }
                        StreamList(streams = streams, onStreamClick = onStreamClick)
                    }
                }
            }
        }
    }
}

@Composable
private fun StreamList(streams: List<Stream>, onStreamClick: (Stream) -> Unit) {
    if (streams.isEmpty()) {
        Text(
            text = stringRes(R.string.empty_list),
            modifier = Modifier.padding(16.dp)
        )
        return
    }
    LazyColumn(modifier = Modifier.fillMaxWidth()) {
        items(streams, key = { it.streamId }) { stream ->
            ListItem(
                headlineContent = { Text(stream.name) },
                modifier = Modifier
                    .fillMaxWidth()
                    .clickable { onStreamClick(stream) }
            )
            Divider()
        }
    }
}

@Composable
private fun tabLabel(tab: HomeTab): String = when (tab) {
    HomeTab.LIVE -> stringRes(R.string.tab_live)
    HomeTab.VOD -> stringRes(R.string.tab_vod)
    HomeTab.SERIES -> stringRes(R.string.tab_series)
}

@Composable
private fun stringRes(id: Int): String = androidx.compose.ui.res.stringResource(id)
