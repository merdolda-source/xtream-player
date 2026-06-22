// Android/app/src/main/kotlin/xtream/emin/player/common/ads/AdConfig.kt
package com.erdin.player.common.ads

import com.erdin.player.BuildConfig

/**
 * Ad unit IDs come from BuildConfig, which resolves to this app's real
 * AdMob account in release builds and to Google's published sample ad units
 * in debug builds (those always have fill, so testing doesn't depend on a
 * brand-new real ad unit's AdMob approval/ramp-up time, and avoids generating
 * invalid traffic by tapping our own real ads during testing).
 */
object AdConfig {
    val APPLICATION_ID: String = BuildConfig.ADMOB_APP_ID
    val INTERSTITIAL_AD_UNIT_ID: String = BuildConfig.ADMOB_INTERSTITIAL_AD_UNIT_ID
    val NATIVE_AD_UNIT_ID: String = BuildConfig.ADMOB_NATIVE_AD_UNIT_ID
    val APP_OPEN_AD_UNIT_ID: String = BuildConfig.ADMOB_APP_OPEN_AD_UNIT_ID

    /** Show one interstitial every N live-channel switches. */
    const val CHANNEL_SWITCHES_PER_INTERSTITIAL = 6
}
