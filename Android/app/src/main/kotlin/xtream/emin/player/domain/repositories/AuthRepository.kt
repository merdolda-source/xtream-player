// Android/app/src/main/kotlin/com/xtream/player/domain/repositories/AuthRepository.kt
package xtream.emin.player.domain.repositories

import xtream.emin.player.domain.entities.User

/**
 * Handles Xtream Codes authentication and persistence of the active
 * session (host/username/password) so the user does not have to log in
 * every app launch.
 */
interface AuthRepository {

    /**
     * Validates the given credentials against the Xtream server's
     * player_api.php login action and, on success, persists them as the
     * current session.
     */
    suspend fun login(host: String, username: String, password: String): Result<User>

    /** Clears the persisted session. */
    suspend fun logout()

    /** Returns the persisted session, or null if no user is logged in. */
    suspend fun getCurrentSession(): User?

    /** True if a session is currently persisted. */
    suspend fun isLoggedIn(): Boolean
}
