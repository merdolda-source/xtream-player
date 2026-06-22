package com.erdin.player.domain.usecases

import com.erdin.player.domain.entities.Category
import com.erdin.player.domain.repositories.StreamRepository
import javax.inject.Inject

/** Fetches the live TV category list used to group channels in the UI. */
class GetLiveCategoriesUseCase @Inject constructor(
    private val streamRepository: StreamRepository
) {
    suspend operator fun invoke(host: String, username: String, password: String): List<Category> =
        streamRepository.getLiveCategories(host, username, password)
}
