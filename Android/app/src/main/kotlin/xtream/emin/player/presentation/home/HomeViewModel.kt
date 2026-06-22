// Android/app/src/main/kotlin/com/xtream/player/presentation/home/HomeViewModel.kt
package xtream.emin.player.presentation.home

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import xtream.emin.player.common.utils.ExpiryFormatter
import xtream.emin.player.common.utils.Logger
import xtream.emin.player.domain.entities.Category
import xtream.emin.player.domain.entities.Stream
import xtream.emin.player.domain.repositories.AuthRepository
import xtream.emin.player.domain.usecases.GetFavoritesUseCase
import xtream.emin.player.domain.usecases.GetLiveCategoriesUseCase
import xtream.emin.player.domain.usecases.GetLiveStreamsUseCase
import xtream.emin.player.domain.usecases.GetSeriesCategoriesUseCase
import xtream.emin.player.domain.usecases.GetSeriesUseCase
import xtream.emin.player.domain.usecases.GetVODStreamsUseCase
import xtream.emin.player.domain.usecases.GetVodCategoriesUseCase
import xtream.emin.player.domain.usecases.SearchStreamsUseCase
import xtream.emin.player.domain.usecases.ToggleFavoriteUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

enum class HomeTab { LIVE, VOD, SERIES }

data class HomeUiState(
    val selectedTab: HomeTab = HomeTab.LIVE,
    val liveStreams: List<Stream> = emptyList(),
    val vodStreams: List<Stream> = emptyList(),
    val seriesStreams: List<Stream> = emptyList(),
    val categories: List<Category> = emptyList(),
    val selectedCategoryId: String? = null,
    val favoriteIds: Set<String> = emptySet(),
    val isLoading: Boolean = false,
    val errorMessage: String? = null,
    val searchQuery: String = "",
    val searchResults: List<Stream>? = null,
    val accountExpiry: String? = null,
    val accountIsTrial: Boolean = false
)

@HiltViewModel
class HomeViewModel @Inject constructor(
    private val authRepository: AuthRepository,
    private val getLiveStreamsUseCase: GetLiveStreamsUseCase,
    private val getVODStreamsUseCase: GetVODStreamsUseCase,
    private val getSeriesUseCase: GetSeriesUseCase,
    private val getLiveCategoriesUseCase: GetLiveCategoriesUseCase,
    private val getVodCategoriesUseCase: GetVodCategoriesUseCase,
    private val getSeriesCategoriesUseCase: GetSeriesCategoriesUseCase,
    private val searchStreamsUseCase: SearchStreamsUseCase,
    private val getFavoritesUseCase: GetFavoritesUseCase,
    private val toggleFavoriteUseCase: ToggleFavoriteUseCase
) : ViewModel() {

    private val _uiState = MutableStateFlow(HomeUiState())
    val uiState: StateFlow<HomeUiState> = _uiState.asStateFlow()

    init {
        loadFavoriteIds()
        loadAccountInfo()
        loadTab(HomeTab.LIVE)
    }

    private fun loadAccountInfo() {
        viewModelScope.launch {
            val user = authRepository.getCurrentSession() ?: return@launch
            _uiState.update {
                it.copy(accountExpiry = ExpiryFormatter.format(user.expDate), accountIsTrial = user.isTrial)
            }
        }
    }

    fun selectTab(tab: HomeTab) {
        _uiState.update { it.copy(selectedTab = tab, selectedCategoryId = null) }
        loadTab(tab)
    }

    fun selectCategory(categoryId: String?) {
        _uiState.update { it.copy(selectedCategoryId = categoryId) }
        loadTab(_uiState.value.selectedTab, categoryId = categoryId)
    }

    fun toggleFavorite(stream: Stream) {
        viewModelScope.launch {
            val isCurrentlyFavorite = _uiState.value.favoriteIds.contains(stream.streamId)
            toggleFavoriteUseCase(stream, isCurrentlyFavorite)
            loadFavoriteIds()
        }
    }

    fun onSearchQueryChanged(query: String) {
        _uiState.update { it.copy(searchQuery = query) }
        if (query.isBlank()) {
            _uiState.update { it.copy(searchResults = null) }
            return
        }
        viewModelScope.launch {
            val results = searchStreamsUseCase(query)
            _uiState.update { it.copy(searchResults = results) }
        }
    }

    fun clearSearch() {
        _uiState.update { it.copy(searchQuery = "", searchResults = null) }
    }

    suspend fun logout() {
        authRepository.logout()
    }

    private fun loadFavoriteIds() {
        viewModelScope.launch {
            val favoriteIds = runCatching { getFavoritesUseCase() }
                .getOrDefault(emptyList())
                .map { it.streamId }
                .toSet()
            _uiState.update { it.copy(favoriteIds = favoriteIds) }
        }
    }

    private fun loadTab(tab: HomeTab, categoryId: String? = null) {
        viewModelScope.launch {
            val user = authRepository.getCurrentSession()
            if (user == null) {
                _uiState.update { it.copy(errorMessage = "Not logged in.") }
                return@launch
            }
            _uiState.update { it.copy(isLoading = true, errorMessage = null) }
            try {
                when (tab) {
                    HomeTab.LIVE -> {
                        val categories = getLiveCategoriesUseCase(user.host, user.username, user.password)
                        val streams = getLiveStreamsUseCase(user.host, user.username, user.password, categoryId)
                        _uiState.update {
                            it.copy(liveStreams = streams, categories = categories, isLoading = false)
                        }
                    }
                    HomeTab.VOD -> {
                        val categories = getVodCategoriesUseCase(user.host, user.username, user.password)
                        val streams = getVODStreamsUseCase(user.host, user.username, user.password, categoryId)
                        _uiState.update {
                            it.copy(vodStreams = streams, categories = categories, isLoading = false)
                        }
                    }
                    HomeTab.SERIES -> {
                        val categories = getSeriesCategoriesUseCase(user.host, user.username, user.password)
                        val streams = getSeriesUseCase(user.host, user.username, user.password, categoryId)
                        _uiState.update {
                            it.copy(seriesStreams = streams, categories = categories, isLoading = false)
                        }
                    }
                }
            } catch (e: Exception) {
                Logger.error("Failed to load $tab", e)
                _uiState.update {
                    it.copy(isLoading = false, errorMessage = e.message ?: "Failed to load content.")
                }
            }
        }
    }
}
