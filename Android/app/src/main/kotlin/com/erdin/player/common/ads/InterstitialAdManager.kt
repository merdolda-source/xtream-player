// Android/app/src/main/kotlin/xtream/emin/player/common/ads/InterstitialAdManager.kt
package com.erdin.player.common.ads

import android.content.Context
import com.google.android.gms.ads.AdError
import com.google.android.gms.ads.AdRequest
import com.google.android.gms.ads.FullScreenContentCallback
import com.google.android.gms.ads.LoadAdError
import com.google.android.gms.ads.interstitial.InterstitialAd
import com.google.android.gms.ads.interstitial.InterstitialAdLoadCallback
import com.erdin.player.common.utils.Logger
import java.util.concurrent.atomic.AtomicInteger

/** Shows a "geçiş" (transition) interstitial every Nth live-channel switch. */
object InterstitialAdManager {
    private var interstitialAd: InterstitialAd? = null
    private var isLoading = false
    private val switchCount = AtomicInteger(0)

    fun preload(context: Context) {
        if (interstitialAd != null || isLoading) return
        isLoading = true
        InterstitialAd.load(
            context.applicationContext,
            AdConfig.INTERSTITIAL_AD_UNIT_ID,
            AdRequest.Builder().build(),
            object : InterstitialAdLoadCallback() {
                override fun onAdLoaded(ad: InterstitialAd) {
                    isLoading = false
                    interstitialAd = ad
                }

                override fun onAdFailedToLoad(error: LoadAdError) {
                    isLoading = false
                    interstitialAd = null
                    Logger.error("Interstitial ad failed to load: ${error.message}")
                }
            }
        )
    }

    /** Call once per live-channel switch; shows an interstitial every Nth call. */
    fun onChannelSwitched(context: Context) {
        val count = switchCount.incrementAndGet()
        if (count < AdConfig.CHANNEL_SWITCHES_PER_INTERSTITIAL) {
            preload(context)
            return
        }
        switchCount.set(0)

        val activity = CurrentActivityTracker.currentActivity
        val ad = interstitialAd
        if (activity == null || ad == null) {
            preload(context)
            return
        }

        ad.fullScreenContentCallback = object : FullScreenContentCallback() {
            override fun onAdDismissedFullScreenContent() {
                interstitialAd = null
                preload(context)
            }

            override fun onAdFailedToShowFullScreenContent(error: AdError) {
                interstitialAd = null
                preload(context)
            }
        }
        ad.show(activity)
    }
}
