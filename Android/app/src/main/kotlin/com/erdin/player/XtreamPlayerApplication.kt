// Android/app/src/main/kotlin/com/xtream/player/XtreamPlayerApplication.kt
package com.erdin.player

import android.app.Application
import com.google.android.gms.ads.MobileAds
import dagger.hilt.android.HiltAndroidApp
import com.erdin.player.common.ads.CurrentActivityTracker
import com.erdin.player.common.utils.Logger

@HiltAndroidApp
class XtreamPlayerApplication : Application() {
    override fun onCreate() {
        super.onCreate()

        // Initialize Logger
        Logger.init(this)
        Logger.info("Xtream Player Application started")

        CurrentActivityTracker.register(this)
        MobileAds.initialize(this)
    }
}
