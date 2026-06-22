// Android/app/src/main/kotlin/xtream/emin/player/common/ads/AppOpenAdManager.kt
package com.erdin.player.common.ads

import android.content.Context
import com.google.android.gms.ads.AdError
import com.google.android.gms.ads.AdRequest
import com.google.android.gms.ads.FullScreenContentCallback
import com.google.android.gms.ads.LoadAdError
import com.google.android.gms.ads.appopen.AppOpenAd
import com.erdin.player.common.utils.Logger

/**
 * Shows an app-open ad whenever the app returns to the foreground (e.g. the
 * user leaves and re-enters), skipping only the very first cold launch so it
 * doesn't collide with the branded splash screen.
 *
 * Driven directly from CurrentActivityTracker's onActivityResumed rather than
 * ProcessLifecycleOwner: the latter's onStart fires before the Activity's own
 * onResume, so currentActivity would still be null at the moment this needs
 * it, on both cold start and background-return.
 */
object AppOpenAdManager {
    private var appOpenAd: AppOpenAd? = null
    private var isLoading = false
    private var isShowingAd = false
    private var isColdStart = true

    fun preload(context: Context) {
        if (appOpenAd != null || isLoading) return
        isLoading = true
        AppOpenAd.load(
            context.applicationContext,
            AdConfig.APP_OPEN_AD_UNIT_ID,
            AdRequest.Builder().build(),
            object : AppOpenAd.AppOpenAdLoadCallback() {
                override fun onAdLoaded(ad: AppOpenAd) {
                    isLoading = false
                    appOpenAd = ad
                }

                override fun onAdFailedToLoad(error: LoadAdError) {
                    isLoading = false
                    Logger.error("App open ad failed to load: ${error.message}")
                }
            }
        )
    }

    fun onActivityResumed() {
        if (isColdStart) {
            isColdStart = false
            preload(CurrentActivityTracker.currentActivity?.applicationContext ?: return)
            return
        }
        showAdIfAvailable()
    }

    private fun showAdIfAvailable() {
        if (isShowingAd) return
        val activity = CurrentActivityTracker.currentActivity
        val ad = appOpenAd
        if (activity == null || ad == null) {
            CurrentActivityTracker.currentActivity?.let { preload(it.applicationContext) }
            return
        }

        ad.fullScreenContentCallback = object : FullScreenContentCallback() {
            override fun onAdDismissedFullScreenContent() {
                isShowingAd = false
                appOpenAd = null
                preload(activity.applicationContext)
            }

            override fun onAdFailedToShowFullScreenContent(error: AdError) {
                isShowingAd = false
                appOpenAd = null
                preload(activity.applicationContext)
            }
        }
        isShowingAd = true
        ad.show(activity)
    }
}
