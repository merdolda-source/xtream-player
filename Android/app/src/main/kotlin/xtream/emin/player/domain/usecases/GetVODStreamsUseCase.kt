// Android/app/src/main/kotlin/com/xtream/player/domain/usecases/GetVODStreamsUseCase.kt
package xtream.emin.player.domain.usecases

import xtream.emin.player.domain.entities.Stream
import xtream.emin.player.domain.repositories.StreamRepository
import javax.inject.Inject

/** Fetches the VOD (movies) catalog, optionally scoped to a category. */
class GetVODStreamsUseCase @Inject constructor(
    private val streamRepository: StreamRepository
) {
    suspend operator fun invoke(
        host: String,
        username: String,
        password: String,
        categoryId: String? = null
    ): List<Stream> = streamRepository.getVODStreams(host, username, password, categoryId)
}
