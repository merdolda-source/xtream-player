// Android/app/src/main/kotlin/com/xtream/player/domain/usecases/LoginUseCase.kt
package xtream.emin.player.domain.usecases

import xtream.emin.player.domain.entities.User
import xtream.emin.player.domain.repositories.AuthRepository
import javax.inject.Inject

/** Authenticates against the Xtream server and persists the session on success. */
class LoginUseCase @Inject constructor(
    private val authRepository: AuthRepository
) {
    suspend operator fun invoke(host: String, username: String, password: String): Result<User> {
        if (host.isBlank() || username.isBlank() || password.isBlank()) {
            return Result.failure(IllegalArgumentException("Host, username and password are required"))
        }
        val normalizedHost = host.trim().trimEnd('/')
        return authRepository.login(normalizedHost, username.trim(), password)
    }
}
