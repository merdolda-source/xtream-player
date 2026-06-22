package com.erdin.player.domain.entities

/**
 * A saved Xtream login, persisted locally so the user can switch between
 * multiple panels/profiles without retyping credentials each time.
 */
data class Account(
    val id: String,
    val profileName: String,
    val host: String,
    val username: String,
    val password: String,
    val status: String? = null,
    val expDate: String? = null,
    val isTrial: Boolean = false,
    val maxConnections: String? = null,
    val lastUsedAt: Long = System.currentTimeMillis()
)
