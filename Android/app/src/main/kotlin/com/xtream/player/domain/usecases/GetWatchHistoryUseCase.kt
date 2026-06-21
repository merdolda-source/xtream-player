// Android/app/src/main/kotlin/com/xtream/player/domain/usecases/GetWatchHistoryUseCase.kt
package com.xtream.player.domain.usecases

import com.xtream.player.domain.entities.Stream
import com.xtream.player.domain.repositories.StreamRepository
import javax.inject.Inject

/** Returns the user's watch history, most recently watched first. */
class GetWatchHistoryUseCase @Inject constructor(
    private val streamRepository: StreamRepository
) {
    suspend operator fun invoke(): List<Stream> = streamRepository.getWatchHistory()
}
