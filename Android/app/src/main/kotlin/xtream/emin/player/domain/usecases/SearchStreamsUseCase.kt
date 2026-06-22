// Android/app/src/main/kotlin/com/xtream/player/domain/usecases/SearchStreamsUseCase.kt
package xtream.emin.player.domain.usecases

import xtream.emin.player.domain.entities.Stream
import xtream.emin.player.domain.repositories.StreamRepository
import javax.inject.Inject

/** Searches across the cached live/VOD/series catalogs by name. */
class SearchStreamsUseCase @Inject constructor(
    private val streamRepository: StreamRepository
) {
    suspend operator fun invoke(query: String): List<Stream> {
        if (query.isBlank()) return emptyList()
        return streamRepository.searchStreams(query.trim())
    }
}
