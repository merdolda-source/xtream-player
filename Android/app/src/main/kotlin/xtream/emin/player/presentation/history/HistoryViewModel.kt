// Android/app/src/main/kotlin/com/xtream/player/presentation/history/HistoryViewModel.kt
package xtream.emin.player.presentation.history

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import xtream.emin.player.domain.entities.Stream
import xtream.emin.player.domain.usecases.GetWatchHistoryUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

data class HistoryUiState(
    val history: List<Stream> = emptyList(),
    val isLoading: Boolean = true
)

@HiltViewModel
class HistoryViewModel @Inject constructor(
    private val getWatchHistoryUseCase: GetWatchHistoryUseCase
) : ViewModel() {

    private val _uiState = MutableStateFlow(HistoryUiState())
    val uiState: StateFlow<HistoryUiState> = _uiState.asStateFlow()

    init {
        loadHistory()
    }

    fun loadHistory() {
        viewModelScope.launch {
            _uiState.value = HistoryUiState(history = getWatchHistoryUseCase(), isLoading = false)
        }
    }
}
