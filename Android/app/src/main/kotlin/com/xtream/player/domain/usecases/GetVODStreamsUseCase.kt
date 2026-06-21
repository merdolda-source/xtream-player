// Android/app/src/main/kotlin/com/xtream/player/domain/usecases/GetVODStreamsUseCase.kt
package com.xtream.player.domain.usecases

import com.xtream.player.domain.entities.Stream
import com.xtream.player.domain.repositories.StreamRepository
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
