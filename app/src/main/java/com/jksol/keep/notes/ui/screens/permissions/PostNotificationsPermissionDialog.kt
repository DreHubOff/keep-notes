package com.jksol.keep.notes.ui.screens.permissions

import android.Manifest
import android.os.Build
import androidx.activity.compose.LocalActivity
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.runtime.Composable
import androidx.compose.ui.res.stringResource
import com.jksol.keep.notes.R
import com.jksol.keep.notes.ui.shared.TextOnlyAlertDialog

@Composable
fun PostNotificationsPermissionDialog(
    onDismiss: () -> Unit,
    onGranted: () -> Unit,
    onOpenAppSettings: () -> Unit,
) {
    val permissionRequestLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.RequestPermission()
    ) { isGranted: Boolean ->
        if (isGranted) {
            onGranted()
        } else {
            onDismiss()
        }
    }

    val activity = LocalActivity.current

    TextOnlyAlertDialog(
        title = stringResource(R.string.allow_notifications_title),
        text = stringResource(R.string.post_notifications_permission_prompt),
        confirmButtonText = stringResource(R.string.allow),
        dismissButtonText = stringResource(R.string.deny),
        confirmAction = {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
                if (activity?.shouldShowRequestPermissionRationale(Manifest.permission.POST_NOTIFICATIONS) == false) {
                    onOpenAppSettings()
                } else {
                    permissionRequestLauncher.launch(Manifest.permission.POST_NOTIFICATIONS)
                }
            }
        },
        dismissAction = onDismiss,
    )
}