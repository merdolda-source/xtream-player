// Android/app/src/main/kotlin/com/xtream/player/domain/entities/User.kt
package com.erdin.player.domain.entities

/**
 * The current Xtream session: the credentials used to authenticate plus a
 * few account/server fields surfaced by the Xtream login response. The
 * host/username/password triple is what every other StreamRepository call
 * needs to build its player_api.php request.
 */
data class User(
    val host: String,
    val username: String,
    val password: String,
    val profileName: String? = null,
    val status: String? = null,
    val expDate: String? = null,
    val isTrial: Boolean = false,
    val maxConnections: String? = null
)
