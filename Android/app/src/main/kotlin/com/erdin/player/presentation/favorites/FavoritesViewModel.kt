// Android/app/src/main/kotlin/com/xtream/player/presentation/favorites/FavoritesViewModel.kt
package com.erdin.player.presentation.favorites

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.erdin.player.domain.entities.Stream
import com.erdin.player.domain.usecases.GetFavoritesUseCase
import com.erdin.player.domain.usecases.ToggleFavoriteUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

data class FavoritesUiState(
    val favorites: List<Stream> = emptyList(),
    val isLoading: Boolean = true
)

@HiltViewModel
class FavoritesViewModel @Inject constructor(
    private val getFavoritesUseCase: GetFavoritesUseCase,
    private val toggleFavoriteUseCase: ToggleFavoriteUseCase
) : ViewModel() {

    private val _uiState = MutableStateFlow(FavoritesUiState())
    val uiState: StateFlow<FavoritesUiState> = _uiState.asStateFlow()

    init {
        loadFavorites()
    }

    fun loadFavorites() {
        viewModelScope.launch {
            _uiState.value = FavoritesUiState(favorites = getFavoritesUseCase(), isLoading = false)
        }
    }

    fun removeFavorite(stream: Stream) {
        viewModelScope.launch {
            toggleFavoriteUseCase(stream, isCurrentlyFavorite = true)
            loadFavorites()
        }
    }
}
