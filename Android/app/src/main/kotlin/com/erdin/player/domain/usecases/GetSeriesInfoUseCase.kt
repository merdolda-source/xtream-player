// Android/app/src/main/kotlin/com/xtream/player/domain/usecases/GetSeriesInfoUseCase.kt
package com.erdin.player.domain.usecases

import com.erdin.player.domain.entities.SeriesDetails
import com.erdin.player.domain.repositories.StreamRepository
import javax.inject.Inject

/** Resolves the season/episode breakdown for a single series entry. */
class GetSeriesInfoUseCase @Inject constructor(
    private val streamRepository: StreamRepository
) {
    suspend operator fun invoke(
        host: String,
        username: String,
        password: String,
        seriesId: String,
        fallbackName: String,
        fallbackCover: String?
    ): SeriesDetails = streamRepository.getSeriesInfo(host, username, password, seriesId, fallbackName, fallbackCover)
}
