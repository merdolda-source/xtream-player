// Android/app/src/main/kotlin/xtream/emin/player/presentation/common/ThemePreferences.kt
package com.erdin.player.presentation.common

import android.content.Context
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow

/**
 * User-controlled dark/light override. Null means "follow the system
 * setting" (the previous, only, behavior); true/false pins the app to
 * dark or light regardless of the system theme.
 */
object ThemePreferences {
    private const val PREFS_NAME = "theme_prefs"
    private const val KEY_DARK_OVERRIDE = "dark_override"

    private val _darkOverride = MutableStateFlow<Boolean?>(null)
    val darkOverride: StateFlow<Boolean?> = _darkOverride.asStateFlow()
    private var initialized = false

    fun init(context: Context) {
        if (initialized) return
        initialized = true
        val prefs = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)
        _darkOverride.value = when {
            !prefs.contains(KEY_DARK_OVERRIDE) -> null
            else -> prefs.getBoolean(KEY_DARK_OVERRIDE, false)
        }
    }

    fun toggle(context: Context, currentlyDark: Boolean) {
        val next = !currentlyDark
        _darkOverride.value = next
        context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)
            .edit()
            .putBoolean(KEY_DARK_OVERRIDE, next)
            .apply()
    }
}
