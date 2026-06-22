// Android/app/src/main/kotlin/com/xtream/player/domain/usecases/RecordWatchHistoryUseCase.kt
package com.erdin.player.domain.usecases

import com.erdin.player.domain.entities.Stream
import com.erdin.player.domain.repositories.StreamRepository
import javax.inject.Inject

/**
 * Records that a stream was played, so it shows up in watch history.
 *
 * [StreamRepository] doesn't expose a write method for history (only the
 * read-only [StreamRepository.getWatchHistory]), so the recording itself is
 * implemented as an optional capability ([HistoryRecorder]) that the
 * concrete repository implementation also provides. This keeps the existing
 * StreamRepository interface's method signatures untouched, per the
 * project's domain contract.
 */
class RecordWatchHistoryUseCase @Inject constructor(
    private val streamRepository: StreamRepository
) {
    suspend operator fun invoke(stream: Stream) {
        (streamRepository as? HistoryRecorder)?.recordWatched(stream)
    }
}

/**
 * Optional extension implemented by the concrete [StreamRepository] to
 * append a watch history entry without changing the public
 * [StreamRepository] interface contract.
 */
interface HistoryRecorder {
    suspend fun recordWatched(stream: Stream)
}
