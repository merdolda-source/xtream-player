package com.erdin.player.domain.usecases

import com.erdin.player.domain.repositories.AuthRepository
import javax.inject.Inject

/** Removes a saved Xtream profile/account. */
class DeleteAccountUseCase @Inject constructor(
    private val authRepository: AuthRepository
) {
    suspend operator fun invoke(accountId: String) = authRepository.deleteAccount(accountId)
}
