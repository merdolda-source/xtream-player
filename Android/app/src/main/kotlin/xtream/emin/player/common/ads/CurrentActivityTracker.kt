// Android/app/src/main/kotlin/xtream/emin/player/common/ads/CurrentActivityTracker.kt
package xtream.emin.player.common.ads

import android.app.Activity
import android.app.Application
import android.os.Bundle
import java.lang.ref.WeakReference

/**
 * Interstitial/app-open ads need a foreground Activity to show against, but
 * the call sites that trigger them (ViewModel/NavHost callbacks) only have a
 * Context. Tracked via Application.ActivityLifecycleCallbacks so ad managers
 * can grab whichever Activity is currently on screen.
 */
object CurrentActivityTracker {
    private var currentActivityRef: WeakReference<Activity>? = null

    val currentActivity: Activity?
        get() = currentActivityRef?.get()

    fun register(application: Application) {
        application.registerActivityLifecycleCallbacks(object : Application.ActivityLifecycleCallbacks {
            override fun onActivityResumed(activity: Activity) {
                currentActivityRef = WeakReference(activity)
                AppOpenAdManager.onActivityResumed()
            }

            override fun onActivityPaused(activity: Activity) {
                if (currentActivityRef?.get() === activity) currentActivityRef = null
            }

            override fun onActivityCreated(activity: Activity, savedInstanceState: Bundle?) = Unit
            override fun onActivityStarted(activity: Activity) = Unit
            override fun onActivityStopped(activity: Activity) = Unit
            override fun onActivitySaveInstanceState(activity: Activity, outState: Bundle) = Unit
            override fun onActivityDestroyed(activity: Activity) = Unit
        })
    }
}
