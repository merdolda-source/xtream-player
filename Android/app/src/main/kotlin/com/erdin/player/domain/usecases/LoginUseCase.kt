// Android/app/src/main/kotlin/com/xtream/player/domain/usecases/LoginUseCase.kt
package com.erdin.player.domain.usecases

import com.erdin.player.domain.entities.User
import com.erdin.player.domain.repositories.AuthRepository
import javax.inject.Inject

/** Authenticates against the Xtream server and saves the profile on success. */
class LoginUseCase @Inject constructor(
    private val authRepository: AuthRepository
) {
    suspend operator fun invoke(host: String, username: String, password: String, profileName: String): Result<User> {
        if (host.isBlank() || username.isBlank() || password.isBlank()) {
            return Result.failure(IllegalArgumentException("Host, username and password are required"))
        }
        val normalizedHost = host.trim().trimEnd('/')
        val resolvedProfileName = profileName.trim().ifBlank { username.trim() }
        return authRepository.login(normalizedHost, username.trim(), password, resolvedProfileName)
    }
}
