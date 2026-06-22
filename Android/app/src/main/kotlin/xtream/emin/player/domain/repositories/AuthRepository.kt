// Android/app/src/main/kotlin/com/xtream/player/domain/repositories/AuthRepository.kt
package xtream.emin.player.domain.repositories

import xtream.emin.player.domain.entities.Account
import xtream.emin.player.domain.entities.User

/**
 * Handles Xtream Codes authentication and persistence of the active
 * session (host/username/password) so the user does not have to log in
 * every app launch. Also manages a list of saved accounts/profiles so the
 * user can switch between multiple Xtream panels.
 */
interface AuthRepository {

    /**
     * Validates the given credentials against the Xtream server's
     * player_api.php login action and, on success, persists them as the
     * current session and saves/updates the matching profile.
     */
    suspend fun login(host: String, username: String, password: String, profileName: String): Result<User>

    /** Re-authenticates a previously saved account, refreshing its session/status. */
    suspend fun loginWithAccount(account: Account): Result<User>

    /** Clears the persisted session, without removing saved accounts. */
    suspend fun logout()

    /** Returns the persisted session, or null if no user is logged in. */
    suspend fun getCurrentSession(): User?

    /** True if a session is currently persisted. */
    suspend fun isLoggedIn(): Boolean

    /** Returns all saved profiles/accounts, most recently used first. */
    suspend fun getSavedAccounts(): List<Account>

    /** Removes a saved profile/account. */
    suspend fun deleteAccount(accountId: String)
}
