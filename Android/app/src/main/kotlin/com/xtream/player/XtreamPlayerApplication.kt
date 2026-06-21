// Android/app/src/main/kotlin/com/xtream/player/XtreamPlayerApplication.kt
package com.xtream.player

import android.app.Application
import dagger.hilt.android.HiltAndroidApp
import com.xtream.player.common.utils.Logger

@HiltAndroidApp
class XtreamPlayerApplication : Application() {
    override fun onCreate() {
        super.onCreate()

        // Initialize Logger
        Logger.init(this)
        Logger.info("Xtream Player Application started")
    }
}
