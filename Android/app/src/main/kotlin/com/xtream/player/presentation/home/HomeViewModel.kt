// Android/app/src/main/kotlin/com/xtream/player/presentation/home/HomeViewModel.kt
package com.xtream.player.presentation.home

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.xtream.player.common.utils.Logger
import com.xtream.player.domain.entities.Stream
import com.xtream.player.domain.repositories.AuthRepository
import com.xtream.player.domain.usecases.GetLiveStreamsUseCase
import com.xtream.player.domain.usecases.GetSeriesUseCase
import com.xtream.player.domain.usecases.GetVODStreamsUseCase
import com.xtream.player.domain.usecases.SearchStreamsUseCase
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
    val isLoading: Boolean = false,
    val errorMessage: String? = null,
    val searchQuery: String = "",
    val searchResults: List<Stream>? = null
)

@HiltViewModel
class HomeViewModel @Inject constructor(
    private val authRepository: AuthRepository,
    private val getLiveStreamsUseCase: GetLiveStreamsUseCase,
    private val getVODStreamsUseCase: GetVODStreamsUseCase,
    private val getSeriesUseCase: GetSeriesUseCase,
    private val searchStreamsUseCase: SearchStreamsUseCase
) : ViewModel() {

    private val _uiState = MutableStateFlow(HomeUiState())
    val uiState: StateFlow<HomeUiState> = _uiState.asStateFlow()

    init {
        loadTab(HomeTab.LIVE)
    }

    fun selectTab(tab: HomeTab) {
        _uiState.update { it.copy(selectedTab = tab) }
        loadTab(tab)
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

    private fun loadTab(tab: HomeTab) {
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
                        val streams = getLiveStreamsUseCase(user.host, user.username, user.password)
                        _uiState.update { it.copy(liveStreams = streams, isLoading = false) }
                    }
                    HomeTab.VOD -> {
                        val streams = getVODStreamsUseCase(user.host, user.username, user.password)
                        _uiState.update { it.copy(vodStreams = streams, isLoading = false) }
                    }
                    HomeTab.SERIES -> {
                        val streams = getSeriesUseCase(user.host, user.username, user.password)
                        _uiState.update { it.copy(seriesStreams = streams, isLoading = false) }
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
