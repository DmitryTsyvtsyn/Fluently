package io.github.dmitrytsyvtsyn.fluently

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.background
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.ui.Modifier
import androidx.core.splashscreen.SplashScreen.Companion.installSplashScreen
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.rememberNavController
import io.github.dmitrytsyvtsyn.fluently.core.navigation.LocalNavController
import io.github.dmitrytsyvtsyn.fluently.core.navigation.coreDestinations
import io.github.dmitrytsyvtsyn.fluently.core.theme.FluentlyTheme
import io.github.dmitrytsyvtsyn.fluently.core.theme_settings_screen.LocalSettingsViewModel
import io.github.dmitrytsyvtsyn.fluently.core.theme_settings_screen.viewmodel.SettingsViewModel
import io.github.dmitrytsyvtsyn.fluently.happening_detail.navigation.happeningDetailDestination
import io.github.dmitrytsyvtsyn.fluently.happening_list.navigation.HappeningListDestination
import io.github.dmitrytsyvtsyn.fluently.happening_list.navigation.happeningListDestination
import io.github.dmitrytsyvtsyn.fluently.happening_pickers.navigation.dateTimePickerDestinations

class MainActivity : ComponentActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        installSplashScreen()
        enableEdgeToEdge()
        setContent {
            val settingsViewModel = viewModel<SettingsViewModel>()

            FluentlyTheme {
                val navController = rememberNavController()

                CompositionLocalProvider(
                    LocalNavController provides navController,
                    LocalSettingsViewModel provides settingsViewModel
                ) {
                    NavHost(
                        navController = navController,
                        startDestination = HappeningListDestination.route,
                        modifier = Modifier.background(MaterialTheme.colorScheme.background)
                    ) {
                        coreDestinations()
                        dateTimePickerDestinations()
                        happeningListDestination()
                        happeningDetailDestination()
                    }
                }
            }
        }
    }

}