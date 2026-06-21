// Android/app/src/main/kotlin/com/xtream/player/domain/usecases/GetFavoritesUseCase.kt
package com.xtream.player.domain.usecases

import com.xtream.player.domain.entities.Stream
import com.xtream.player.domain.repositories.StreamRepository
import javax.inject.Inject

/** Returns the user's favorited streams. */
class GetFavoritesUseCase @Inject constructor(
    private val streamRepository: StreamRepository
) {
    suspend operator fun invoke(): List<Stream> = streamRepository.getFavorites()
}
