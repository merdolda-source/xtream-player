// Android/app/src/main/kotlin/com/xtream/player/presentation/home/HomeViewModel.kt
package com.erdin.player.presentation.home

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.erdin.player.common.utils.ExpiryFormatter
import com.erdin.player.common.utils.Logger
import com.erdin.player.domain.entities.Category
import com.erdin.player.domain.entities.Stream
import com.erdin.player.domain.repositories.AuthRepository
import com.erdin.player.domain.usecases.GetFavoritesUseCase
import com.erdin.player.domain.usecases.GetLiveCategoriesUseCase
import com.erdin.player.domain.usecases.GetLiveStreamsUseCase
import com.erdin.player.domain.usecases.GetSeriesCategoriesUseCase
import com.erdin.player.domain.usecases.GetSeriesUseCase
import com.erdin.player.domain.usecases.GetVODStreamsUseCase
import com.erdin.player.domain.usecases.GetVodCategoriesUseCase
import com.erdin.player.domain.usecases.SearchStreamsUseCase
import com.erdin.player.domain.usecases.ToggleFavoriteUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

enum class HomeTab { LIVE, VOD, SERIES }

/** First browse by category (list), then drill into that category's streams (cards). */
enum class HomeViewMode { CATEGORIES, STREAMS }

data class HomeUiState(
    val selectedTab: HomeTab = HomeTab.LIVE,
    val viewMode: HomeViewMode = HomeViewMode.CATEGORIES,
    val liveStreams: List<Stream> = emptyList(),
    val vodStreams: List<Stream> = emptyList(),
    val seriesStreams: List<Stream> = emptyList(),
    val categories: List<Category> = emptyList(),
    val selectedCategoryId: String? = null,
    val selectedCategoryName: String? = null,
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
        loadCategories(HomeTab.LIVE)
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
        _uiState.update {
            it.copy(
                selectedTab = tab,
                viewMode = HomeViewMode.CATEGORIES,
                selectedCategoryId = null,
                selectedCategoryName = null
            )
        }
        loadCategories(tab)
    }

    /** Drill into a category (or "All" when [categoryId] is null) and load its streams. */
    fun selectCategory(categoryId: String?, categoryName: String?) {
        _uiState.update {
            it.copy(
                viewMode = HomeViewMode.STREAMS,
                selectedCategoryId = categoryId,
                selectedCategoryName = categoryName
            )
        }
        loadStreams(_uiState.value.selectedTab, categoryId)
    }

    /** Back out of the stream grid to the category list for the current tab. */
    fun backToCategories() {
        _uiState.update { it.copy(viewMode = HomeViewMode.CATEGORIES) }
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

    private fun loadCategories(tab: HomeTab) {
        viewModelScope.launch {
            val user = authRepository.getCurrentSession()
            if (user == null) {
                _uiState.update { it.copy(errorMessage = "Not logged in.") }
                return@launch
            }
            _uiState.update { it.copy(isLoading = true, errorMessage = null) }
            try {
                val categories = when (tab) {
                    HomeTab.LIVE -> getLiveCategoriesUseCase(user.host, user.username, user.password)
                    HomeTab.VOD -> getVodCategoriesUseCase(user.host, user.username, user.password)
                    HomeTab.SERIES -> getSeriesCategoriesUseCase(user.host, user.username, user.password)
                }
                _uiState.update { it.copy(categories = categories, isLoading = false) }
            } catch (e: Exception) {
                Logger.error("Failed to load $tab categories", e)
                _uiState.update {
                    it.copy(isLoading = false, errorMessage = e.message ?: "Failed to load content.")
                }
            }
        }
    }

    private fun loadStreams(tab: HomeTab, categoryId: String?) {
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
                        val streams = getLiveStreamsUseCase(user.host, user.username, user.password, categoryId)
                        _uiState.update { it.copy(liveStreams = streams, isLoading = false) }
                    }
                    HomeTab.VOD -> {
                        val streams = getVODStreamsUseCase(user.host, user.username, user.password, categoryId)
                        _uiState.update { it.copy(vodStreams = streams, isLoading = false) }
                    }
                    HomeTab.SERIES -> {
                        val streams = getSeriesUseCase(user.host, user.username, user.password, categoryId)
                        _uiState.update { it.copy(seriesStreams = streams, isLoading = false) }
                    }
                }
            } catch (e: Exception) {
                Logger.error("Failed to load $tab streams", e)
                _uiState.update {
                    it.copy(isLoading = false, errorMessage = e.message ?: "Failed to load content.")
                }
            }
        }
    }
}
