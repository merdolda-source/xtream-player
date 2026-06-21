// Android/app/src/main/kotlin/com/xtream/player/presentation/favorites/FavoritesScreen.kt
package com.xtream.player.presentation.favorites

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Divider
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.IconButton
import androidx.compose.material3.ListItem
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.xtream.player.R
import com.xtream.player.domain.entities.Stream

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun FavoritesScreen(
    onStreamClick: (Stream) -> Unit,
    onBack: () -> Unit,
    viewModel: FavoritesViewModel = hiltViewModel()
) {
    val uiState by viewModel.uiState.collectAsState()

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text(stringRes(R.string.favorites_title)) },
                navigationIcon = {
                    IconButton(onClick = onBack) { Text("<") }
                }
            )
        }
    ) { paddingValues ->
        Box(modifier = Modifier.fillMaxSize().padding(paddingValues)) {
            when {
                uiState.isLoading -> {
                    CircularProgressIndicator(modifier = Modifier.align(Alignment.Center))
                }
                uiState.favorites.isEmpty() -> {
                    Text(
                        text = stringRes(R.string.favorites_empty),
                        modifier = Modifier.align(Alignment.Center).padding(16.dp)
                    )
                }
                else -> {
                    LazyColumn(modifier = Modifier.fillMaxWidth()) {
                        items(uiState.favorites, key = { it.streamId }) { stream ->
                            ListItem(
                                headlineContent = { Text(stream.name) },
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .clickable { onStreamClick(stream) },
                                trailingContent = {
                                    TextButton(onClick = { viewModel.removeFavorite(stream) }) {
                                        Text(stringRes(R.string.action_favorite_remove))
                                    }
                                }
                            )
                            Divider()
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun stringRes(id: Int): String = androidx.compose.ui.res.stringResource(id)
