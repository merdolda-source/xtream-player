// Android/app/src/main/kotlin/com/xtream/player/presentation/series/SeriesDetailViewModel.kt
package com.xtream.player.presentation.series

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.xtream.player.common.utils.Logger
import com.xtream.player.domain.entities.Episode
import com.xtream.player.domain.entities.Season
import com.xtream.player.domain.repositories.AuthRepository
import com.xtream.player.domain.usecases.GetSeriesInfoUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

data class SeriesDetailUiState(
    val isLoading: Boolean = true,
    val errorMessage: String? = null,
    val name: String = "",
    val cover: String? = null,
    val plot: String? = null,
    val seasons: List<Season> = emptyList(),
    val selectedSeason: Int? = null,
    val episodes: List<Episode> = emptyList()
)

@HiltViewModel
class SeriesDetailViewModel @Inject constructor(
    private val authRepository: AuthRepository,
    private val getSeriesInfoUseCase: GetSeriesInfoUseCase
) : ViewModel() {

    private val _uiState = MutableStateFlow(SeriesDetailUiState())
    val uiState: StateFlow<SeriesDetailUiState> = _uiState.asStateFlow()

    private var episodesBySeason: Map<Int, List<Episode>> = emptyMap()
    private var loadedSeriesId: String? = null

    fun load(seriesId: String, fallbackName: String, fallbackCover: String?) {
        if (loadedSeriesId == seriesId) return
        loadedSeriesId = seriesId
        viewModelScope.launch {
            val user = authRepository.getCurrentSession()
            if (user == null) {
                _uiState.update { it.copy(isLoading = false, errorMessage = "Not logged in.") }
                return@launch
            }
            _uiState.update {
                it.copy(isLoading = true, errorMessage = null, name = fallbackName, cover = fallbackCover)
            }
            try {
                val details = getSeriesInfoUseCase(
                    user.host, user.username, user.password, seriesId, fallbackName, fallbackCover
                )
                episodesBySeason = details.episodesBySeason
                val firstSeason = details.seasons.firstOrNull()?.seasonNumber
                _uiState.update {
                    it.copy(
                        isLoading = false,
                        name = details.name,
                        cover = details.cover,
                        plot = details.plot,
                        seasons = details.seasons,
                        selectedSeason = firstSeason,
                        episodes = firstSeason?.let { season -> episodesBySeason[season] }.orEmpty()
                    )
                }
            } catch (e: Exception) {
                Logger.error("Failed to load series info", e)
                _uiState.update {
                    it.copy(isLoading = false, errorMessage = e.message ?: "Failed to load series.")
                }
            }
        }
    }

    fun selectSeason(seasonNumber: Int) {
        _uiState.update {
            it.copy(selectedSeason = seasonNumber, episodes = episodesBySeason[seasonNumber].orEmpty())
        }
    }
}
