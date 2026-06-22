// Android/app/src/main/kotlin/com/xtream/player/domain/usecases/SearchStreamsUseCase.kt
package com.erdin.player.domain.usecases

import com.erdin.player.domain.entities.Stream
import com.erdin.player.domain.repositories.StreamRepository
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
