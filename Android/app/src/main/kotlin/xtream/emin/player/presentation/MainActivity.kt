// Android/app/src/main/kotlin/com/xtream/player/presentation/MainActivity.kt
package xtream.emin.player.presentation

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.viewModels
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.core.splashscreen.SplashScreen.Companion.installSplashScreen
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.compose.rememberNavController
import xtream.emin.player.presentation.common.XtreamPlayerTheme
import xtream.emin.player.presentation.navigation.NavRoutes
import xtream.emin.player.presentation.navigation.XtreamNavHost
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class MainActivity : ComponentActivity() {
    private val mainViewModel: MainViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        val splashScreen = installSplashScreen()
        super.onCreate(savedInstanceState)
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

    if (isLoggedIn != null) {
        val startDestination = if (isLoggedIn == true) NavRoutes.HOME else NavRoutes.LOGIN
        XtreamNavHost(navController = navController, startDestination = startDestination)
    }
}
