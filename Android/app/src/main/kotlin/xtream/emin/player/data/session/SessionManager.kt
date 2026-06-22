// Android/app/src/main/kotlin/com/xtream/player/data/session/SessionManager.kt
package xtream.emin.player.data.session

import android.content.Context
import android.content.SharedPreferences
import androidx.security.crypto.EncryptedSharedPreferences
import androidx.security.crypto.MasterKey
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import xtream.emin.player.domain.entities.Account
import xtream.emin.player.domain.entities.User
import dagger.hilt.android.qualifiers.ApplicationContext
import javax.inject.Inject
import javax.inject.Singleton

/**
 * Persists the active Xtream session plus the list of saved
 * accounts/profiles using EncryptedSharedPreferences so credentials are
 * never stored in plaintext on disk.
 */
@Singleton
class SessionManager @Inject constructor(
    @ApplicationContext context: Context
) {
    private val gson = Gson()

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

    fun saveSession(user: User, profileName: String) {
        prefs.edit()
            .putString(KEY_HOST, user.host)
            .putString(KEY_USERNAME, user.username)
            .putString(KEY_PASSWORD, user.password)
            .putString(KEY_PROFILE_NAME, profileName)
            .putString(KEY_STATUS, user.status)
            .putString(KEY_EXP_DATE, user.expDate)
            .putBoolean(KEY_IS_TRIAL, user.isTrial)
            .putString(KEY_MAX_CONNECTIONS, user.maxConnections)
            .apply()

        upsertAccount(
            Account(
                id = accountId(user.host, user.username),
                profileName = profileName,
                host = user.host,
                username = user.username,
                password = user.password,
                status = user.status,
                expDate = user.expDate,
                isTrial = user.isTrial,
                maxConnections = user.maxConnections,
                lastUsedAt = System.currentTimeMillis()
            )
        )
    }

    fun getSession(): User? {
        val host = prefs.getString(KEY_HOST, null) ?: return null
        val username = prefs.getString(KEY_USERNAME, null) ?: return null
        val password = prefs.getString(KEY_PASSWORD, null) ?: return null
        return User(
            host = host,
            username = username,
            password = password,
            profileName = prefs.getString(KEY_PROFILE_NAME, null),
            status = prefs.getString(KEY_STATUS, null),
            expDate = prefs.getString(KEY_EXP_DATE, null),
            isTrial = prefs.getBoolean(KEY_IS_TRIAL, false),
            maxConnections = prefs.getString(KEY_MAX_CONNECTIONS, null)
        )
    }

    /** Clears only the active-session keys; saved accounts/profiles are kept. */
    fun clearSession() {
        prefs.edit()
            .remove(KEY_HOST)
            .remove(KEY_USERNAME)
            .remove(KEY_PASSWORD)
            .remove(KEY_PROFILE_NAME)
            .remove(KEY_STATUS)
            .remove(KEY_EXP_DATE)
            .remove(KEY_IS_TRIAL)
            .remove(KEY_MAX_CONNECTIONS)
            .apply()
    }

    fun getAccounts(): List<Account> {
        val json = prefs.getString(KEY_ACCOUNTS, null) ?: return emptyList()
        val type = object : TypeToken<List<Account>>() {}.type
        return runCatching { gson.fromJson<List<Account>>(json, type) }
            .getOrNull()
            .orEmpty()
            .sortedByDescending { it.lastUsedAt }
    }

    fun deleteAccount(accountId: String) {
        val updated = getAccounts().filterNot { it.id == accountId }
        prefs.edit().putString(KEY_ACCOUNTS, gson.toJson(updated)).apply()
    }

    private fun upsertAccount(account: Account) {
        val updated = getAccounts().filterNot { it.id == account.id } + account
        prefs.edit().putString(KEY_ACCOUNTS, gson.toJson(updated)).apply()
    }

    private fun accountId(host: String, username: String) = "$host|$username"

    companion object {
        private const val PREFS_FILE_NAME = "xtream_player_session"
        private const val KEY_HOST = "host"
        private const val KEY_USERNAME = "username"
        private const val KEY_PASSWORD = "password"
        private const val KEY_PROFILE_NAME = "profile_name"
        private const val KEY_STATUS = "status"
        private const val KEY_EXP_DATE = "exp_date"
        private const val KEY_IS_TRIAL = "is_trial"
        private const val KEY_MAX_CONNECTIONS = "max_connections"
        private const val KEY_ACCOUNTS = "accounts"
    }
}
