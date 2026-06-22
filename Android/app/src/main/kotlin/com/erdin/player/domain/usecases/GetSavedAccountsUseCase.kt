package com.erdin.player.domain.usecases

import com.erdin.player.domain.entities.Account
import com.erdin.player.domain.repositories.AuthRepository
import javax.inject.Inject

/** Returns all saved Xtream profiles, most recently used first. */
class GetSavedAccountsUseCase @Inject constructor(
    private val authRepository: AuthRepository
) {
    suspend operator fun invoke(): List<Account> = authRepository.getSavedAccounts()
}
