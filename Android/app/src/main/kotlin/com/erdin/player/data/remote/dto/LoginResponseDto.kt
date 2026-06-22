// Android/app/src/main/kotlin/com/xtream/player/data/remote/dto/LoginResponseDto.kt
package com.erdin.player.data.remote.dto

import com.google.gson.annotations.SerializedName

/**
 * Response shape of Xtream Codes `player_api.php?username=..&password=..`
 * (no `action` param - this is the implicit "login" call).
 */
data class LoginResponseDto(
    @SerializedName("user_info") val userInfo: UserInfoDto? = null,
    @SerializedName("server_info") val serverInfo: ServerInfoDto? = null
)

data class UserInfoDto(
    @SerializedName("username") val username: String? = null,
    @SerializedName("status") val status: String? = null,
    @SerializedName("exp_date") val expDate: String? = null,
    @SerializedName("is_trial") val isTrial: String? = null,
    @SerializedName("max_connections") val maxConnections: String? = null,
    @SerializedName("auth") val auth: Int? = null
)

data class ServerInfoDto(
    @SerializedName("url") val url: String? = null,
    @SerializedName("port") val port: String? = null,
    @SerializedName("https_port") val httpsPort: String? = null,
    @SerializedName("server_protocol") val serverProtocol: String? = null
)
