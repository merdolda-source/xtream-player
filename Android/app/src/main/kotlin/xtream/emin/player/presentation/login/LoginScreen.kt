// Android/app/src/main/kotlin/com/xtream/player/presentation/login/LoginScreen.kt
package xtream.emin.player.presentation.login

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Person
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.hilt.navigation.compose.hiltViewModel
import xtream.emin.player.R
import xtream.emin.player.common.utils.ExpiryFormatter
import xtream.emin.player.domain.entities.Account

@Composable
fun LoginScreen(
    onLoginSuccess: () -> Unit,
    viewModel: LoginViewModel = hiltViewModel()
) {
    val uiState by viewModel.uiState.collectAsState()

    LaunchedEffect(uiState.loginSucceeded) {
        if (uiState.loginSucceeded) {
            onLoginSuccess()
        }
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(
                Brush.verticalGradient(
                    colors = listOf(
                        MaterialTheme.colorScheme.background,
                        MaterialTheme.colorScheme.surfaceVariant
                    )
                )
            )
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(horizontal = 24.dp)
                .padding(top = 56.dp, bottom = 24.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Image(
                painter = painterResource(R.drawable.logo_full),
                contentDescription = null,
                modifier = Modifier.size(96.dp)
            )
            Text(
                text = stringRes(R.string.login_title),
                style = MaterialTheme.typography.headlineSmall,
                fontWeight = FontWeight.Bold,
                modifier = Modifier.padding(top = 16.dp)
            )
            Text(
                text = stringRes(R.string.login_subtitle),
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onBackground.copy(alpha = 0.7f),
                modifier = Modifier.padding(top = 4.dp)
            )

            if (uiState.showAddForm) {
                AddProfileForm(
                    uiState = uiState,
                    showCancel = uiState.savedAccounts.isNotEmpty(),
                    onProfileNameChanged = viewModel::onProfileNameChanged,
                    onHostChanged = viewModel::onHostChanged,
                    onUsernameChanged = viewModel::onUsernameChanged,
                    onPasswordChanged = viewModel::onPasswordChanged,
                    onSubmit = viewModel::login,
                    onCancel = viewModel::cancelAddAccountForm
                )
            } else {
                ProfilePicker(
                    accounts = uiState.savedAccounts,
                    isLoading = uiState.isLoading,
                    errorMessage = uiState.errorMessage,
                    onAccountClick = viewModel::loginWithAccount,
                    onAccountDelete = viewModel::deleteAccount,
                    onAddProfile = viewModel::showAddAccountForm
                )
            }
        }
    }
}

@Composable
private fun ProfilePicker(
    accounts: List<Account>,
    isLoading: Boolean,
    errorMessage: String?,
    onAccountClick: (Account) -> Unit,
    onAccountDelete: (Account) -> Unit,
    onAddProfile: () -> Unit
) {
    Text(
        text = stringRes(R.string.login_profiles_title),
        style = MaterialTheme.typography.titleMedium,
        modifier = Modifier.padding(top = 32.dp).fillMaxWidth()
    )

    if (isLoading) {
        CircularProgressIndicator(modifier = Modifier.padding(top = 24.dp))
    } else {
        LazyVerticalGrid(
            columns = GridCells.Fixed(2),
            modifier = Modifier.fillMaxWidth().padding(top = 12.dp),
            contentPadding = PaddingValues(bottom = 16.dp),
            horizontalArrangement = Arrangement.spacedBy(12.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            items(accounts, key = { it.id }) { account ->
                ProfileCard(
                    account = account,
                    onClick = { onAccountClick(account) },
                    onDelete = { onAccountDelete(account) }
                )
            }
            item {
                AddProfileCard(onClick = onAddProfile)
            }
        }
    }

    errorMessage?.let { error ->
        Text(
            text = error,
            color = MaterialTheme.colorScheme.error,
            modifier = Modifier.padding(top = 8.dp)
        )
    }
}

@OptIn(androidx.compose.material3.ExperimentalMaterial3Api::class)
@Composable
private fun ProfileCard(account: Account, onClick: () -> Unit, onDelete: () -> Unit) {
    Card(
        onClick = onClick,
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(18.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
    ) {
        Column(modifier = Modifier.fillMaxWidth().padding(14.dp)) {
            Row(verticalAlignment = Alignment.CenterVertically, modifier = Modifier.fillMaxWidth()) {
                Surface(
                    shape = CircleShape,
                    color = MaterialTheme.colorScheme.primary.copy(alpha = 0.15f),
                    modifier = Modifier.size(40.dp)
                ) {
                    Box(contentAlignment = Alignment.Center, modifier = Modifier.fillMaxSize()) {
                        Icon(
                            Icons.Filled.Person,
                            contentDescription = null,
                            tint = MaterialTheme.colorScheme.primary
                        )
                    }
                }
                Box(modifier = Modifier.weight(1f))
                IconButton(onClick = onDelete, modifier = Modifier.size(32.dp)) {
                    Icon(
                        Icons.Filled.Delete,
                        contentDescription = stringRes(R.string.login_delete_profile),
                        tint = MaterialTheme.colorScheme.onSurfaceVariant,
                        modifier = Modifier.size(18.dp)
                    )
                }
            }
            Text(
                text = account.profileName,
                style = MaterialTheme.typography.titleSmall,
                fontWeight = FontWeight.SemiBold,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis,
                modifier = Modifier.padding(top = 8.dp)
            )
            Text(
                text = account.username,
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis
            )
            val expiry = ExpiryFormatter.format(account.expDate)
            Text(
                text = when {
                    account.isTrial -> stringRes(R.string.login_trial_badge)
                    expiry != null -> stringRes(R.string.login_expires_label).format(expiry)
                    else -> stringRes(R.string.login_unlimited)
                },
                style = MaterialTheme.typography.labelSmall,
                color = if (ExpiryFormatter.isExpired(account.expDate)) {
                    MaterialTheme.colorScheme.error
                } else {
                    MaterialTheme.colorScheme.secondary
                },
                modifier = Modifier.padding(top = 4.dp)
            )
        }
    }
}

@OptIn(androidx.compose.material3.ExperimentalMaterial3Api::class)
@Composable
private fun AddProfileCard(onClick: () -> Unit) {
    Card(
        onClick = onClick,
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(18.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant)
    ) {
        Column(
            modifier = Modifier.fillMaxWidth().padding(14.dp).size(118.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            Icon(Icons.Filled.Add, contentDescription = null, tint = MaterialTheme.colorScheme.primary)
            Text(
                text = stringRes(R.string.login_add_profile),
                style = MaterialTheme.typography.bodySmall,
                modifier = Modifier.padding(top = 6.dp)
            )
        }
    }
}

@Composable
private fun AddProfileForm(
    uiState: LoginUiState,
    showCancel: Boolean,
    onProfileNameChanged: (String) -> Unit,
    onHostChanged: (String) -> Unit,
    onUsernameChanged: (String) -> Unit,
    onPasswordChanged: (String) -> Unit,
    onSubmit: () -> Unit,
    onCancel: () -> Unit
) {
    Card(
        modifier = Modifier.fillMaxWidth().padding(top = 28.dp),
        shape = RoundedCornerShape(20.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
    ) {
        Column(modifier = Modifier.fillMaxWidth().padding(20.dp)) {
            Text(
                text = stringRes(R.string.login_new_profile_title),
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.SemiBold
            )

            OutlinedTextField(
                value = uiState.profileName,
                onValueChange = onProfileNameChanged,
                label = { Text(stringRes(R.string.login_profile_name_label)) },
                modifier = Modifier.fillMaxWidth().padding(top = 16.dp),
                singleLine = true
            )

            OutlinedTextField(
                value = uiState.host,
                onValueChange = onHostChanged,
                label = { Text(stringRes(R.string.login_host_label)) },
                modifier = Modifier.fillMaxWidth().padding(top = 12.dp),
                singleLine = true
            )

            OutlinedTextField(
                value = uiState.username,
                onValueChange = onUsernameChanged,
                label = { Text(stringRes(R.string.login_username_label)) },
                modifier = Modifier.fillMaxWidth().padding(top = 12.dp),
                singleLine = true
            )

            OutlinedTextField(
                value = uiState.password,
                onValueChange = onPasswordChanged,
                label = { Text(stringRes(R.string.login_password_label)) },
                modifier = Modifier.fillMaxWidth().padding(top = 12.dp),
                singleLine = true,
                visualTransformation = PasswordVisualTransformation()
            )

            uiState.errorMessage?.let { error ->
                Text(
                    text = error,
                    color = MaterialTheme.colorScheme.error,
                    modifier = Modifier.padding(top = 8.dp)
                )
            }

            if (uiState.isLoading) {
                CircularProgressIndicator(
                    modifier = Modifier
                        .padding(top = 16.dp)
                        .align(Alignment.CenterHorizontally)
                )
            } else {
                Button(
                    onClick = onSubmit,
                    modifier = Modifier.fillMaxWidth().padding(top = 16.dp)
                ) {
                    Text(stringRes(R.string.login_button))
                }
                if (showCancel) {
                    OutlinedButton(
                        onClick = onCancel,
                        modifier = Modifier.fillMaxWidth().padding(top = 8.dp)
                    ) {
                        Text(stringRes(R.string.login_cancel))
                    }
                }
            }
        }
    }
}

@Composable
private fun stringRes(id: Int): String = androidx.compose.ui.res.stringResource(id)
