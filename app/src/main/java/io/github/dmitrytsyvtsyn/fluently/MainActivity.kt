package io.github.dmitrytsyvtsyn.fluently

import android.Manifest
import android.content.Intent
import android.content.pm.PackageManager.PERMISSION_GRANTED
import android.net.Uri
import android.os.Bundle
import android.provider.Settings
import androidx.activity.ComponentActivity
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.background
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.core.content.ContextCompat
import androidx.core.splashscreen.SplashScreen.Companion.installSplashScreen
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.rememberNavController
import io.github.dmitrytsyvtsyn.fluently.components.HappeningCalendarError
import io.github.dmitrytsyvtsyn.fluently.core.navigation.LocalNavController
import io.github.dmitrytsyvtsyn.fluently.core.navigation.coreDestinations
import io.github.dmitrytsyvtsyn.fluently.core.theme_settings_screen.LocalSettingsViewModel
import io.github.dmitrytsyvtsyn.fluently.core.theme_settings_screen.viewmodel.SettingsViewModel
import io.github.dmitrytsyvtsyn.fluently.core.theme.FluentlyTheme
import io.github.dmitrytsyvtsyn.fluently.happening_detail.happeningDetailDestination
import io.github.dmitrytsyvtsyn.fluently.happening_list.HappeningListDestination
import io.github.dmitrytsyvtsyn.fluently.happening_list.happeningListDestination
import io.github.dmitrytsyvtsyn.fluently.happening_pickers.dateTimePickerDestinations

class MainActivity : ComponentActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        installSplashScreen()
        enableEdgeToEdge()
        setContent {
            val settingsViewModel = viewModel<SettingsViewModel>()
            val settingsViewState by settingsViewModel.viewState.collectAsState()

            FluentlyTheme(contrast = settingsViewState.contrast) {

                val hasPermissionSatisfiedState = remember { mutableStateOf(true) }
                val context = LocalContext.current
                val launcher = rememberLauncherForActivityResult(ActivityResultContracts.RequestMultiplePermissions()) { permissions ->
                    val hasPermissionSatisfied = permissions[Manifest.permission.READ_CALENDAR] == true &&
                            permissions[Manifest.permission.WRITE_CALENDAR] == true

                    if (!hasPermissionSatisfiedState.value && !hasPermissionSatisfied) {
                        val intent = Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS)
                            .setData(Uri.fromParts("package", context.packageName, null))
                        context.startActivity(intent)
                    }

                    hasPermissionSatisfiedState.value = hasPermissionSatisfied
                }

                LaunchedEffect(key1 = Unit) {
                    if (ContextCompat.checkSelfPermission(context, Manifest.permission.READ_CALENDAR) != PERMISSION_GRANTED ||
                        ContextCompat.checkSelfPermission(context,  Manifest.permission.WRITE_CALENDAR) != PERMISSION_GRANTED) {
                        launcher.launch(
                            arrayOf(
                                Manifest.permission.READ_CALENDAR,
                                Manifest.permission.WRITE_CALENDAR
                            )
                        )
                    }
                }

                if (!hasPermissionSatisfiedState.value) {
                    HappeningCalendarError(launcher)
                } else {
                    MainNavGraph(settingsViewModel = settingsViewModel)
                }
            }
        }
    }

}

@Composable
private fun MainNavGraph(settingsViewModel: SettingsViewModel) {
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
