package xtream.emin.player.domain.usecases

import xtream.emin.player.domain.entities.Category
import xtream.emin.player.domain.repositories.StreamRepository
import javax.inject.Inject

/** Fetches the live TV category list used to group channels in the UI. */
class GetLiveCategoriesUseCase @Inject constructor(
    private val streamRepository: StreamRepository
) {
    suspend operator fun invoke(host: String, username: String, password: String): List<Category> =
        streamRepository.getLiveCategories(host, username, password)
}
