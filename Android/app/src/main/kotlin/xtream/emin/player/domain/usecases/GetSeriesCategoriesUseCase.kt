package xtream.emin.player.domain.usecases

import xtream.emin.player.domain.entities.Category
import xtream.emin.player.domain.repositories.StreamRepository
import javax.inject.Inject

/** Fetches the series category list used to group shows in the UI. */
class GetSeriesCategoriesUseCase @Inject constructor(
    private val streamRepository: StreamRepository
) {
    suspend operator fun invoke(host: String, username: String, password: String): List<Category> =
        streamRepository.getSeriesCategories(host, username, password)
}
