// Android/app/src/main/kotlin/com/xtream/player/domain/usecases/ToggleFavoriteUseCase.kt
package com.xtream.player.domain.usecases

import com.xtream.player.domain.entities.Stream
import com.xtream.player.domain.repositories.StreamRepository
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
