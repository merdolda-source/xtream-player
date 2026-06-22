package xtream.emin.player.domain.usecases

import xtream.emin.player.domain.entities.Account
import xtream.emin.player.domain.repositories.AuthRepository
import javax.inject.Inject

/** Returns all saved Xtream profiles, most recently used first. */
class GetSavedAccountsUseCase @Inject constructor(
    private val authRepository: AuthRepository
) {
    suspend operator fun invoke(): List<Account> = authRepository.getSavedAccounts()
}
