// Android/app/src/main/kotlin/com/xtream/player/data/repositories/AuthRepositoryImpl.kt
package xtream.emin.player.data.repositories

import xtream.emin.player.data.remote.api.XtreamApiService
import xtream.emin.player.data.remote.api.XtreamUrlBuilder
import xtream.emin.player.data.session.SessionManager
import xtream.emin.player.domain.entities.User
import xtream.emin.player.domain.repositories.AuthRepository
import javax.inject.Inject

class AuthRepositoryImpl @Inject constructor(
    private val apiService: XtreamApiService,
    private val sessionManager: SessionManager
) : AuthRepository {

    override suspend fun login(host: String, username: String, password: String): Result<User> {
        return try {
            val response = apiService.login(XtreamUrlBuilder.playerApiUrl(host), username, password)
            val userInfo = response.userInfo
            if (userInfo == null || userInfo.auth != 1) {
                Result.failure(IllegalStateException("Invalid credentials or server response"))
            } else {
                val user = User(
                    host = host,
                    username = username,
                    password = password,
                    status = userInfo.status,
                    expDate = userInfo.expDate,
                    isTrial = userInfo.isTrial == "1",
                    maxConnections = userInfo.maxConnections
                )
                sessionManager.saveSession(user)
                Result.success(user)
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    override suspend fun logout() {
        sessionManager.clearSession()
    }

    override suspend fun getCurrentSession(): User? = sessionManager.getSession()

    override suspend fun isLoggedIn(): Boolean = sessionManager.getSession() != null
}
