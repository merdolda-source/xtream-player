package com.erdin.player.domain.usecases

import com.erdin.player.domain.entities.Category
import com.erdin.player.domain.repositories.StreamRepository
import javax.inject.Inject

/** Fetches the series category list used to group shows in the UI. */
class GetSeriesCategoriesUseCase @Inject constructor(
    private val streamRepository: StreamRepository
) {
    suspend operator fun invoke(host: String, username: String, password: String): List<Category> =
        streamRepository.getSeriesCategories(host, username, password)
}
