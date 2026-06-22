// Android/app/src/main/kotlin/xtream/emin/player/common/utils/LocaleHelper.kt
package com.erdin.player.common.utils

import android.content.Context
import android.content.res.Configuration
import java.util.Locale

/**
 * AppCompatDelegate.setApplicationLocales() only auto-applies the chosen
 * locale to resources without code changes on API 33+ (via the platform
 * LocaleManager) or inside an AppCompatActivity on older APIs. Since
 * MainActivity is a plain ComponentActivity (kept that way to avoid
 * destabilizing the splash-screen theme chain), we wrap the base Context
 * ourselves with the saved locale on every API level instead.
 */
object LocaleHelper {
    private const val PREFS_NAME = "locale_prefs"
    private const val KEY_LANGUAGE_TAG = "language_tag"

    fun getLanguageTag(context: Context): String? =
        context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE).getString(KEY_LANGUAGE_TAG, null)

    fun setLanguageTag(context: Context, tag: String) {
        context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)
            .edit()
            .putString(KEY_LANGUAGE_TAG, tag)
            .apply()
    }

    fun wrap(context: Context): Context {
        val tag = getLanguageTag(context) ?: return context
        val locale = Locale.forLanguageTag(tag)
        Locale.setDefault(locale)
        val config = Configuration(context.resources.configuration)
        config.setLocale(locale)
        config.setLayoutDirection(locale)
        return context.createConfigurationContext(config)
    }
}
