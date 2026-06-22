package xtream.emin.player.domain.usecases

import xtream.emin.player.domain.entities.Account
import xtream.emin.player.domain.entities.User
import xtream.emin.player.domain.repositories.AuthRepository
import javax.inject.Inject

/** Re-authenticates a previously saved profile/account. */
class LoginWithAccountUseCase @Inject constructor(
    private val authRepository: AuthRepository
) {
    suspend operator fun invoke(account: Account): Result<User> = authRepository.loginWithAccount(account)
}
