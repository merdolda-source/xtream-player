// Android/app/src/main/kotlin/com/xtream/player/presentation/login/LoginViewModel.kt
package xtream.emin.player.presentation.login

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import xtream.emin.player.common.utils.Logger
import xtream.emin.player.domain.entities.Account
import xtream.emin.player.domain.usecases.DeleteAccountUseCase
import xtream.emin.player.domain.usecases.GetSavedAccountsUseCase
import xtream.emin.player.domain.usecases.LoginUseCase
import xtream.emin.player.domain.usecases.LoginWithAccountUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

data class LoginUiState(
    val profileName: String = "",
    val host: String = "",
    val username: String = "",
    val password: String = "",
    val isLoading: Boolean = false,
    val errorMessage: String? = null,
    val loginSucceeded: Boolean = false,
    val savedAccounts: List<Account> = emptyList(),
    val showAddForm: Boolean = false
)

@HiltViewModel
class LoginViewModel @Inject constructor(
    private val loginUseCase: LoginUseCase,
    private val loginWithAccountUseCase: LoginWithAccountUseCase,
    private val getSavedAccountsUseCase: GetSavedAccountsUseCase,
    private val deleteAccountUseCase: DeleteAccountUseCase
) : ViewModel() {

    private val _uiState = MutableStateFlow(LoginUiState())
    val uiState: StateFlow<LoginUiState> = _uiState.asStateFlow()

    init {
        refreshSavedAccounts()
    }

    private fun refreshSavedAccounts() {
        viewModelScope.launch {
            val accounts = getSavedAccountsUseCase()
            _uiState.update { it.copy(savedAccounts = accounts, showAddForm = accounts.isEmpty()) }
        }
    }

    fun onProfileNameChanged(value: String) {
        _uiState.update { it.copy(profileName = value, errorMessage = null) }
    }

    fun onHostChanged(value: String) {
        _uiState.update { it.copy(host = value, errorMessage = null) }
    }

    fun onUsernameChanged(value: String) {
        _uiState.update { it.copy(username = value, errorMessage = null) }
    }

    fun onPasswordChanged(value: String) {
        _uiState.update { it.copy(password = value, errorMessage = null) }
    }

    fun showAddAccountForm() {
        _uiState.update {
            it.copy(
                showAddForm = true,
                profileName = "",
                host = "",
                username = "",
                password = "",
                errorMessage = null
            )
        }
    }

    fun cancelAddAccountForm() {
        _uiState.update { it.copy(showAddForm = false, errorMessage = null) }
    }

    fun login() {
        val state = _uiState.value
        if (state.host.isBlank() || state.username.isBlank() || state.password.isBlank()) {
            _uiState.update { it.copy(errorMessage = "Please fill in all fields.") }
            return
        }

        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true, errorMessage = null) }
            val result = loginUseCase(state.host, state.username, state.password, state.profileName)
            handleLoginResult(result.map { state.host }, host = state.host)
        }
    }

    fun loginWithAccount(account: Account) {
        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true, errorMessage = null) }
            val result = loginWithAccountUseCase(account)
            handleLoginResult(result.map { account.host }, host = account.host)
        }
    }

    private fun handleLoginResult(result: Result<String>, host: String) {
        result.onSuccess {
            Logger.info("Login succeeded for host=$host")
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

    fun deleteAccount(account: Account) {
        viewModelScope.launch {
            deleteAccountUseCase(account.id)
            refreshSavedAccounts()
        }
    }
}
