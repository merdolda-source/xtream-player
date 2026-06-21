// Android/app/src/main/kotlin/com/xtream/player/presentation/player/PlayerViewModel.kt
package com.xtream.player.presentation.player

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.xtream.player.common.utils.Logger
import com.xtream.player.data.remote.api.XtreamUrlBuilder
import com.xtream.player.domain.entities.Stream
import com.xtream.player.domain.entities.StreamType
import com.xtream.player.domain.repositories.AuthRepository
import com.xtream.player.domain.usecases.RecordWatchHistoryUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

data class PlayerUiState(
    val playbackUrl: String? = null,
    val errorMessage: String? = null
)

@HiltViewModel
class PlayerViewModel @Inject constructor(
    private val authRepository: AuthRepository,
    private val recordWatchHistoryUseCase: RecordWatchHistoryUseCase
) : ViewModel() {

    private val _uiState = MutableStateFlow(PlayerUiState())
    val uiState: StateFlow<PlayerUiState> = _uiState.asStateFlow()

    fun loadStream(streamId: String, streamType: StreamType, name: String, containerExtension: String?) {
        viewModelScope.launch {
            val user = authRepository.getCurrentSession()
            if (user == null) {
                _uiState.value = PlayerUiState(errorMessage = "Not logged in.")
                return@launch
            }
            val stream = Stream(
                streamId = streamId,
                name = name,
                streamType = streamType,
                containerExtension = containerExtension
            )
            val url = XtreamUrlBuilder.streamPlaybackUrl(user.host, user.username, user.password, stream)
            _uiState.value = PlayerUiState(playbackUrl = url)

            runCatching { recordWatchHistoryUseCase(stream) }
                .onFailure { Logger.error("Failed to record watch history", it) }
        }
    }
}
