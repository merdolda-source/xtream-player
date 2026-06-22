// Android/app/src/main/kotlin/com/xtream/player/domain/usecases/GetFavoritesUseCase.kt
package xtream.emin.player.domain.usecases

import xtream.emin.player.domain.entities.Stream
import xtream.emin.player.domain.repositories.StreamRepository
import javax.inject.Inject

/** Returns the user's favorited streams. */
class GetFavoritesUseCase @Inject constructor(
    private val streamRepository: StreamRepository
) {
    suspend operator fun invoke(): List<Stream> = streamRepository.getFavorites()
}
