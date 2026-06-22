// Android/app/src/main/kotlin/com/xtream/player/presentation/MainViewModel.kt
package xtream.emin.player.presentation

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import xtream.emin.player.domain.repositories.AuthRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

/** Decides whether the NavHost should start at Login or Home based on a persisted session. */
@HiltViewModel
class MainViewModel @Inject constructor(
    private val authRepository: AuthRepository
) : ViewModel() {

    private val _isLoggedIn = MutableStateFlow<Boolean?>(null)
    val isLoggedIn: StateFlow<Boolean?> = _isLoggedIn

    init {
        viewModelScope.launch {
            _isLoggedIn.value = authRepository.isLoggedIn()
        }
    }
}
