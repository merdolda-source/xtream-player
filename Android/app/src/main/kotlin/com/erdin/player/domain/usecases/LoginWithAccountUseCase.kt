package com.erdin.player.domain.usecases

import com.erdin.player.domain.entities.Account
import com.erdin.player.domain.entities.User
import com.erdin.player.domain.repositories.AuthRepository
import javax.inject.Inject

/** Re-authenticates a previously saved profile/account. */
class LoginWithAccountUseCase @Inject constructor(
    private val authRepository: AuthRepository
) {
    suspend operator fun invoke(account: Account): Result<User> = authRepository.loginWithAccount(account)
}
