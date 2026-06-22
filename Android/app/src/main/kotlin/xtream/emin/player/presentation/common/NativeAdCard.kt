// Android/app/src/main/kotlin/xtream/emin/player/presentation/common/NativeAdCard.kt
package xtream.emin.player.presentation.common

import android.graphics.Typeface
import android.view.Gravity
import android.view.View
import android.widget.Button
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.compose.ui.viewinterop.AndroidView
import com.google.android.gms.ads.AdListener
import com.google.android.gms.ads.AdLoader
import com.google.android.gms.ads.AdRequest
import com.google.android.gms.ads.LoadAdError
import com.google.android.gms.ads.nativead.NativeAd
import com.google.android.gms.ads.nativead.NativeAdView
import xtream.emin.player.common.ads.AdConfig
import xtream.emin.player.common.utils.Logger

private class NativeAdChildViews(
    val adView: NativeAdView,
    val headline: TextView,
    val body: TextView,
    val icon: ImageView,
    val cta: Button
)

/** A real, natively-rendered AdMob native ad mapped into the app's own layout (not a placeholder/WebView). */
@Composable
fun NativeAdCard(modifier: Modifier = Modifier) {
    val context = LocalContext.current
    var nativeAd by remember { mutableStateOf<NativeAd?>(null) }
    var childViews by remember { mutableStateOf<NativeAdChildViews?>(null) }

    DisposableEffect(Unit) {
        val loader = AdLoader.Builder(context, AdConfig.NATIVE_AD_UNIT_ID)
            .forNativeAd { ad -> nativeAd = ad }
            .withAdListener(object : AdListener() {
                override fun onAdFailedToLoad(error: LoadAdError) {
                    Logger.error("Native ad failed to load: ${error.message}")
                }
            })
            .build()
        loader.loadAd(AdRequest.Builder().build())
        onDispose { nativeAd?.destroy() }
    }

    val ad = nativeAd ?: return

    Surface(
        modifier = modifier.fillMaxWidth().height(88.dp),
        shape = RoundedCornerShape(12.dp),
        color = MaterialTheme.colorScheme.surfaceVariant
    ) {
        AndroidView(
            modifier = Modifier.fillMaxWidth(),
            factory = { ctx ->
                val headline = TextView(ctx).apply { setTypeface(typeface, Typeface.BOLD) }
                val sponsoredLabel = TextView(ctx).apply { text = "Ad"; textSize = 10f }
                val body = TextView(ctx).apply { maxLines = 1 }
                val icon = ImageView(ctx).apply {
                    layoutParams = LinearLayout.LayoutParams(56, 56)
                    visibility = View.GONE
                }
                val cta = Button(ctx)

                val textColumn = LinearLayout(ctx).apply {
                    orientation = LinearLayout.VERTICAL
                    layoutParams = LinearLayout.LayoutParams(0, LinearLayout.LayoutParams.WRAP_CONTENT, 1f)
                    addView(sponsoredLabel)
                    addView(headline)
                    addView(body)
                }
                val root = LinearLayout(ctx).apply {
                    orientation = LinearLayout.HORIZONTAL
                    gravity = Gravity.CENTER_VERTICAL
                    setPadding(24, 12, 24, 12)
                    addView(icon)
                    addView(textColumn)
                    addView(cta)
                }

                val adView = NativeAdView(ctx).apply {
                    addView(root)
                    headlineView = headline
                    bodyView = body
                    callToActionView = cta
                    iconView = icon
                }

                childViews = NativeAdChildViews(adView, headline, body, icon, cta)
                adView
            },
            update = {
                val views = childViews ?: return@AndroidView
                views.headline.text = ad.headline
                views.body.text = ad.body.orEmpty()
                views.cta.text = ad.callToAction.orEmpty()
                val iconDrawable = ad.icon?.drawable
                if (iconDrawable != null) {
                    views.icon.setImageDrawable(iconDrawable)
                    views.icon.visibility = View.VISIBLE
                } else {
                    views.icon.visibility = View.GONE
                }
                views.adView.setNativeAd(ad)
            }
        )
    }
}
