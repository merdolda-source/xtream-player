// Android/app/src/main/kotlin/com/xtream/player/presentation/MainActivity.kt
package com.erdin.player.presentation

import android.content.Context
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.viewModels
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.size
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import androidx.core.splashscreen.SplashScreen.Companion.installSplashScreen
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.compose.rememberNavController
import kotlinx.coroutines.delay
import com.erdin.player.R
import com.erdin.player.common.utils.LocaleHelper
import com.erdin.player.presentation.common.ThemePreferences
import com.erdin.player.presentation.common.XtreamPlayerTheme
import com.erdin.player.presentation.navigation.NavRoutes
import com.erdin.player.presentation.navigation.XtreamNavHost
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class MainActivity : ComponentActivity() {
    private val mainViewModel: MainViewModel by viewModels()

    override fun attachBaseContext(newBase: Context) {
        super.attachBaseContext(LocaleHelper.wrap(newBase))
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        val splashScreen = installSplashScreen()
        super.onCreate(savedInstanceState)
        ThemePreferences.init(this)
        splashScreen.setKeepOnScreenCondition { mainViewModel.isLoggedIn.value == null }
        setContent {
            XtreamPlayerTheme {
                Surface(modifier = Modifier.fillMaxSize()) {
                    XtreamPlayerApp()
                }
            }
        }
    }
}

@Composable
private fun XtreamPlayerApp(viewModel: MainViewModel = hiltViewModel()) {
    val isLoggedIn by viewModel.isLoggedIn.collectAsState()
    val navController = rememberNavController()

    // The OS SplashScreen API clips the launch icon to a small safe zone, so
    // the full logo + wordmark never gets to show there. Show it ourselves
    // for a brief moment right after Compose takes over, then fade away.
    var showBrandedSplash by remember { mutableStateOf(true) }
    LaunchedEffect(Unit) {
        delay(1100)
        showBrandedSplash = false
    }

    Box(modifier = Modifier.fillMaxSize()) {
        if (isLoggedIn != null) {
            val startDestination = if (isLoggedIn == true) NavRoutes.HOME else NavRoutes.LOGIN
            XtreamNavHost(navController = navController, startDestination = startDestination)
        }

        AnimatedVisibility(
            visible = showBrandedSplash,
            exit = fadeOut(animationSpec = tween(400))
        ) {
            BrandedSplashScreen()
        }
    }
}

@Composable
private fun BrandedSplashScreen() {
    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Color(0xFF110C10)),
        contentAlignment = Alignment.Center
    ) {
        Image(
            painter = painterResource(R.drawable.logo_full),
            contentDescription = null,
            modifier = Modifier.size(220.dp)
        )
    }
}
