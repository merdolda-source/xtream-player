package xtream.emin.player.domain.usecases

import xtream.emin.player.domain.entities.Category
import xtream.emin.player.domain.repositories.StreamRepository
import javax.inject.Inject

/** Fetches the VOD category list used to group movies in the UI. */
class GetVodCategoriesUseCase @Inject constructor(
    private val streamRepository: StreamRepository
) {
    suspend operator fun invoke(host: String, username: String, password: String): List<Category> =
        streamRepository.getVodCategories(host, username, password)
}
