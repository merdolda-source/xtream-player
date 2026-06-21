// Android/app/src/main/kotlin/com/xtream/player/presentation/MainActivity.kt
package com.xtream.player.presentation

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.compose.rememberNavController
import com.xtream.player.presentation.common.XtreamPlayerTheme
import com.xtream.player.presentation.navigation.NavRoutes
import com.xtream.player.presentation.navigation.XtreamNavHost
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
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
