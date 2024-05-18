package io.github.dmitrytsyvtsyn.interfunny

import android.Manifest
import android.animation.ObjectAnimator
import android.content.Intent
import android.content.SharedPreferences
import android.content.pm.PackageManager.PERMISSION_GRANTED
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.provider.Settings
import android.util.Log
import android.view.View
import android.view.animation.AnticipateInterpolator
import androidx.activity.ComponentActivity
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.activity.result.registerForActivityResult
import androidx.annotation.RequiresApi
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
import androidx.core.animation.doOnEnd
import androidx.core.content.ContextCompat
import androidx.core.splashscreen.SplashScreen.Companion.installSplashScreen
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.dialog
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import io.github.dmitrytsyvtsyn.interfunny.core.di.DI
import io.github.dmitrytsyvtsyn.interfunny.core.navigation.LocalNavController
import io.github.dmitrytsyvtsyn.interfunny.core.navigation.Screens
import io.github.dmitrytsyvtsyn.interfunny.core.theme.InterFunnyTheme
import io.github.dmitrytsyvtsyn.interfunny.core.theme.ThemeContrast
import io.github.dmitrytsyvtsyn.interfunny.interview_event_detail.InterviewDatePicker
import io.github.dmitrytsyvtsyn.interfunny.interview_event_detail.InterviewEventDetailScreen
import io.github.dmitrytsyvtsyn.interfunny.interview_event_list.InterviewEventListScreen
import io.github.dmitrytsyvtsyn.interfunny.interview_event_list.components.InterviewCalendarError
import io.github.dmitrytsyvtsyn.interfunny.theme_settings.InterviewThemeSettingsScreen
import kotlinx.collections.immutable.PersistentList
import kotlinx.collections.immutable.toPersistentList
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class SettingsRepository(private val preferences: SharedPreferences) {

    private val editor = preferences.edit()

    suspend fun saveContrast(contrast: ThemeContrast) = withContext(Dispatchers.Default) {
        editor.putInt(theme_contrast_key, contrast.ordinal).commit()
    }

    suspend fun readContrast() = withContext(Dispatchers.Default) {
        val defaultValue = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) ThemeContrast.DYNAMIC.ordinal else ThemeContrast.LIGHT.ordinal
        ThemeContrast.entries[preferences.getInt(theme_contrast_key, defaultValue)]
    }

    companion object {
        private const val theme_contrast_key = "theme_contrast_key"
    }

}

data class SettingsViewState(
    val contrast: ThemeContrast,
    val contrasts: PersistentList<ThemeContrast>
)

class SettingsViewModel : ViewModel() {

    private val repository = SettingsRepository(DI.preferences)

    private val _state = MutableStateFlow(
        SettingsViewState(
            contrast = ThemeContrast.LIGHT,
            contrasts = ThemeContrast.entries.toPersistentList()
        )
    )
    val state: StateFlow<SettingsViewState> = _state

    fun changeContrast(contrast: ThemeContrast) {
        if (_state.value.contrast == contrast) return

        viewModelScope.launch {
            repository.saveContrast(contrast)
            _state.value = _state.value.copy(contrast = contrast)
        }
    }

}

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
