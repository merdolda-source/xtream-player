// Android/app/src/main/kotlin/com/xtream/player/presentation/login/LoginViewModel.kt
package com.xtream.player.presentation.login

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.xtream.player.common.utils.Logger
import com.xtream.player.domain.usecases.LoginUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

data class LoginUiState(
    val host: String = "",
    val username: String = "",
    val password: String = "",
    val isLoading: Boolean = false,
    val errorMessage: String? = null,
    val loginSucceeded: Boolean = false
)

@HiltViewModel
class LoginViewModel @Inject constructor(
    private val loginUseCase: LoginUseCase
) : ViewModel() {

    private val _uiState = MutableStateFlow(LoginUiState())
    val uiState: StateFlow<LoginUiState> = _uiState.asStateFlow()

    fun onHostChanged(value: String) {
        _uiState.update { it.copy(host = value, errorMessage = null) }
    }

    fun onUsernameChanged(value: String) {
        _uiState.update { it.copy(username = value, errorMessage = null) }
    }

    fun onPasswordChanged(value: String) {
        _uiState.update { it.copy(password = value, errorMessage = null) }
    }

    fun login() {
        val state = _uiState.value
        if (state.host.isBlank() || state.username.isBlank() || state.password.isBlank()) {
            _uiState.update { it.copy(errorMessage = "Please fill in all fields.") }
            return
        }

        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true, errorMessage = null) }
            val result = loginUseCase(state.host, state.username, state.password)
            result.onSuccess {
                Logger.info("Login succeeded for host=${state.host}")
                _uiState.update { it.copy(isLoading = false, loginSucceeded = true) }
            }.onFailure { error ->
                Logger.error("Login failed", error)
                _uiState.update {
                    it.copy(
                        isLoading = false,
                        errorMessage = error.message ?: "Login failed. Check your credentials and try again."
                    )
                }
            }
        }
    }
}
