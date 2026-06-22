// Android/app/src/main/kotlin/com/xtream/player/domain/usecases/GetLiveStreamsUseCase.kt
package com.erdin.player.domain.usecases

import com.erdin.player.domain.entities.Stream
import com.erdin.player.domain.repositories.StreamRepository
import javax.inject.Inject

/** Fetches the live TV channel catalog, optionally scoped to a category. */
class GetLiveStreamsUseCase @Inject constructor(
    private val streamRepository: StreamRepository
) {
    suspend operator fun invoke(
        host: String,
        username: String,
        password: String,
        categoryId: String? = null
    ): List<Stream> = streamRepository.getLiveStreams(host, username, password, categoryId)
}
