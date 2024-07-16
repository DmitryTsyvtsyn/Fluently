package io.github.dmitrytsyvtsyn.fluently.happening_detail

import android.Manifest
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.provider.Settings
import androidx.activity.compose.ManagedActivityResultLauncher
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.platform.LocalContext
import androidx.core.content.ContextCompat

internal class CalendarPermissionsRequester(
    private val context: Context,
    private val launcher: ManagedActivityResultLauncher<Array<String>, Map<String, Boolean>>,
    private val onPermissionAllowed: (allowed: Boolean) -> Unit
) {

    fun requestPermissions() {
        if (ContextCompat.checkSelfPermission(context, Manifest.permission.READ_CALENDAR) != PackageManager.PERMISSION_GRANTED ||
            ContextCompat.checkSelfPermission(context, Manifest.permission.WRITE_CALENDAR) != PackageManager.PERMISSION_GRANTED
        ) {
            launcher.launch(
                arrayOf(
                    Manifest.permission.READ_CALENDAR,
                    Manifest.permission.WRITE_CALENDAR
                )
            )
        } else {
            onPermissionAllowed.invoke(true)
        }
    }

}

@Composable
internal fun rememberCalendarPermissionsRequester(onPermissionAllowed: (allowed: Boolean) -> Unit): CalendarPermissionsRequester {
    val context = LocalContext.current
    val launcher = rememberLauncherForActivityResult(ActivityResultContracts.RequestMultiplePermissions()) { permissions ->
        val hasPermissionSatisfied = permissions[Manifest.permission.READ_CALENDAR] == true &&
                permissions[Manifest.permission.WRITE_CALENDAR] == true

        if (!hasPermissionSatisfied) {
            val intent = Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS)
                .setData(Uri.fromParts("package", context.packageName, null))
            context.startActivity(intent)
        }

        onPermissionAllowed.invoke(hasPermissionSatisfied)
    }

    return remember { CalendarPermissionsRequester(context, launcher, onPermissionAllowed) }
}