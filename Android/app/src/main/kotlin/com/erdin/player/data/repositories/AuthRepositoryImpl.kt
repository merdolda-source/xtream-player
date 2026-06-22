// Android/app/src/main/kotlin/com/xtream/player/data/repositories/AuthRepositoryImpl.kt
package com.erdin.player.data.repositories

import com.erdin.player.data.remote.api.XtreamApiService
import com.erdin.player.data.remote.api.XtreamUrlBuilder
import com.erdin.player.data.session.SessionManager
import com.erdin.player.domain.entities.Account
import com.erdin.player.domain.entities.User
import com.erdin.player.domain.repositories.AuthRepository
import javax.inject.Inject

class AuthRepositoryImpl @Inject constructor(
    private val apiService: XtreamApiService,
    private val sessionManager: SessionManager
) : AuthRepository {

    override suspend fun login(host: String, username: String, password: String, profileName: String): Result<User> {
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
                    profileName = profileName,
                    status = userInfo.status,
                    expDate = userInfo.expDate,
                    isTrial = userInfo.isTrial == "1",
                    maxConnections = userInfo.maxConnections
                )
                sessionManager.saveSession(user, profileName)
                Result.success(user)
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    override suspend fun loginWithAccount(account: Account): Result<User> =
        login(account.host, account.username, account.password, account.profileName)

    override suspend fun logout() {
        sessionManager.clearSession()
    }

    override suspend fun getCurrentSession(): User? = sessionManager.getSession()

    override suspend fun isLoggedIn(): Boolean = sessionManager.getSession() != null

    override suspend fun getSavedAccounts(): List<Account> = sessionManager.getAccounts()

    override suspend fun deleteAccount(accountId: String) = sessionManager.deleteAccount(accountId)
}
