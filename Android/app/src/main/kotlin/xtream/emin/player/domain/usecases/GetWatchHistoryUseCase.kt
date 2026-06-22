// Android/app/src/main/kotlin/com/xtream/player/domain/usecases/GetWatchHistoryUseCase.kt
package xtream.emin.player.domain.usecases

import xtream.emin.player.domain.entities.Stream
import xtream.emin.player.domain.repositories.StreamRepository
import javax.inject.Inject

/** Returns the user's watch history, most recently watched first. */
class GetWatchHistoryUseCase @Inject constructor(
    private val streamRepository: StreamRepository
) {
    suspend operator fun invoke(): List<Stream> = streamRepository.getWatchHistory()
}
