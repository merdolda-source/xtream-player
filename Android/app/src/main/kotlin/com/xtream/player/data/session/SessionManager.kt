// Android/app/src/main/kotlin/com/xtream/player/data/session/SessionManager.kt
package com.xtream.player.data.session

import android.content.Context
import android.content.SharedPreferences
import androidx.security.crypto.EncryptedSharedPreferences
import androidx.security.crypto.MasterKey
import com.xtream.player.domain.entities.User
import dagger.hilt.android.qualifiers.ApplicationContext
import javax.inject.Inject
import javax.inject.Singleton

/**
 * Persists the active Xtream session (host/username/password + a few
 * account fields) using EncryptedSharedPreferences so credentials are not
 * stored in plaintext on disk.
 */
@Singleton
class SessionManager @Inject constructor(
    @ApplicationContext context: Context
) {
    private val masterKey = MasterKey.Builder(context)
        .setKeyScheme(MasterKey.KeyScheme.AES256_GCM)
        .build()

    private val prefs: SharedPreferences = EncryptedSharedPreferences.create(
        context,
        PREFS_FILE_NAME,
        masterKey,
        EncryptedSharedPreferences.PrefKeyEncryptionScheme.AES256_SIV,
        EncryptedSharedPreferences.PrefValueEncryptionScheme.AES256_GCM
    )

    fun saveSession(user: User) {
        prefs.edit()
            .putString(KEY_HOST, user.host)
            .putString(KEY_USERNAME, user.username)
            .putString(KEY_PASSWORD, user.password)
            .putString(KEY_STATUS, user.status)
            .putString(KEY_EXP_DATE, user.expDate)
            .putBoolean(KEY_IS_TRIAL, user.isTrial)
            .putString(KEY_MAX_CONNECTIONS, user.maxConnections)
            .apply()
    }

    fun getSession(): User? {
        val host = prefs.getString(KEY_HOST, null) ?: return null
        val username = prefs.getString(KEY_USERNAME, null) ?: return null
        val password = prefs.getString(KEY_PASSWORD, null) ?: return null
        return User(
            host = host,
            username = username,
            password = password,
            status = prefs.getString(KEY_STATUS, null),
            expDate = prefs.getString(KEY_EXP_DATE, null),
            isTrial = prefs.getBoolean(KEY_IS_TRIAL, false),
            maxConnections = prefs.getString(KEY_MAX_CONNECTIONS, null)
        )
    }

    fun clearSession() {
        prefs.edit().clear().apply()
    }

    companion object {
        private const val PREFS_FILE_NAME = "xtream_player_session"
        private const val KEY_HOST = "host"
        private const val KEY_USERNAME = "username"
        private const val KEY_PASSWORD = "password"
        private const val KEY_STATUS = "status"
        private const val KEY_EXP_DATE = "exp_date"
        private const val KEY_IS_TRIAL = "is_trial"
        private const val KEY_MAX_CONNECTIONS = "max_connections"
    }
}
