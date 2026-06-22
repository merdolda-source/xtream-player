// Android/app/src/main/kotlin/xtream/emin/player/common/ads/AdConfig.kt
package xtream.emin.player.common.ads

/** Real AdMob ad unit IDs provided for this app's own AdMob account. */
object AdConfig {
    const val APPLICATION_ID = "ca-app-pub-2289573527937577~5012458331"
    const val INTERSTITIAL_AD_UNIT_ID = "ca-app-pub-2289573527937577/1957336650"
    const val NATIVE_AD_UNIT_ID = "ca-app-pub-2289573527937577/5777428099"
    const val APP_OPEN_AD_UNIT_ID = "ca-app-pub-2289573527937577/3851092581"

    /** Show one interstitial every N live-channel switches. */
    const val CHANNEL_SWITCHES_PER_INTERSTITIAL = 6
}
