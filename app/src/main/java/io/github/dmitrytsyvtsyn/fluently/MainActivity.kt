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
import androidx.compose.runtime.staticCompositionLocalOf
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.core.content.ContextCompat
import androidx.core.splashscreen.SplashScreen.Companion.installSplashScreen
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.dialog
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import io.github.dmitrytsyvtsyn.fluently.core.navigation.LocalNavController
import io.github.dmitrytsyvtsyn.fluently.core.navigation.Screens
import io.github.dmitrytsyvtsyn.fluently.core.theme.FluentlyTheme
import io.github.dmitrytsyvtsyn.fluently.happening_date_picker.HappeningDatePicker
import io.github.dmitrytsyvtsyn.fluently.happening_detail.HappeningDetailScreen
import io.github.dmitrytsyvtsyn.fluently.happening_list.HappeningListScreen
import io.github.dmitrytsyvtsyn.fluently.happening_list.components.HappeningCalendarError
import io.github.dmitrytsyvtsyn.fluently.happening_time_picker.HappeningTimePicker
import io.github.dmitrytsyvtsyn.fluently.theme_settings.ThemeSettingsScreen
import io.github.dmitrytsyvtsyn.fluently.theme_settings.viewmodel.SettingsViewModel
import kotlinx.coroutines.flow.flow

val LocalSettingsViewModel = staticCompositionLocalOf<SettingsViewModel> { error("Don't forget about me(") }

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
            startDestination = Screens.HappeningListScreen.NAME,
            modifier = Modifier.background(MaterialTheme.colorScheme.background)
        ) {
            composable(
                route = Screens.HappeningListScreen.NAME
            ) {
                HappeningListScreen()
            }

            composable(
                route = "${Screens.HappeningDetailScreen.NAME}/{${Screens.HappeningDetailScreen.ID}}/{${Screens.HappeningDetailScreen.INITIAL_DATE}}",
                arguments = listOf(
                    navArgument(Screens.HappeningDetailScreen.ID) { type = NavType.LongType },
                    navArgument(Screens.HappeningDetailScreen.INITIAL_DATE) { type = NavType.LongType }
                )
            ) { backStackEntry ->
                HappeningDetailScreen(
                    backStackEntry.arguments?.getLong(Screens.HappeningDetailScreen.ID) ?: -1,
                    backStackEntry.arguments?.getLong(Screens.HappeningDetailScreen.INITIAL_DATE) ?: System.currentTimeMillis()
                )
            }

            composable(
                route = Screens.HappeningThemeSettingsScreen.NAME,
            ) {
                ThemeSettingsScreen()
            }

            dialog(
                route = "${Screens.HappeningDatePickerDialog.NAME}?${Screens.HappeningDatePickerDialog.INITIAL_DATE}={${Screens.HappeningDatePickerDialog.INITIAL_DATE}}&${Screens.HappeningDatePickerDialog.MIN_DATE}={${Screens.HappeningDatePickerDialog.MIN_DATE}}&${Screens.HappeningDatePickerDialog.MAX_DATE}={${Screens.HappeningDatePickerDialog.MAX_DATE}}",
                arguments = listOf(
                    navArgument(Screens.HappeningDatePickerDialog.INITIAL_DATE) { type = NavType.LongType },
                    navArgument(Screens.HappeningDatePickerDialog.MIN_DATE) {
                        type = NavType.LongType
                        defaultValue = Long.MIN_VALUE
                    },
                    navArgument(Screens.HappeningDatePickerDialog.MAX_DATE) {
                        type = NavType.LongType
                        defaultValue = Long.MAX_VALUE
                    }
                )
            ) { backStackEntry ->
                val arguments = backStackEntry.arguments
                HappeningDatePicker(
                    initialDate = arguments?.getLong(Screens.HappeningDatePickerDialog.INITIAL_DATE) ?: error("initial_date is empty!"),
                    minDate = arguments.getLong(Screens.HappeningDatePickerDialog.MIN_DATE, Long.MIN_VALUE),
                    maxDate = arguments.getLong(Screens.HappeningDatePickerDialog.MAX_DATE, Long.MAX_VALUE),
                    dismiss = { navController.popBackStack() },
                    apply = { date ->
                        navController.previousBackStackEntry
                            ?.savedStateHandle
                            ?.set(Screens.HappeningDatePickerDialog.RESULT_KEY, date)
                        navController.popBackStack()
                    }
                )
            }

            dialog(
                route = "${Screens.HappeningTimePickerDialog.NAME}?${Screens.HappeningTimePickerDialog.START_HOURS}={${Screens.HappeningTimePickerDialog.START_HOURS}}&${Screens.HappeningTimePickerDialog.START_MINUTES}={${Screens.HappeningTimePickerDialog.START_MINUTES}}&${Screens.HappeningTimePickerDialog.END_HOURS}={${Screens.HappeningTimePickerDialog.END_HOURS}}&${Screens.HappeningTimePickerDialog.END_MINUTES}={${Screens.HappeningTimePickerDialog.END_MINUTES}}",
                arguments = listOf(
                    navArgument(Screens.HappeningTimePickerDialog.START_HOURS) { type = NavType.IntType },
                    navArgument(Screens.HappeningTimePickerDialog.START_MINUTES) { type = NavType.IntType },
                    navArgument(Screens.HappeningTimePickerDialog.END_HOURS) { type = NavType.IntType },
                    navArgument(Screens.HappeningTimePickerDialog.END_MINUTES) { type = NavType.IntType }
                )
            ) { backStackEntry ->
                val arguments = backStackEntry.arguments ?: error("${Screens.HappeningTimePickerDialog.NAME} arguments is empty!")
                HappeningTimePicker(
                    dismiss = {
                        navController.popBackStack()
                    },
                    apply = { startHours, startMinutes, endHours, endMinutes ->
                        navController.previousBackStackEntry
                            ?.savedStateHandle
                            ?.set(
                                Screens.HappeningTimePickerDialog.RESULT_KEY,
                                Screens.HappeningTimePickerDialog.Result(startHours, startMinutes, endHours, endMinutes)
                            )
                        navController.popBackStack()
                    },
                    startHours = arguments.getInt(Screens.HappeningTimePickerDialog.START_HOURS),
                    startMinutes = arguments.getInt(Screens.HappeningTimePickerDialog.START_MINUTES),
                    endHours = arguments.getInt(Screens.HappeningTimePickerDialog.END_HOURS),
                    endMinutes = arguments.getInt(Screens.HappeningTimePickerDialog.END_MINUTES)
                )
            }
        }
    }
}
