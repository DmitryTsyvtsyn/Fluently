package io.github.dmitrytsyvtsyn.interfunny

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
import androidx.compose.runtime.staticCompositionLocalOf
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.core.content.ContextCompat
import androidx.core.splashscreen.SplashScreen.Companion.installSplashScreen
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import io.github.dmitrytsyvtsyn.interfunny.core.navigation.LocalNavController
import io.github.dmitrytsyvtsyn.interfunny.core.navigation.Screens
import io.github.dmitrytsyvtsyn.interfunny.core.theme.InterFunnyTheme
import io.github.dmitrytsyvtsyn.interfunny.interview_event_detail.InterviewEventDetailScreen
import io.github.dmitrytsyvtsyn.interfunny.interview_event_list.InterviewEventListScreen
import io.github.dmitrytsyvtsyn.interfunny.interview_event_list.components.InterviewCalendarError
import io.github.dmitrytsyvtsyn.interfunny.theme_settings.InterviewThemeSettingsScreen
import io.github.dmitrytsyvtsyn.interfunny.theme_settings.SettingsViewModel

val LocalSettingsViewModel = staticCompositionLocalOf<SettingsViewModel> { error("Don't forget about me(") }

class MainActivity : ComponentActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        installSplashScreen()
        enableEdgeToEdge()
        setContent {
            val settingsViewModel = viewModel<SettingsViewModel>()
            val settingsViewState by settingsViewModel.state.collectAsState()

            InterFunnyTheme(contrast = settingsViewState.contrast) {

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
                    InterviewCalendarError(launcher)
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
            startDestination = Screens.InterviewEventListScreen.name,
            modifier = Modifier.background(MaterialTheme.colorScheme.background)
        ) {
            composable(
                route = Screens.InterviewEventListScreen.name
            ) {
                InterviewEventListScreen()
            }

            composable(
                route = "${Screens.InterviewEventDetailScreen.name}/{${Screens.InterviewEventDetailScreen.id}}",
                arguments = listOf(
                    navArgument(Screens.InterviewEventDetailScreen.id) { type = NavType.LongType }
                )
            ) { backStackEntry ->
                InterviewEventDetailScreen(
                    backStackEntry.arguments?.getLong(Screens.InterviewEventDetailScreen.id) ?: -1
                )
            }

            composable(
                route = Screens.InterviewThemeSettingsScreen.name,
            ) {
                InterviewThemeSettingsScreen()
            }

        }
    }
}
