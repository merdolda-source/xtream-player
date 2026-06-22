// Android/app/src/main/kotlin/com/xtream/player/domain/usecases/ToggleFavoriteUseCase.kt
package xtream.emin.player.domain.usecases

import xtream.emin.player.domain.entities.Stream
import xtream.emin.player.domain.repositories.StreamRepository
import javax.inject.Inject

/** Adds or removes a stream from favorites depending on its current state. */
class ToggleFavoriteUseCase @Inject constructor(
    private val streamRepository: StreamRepository
) {
    suspend operator fun invoke(stream: Stream, isCurrentlyFavorite: Boolean) {
        if (isCurrentlyFavorite) {
            streamRepository.removeFavorite(stream.streamId)
        } else {
            streamRepository.addFavorite(stream)
        }
    }
}
