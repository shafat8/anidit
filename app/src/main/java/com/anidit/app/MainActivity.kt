package com.anidit.app

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.anidit.app.ui.nav.Routes
import com.anidit.app.ui.screens.ExportScreen
import com.anidit.app.ui.screens.HomeScreen
import com.anidit.app.ui.screens.ImportMediaScreen
import com.anidit.app.ui.screens.PromptGenerateScreen
import com.anidit.app.ui.screens.TimelineScreen
import com.anidit.app.ui.theme.AniDitColorScheme
import com.anidit.app.ui.theme.AniDitShapes
import com.anidit.app.ui.theme.AniDitTypography
import com.anidit.app.ui.theme.Ink

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            MaterialTheme(
                colorScheme = AniDitColorScheme,
                typography = AniDitTypography,
                shapes = AniDitShapes
            ) {
                Surface(color = Ink) {
                    val navController = rememberNavController()
                    NavHost(navController = navController, startDestination = Routes.HOME) {
                        composable(Routes.HOME) { HomeScreen(navController) }
                        composable(Routes.IMPORT) { ImportMediaScreen(navController) }
                        composable(Routes.PROMPT_GENERATE) { PromptGenerateScreen(navController) }
                        composable(Routes.TIMELINE) { TimelineScreen(navController) }
                        composable(Routes.EXPORT) { ExportScreen(navController) }
                    }
                }
            }
        }
    }
}
