package com.jksol.keep.notes.ui.shared

import androidx.compose.material3.SnackbarDuration
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.SnackbarResult
import androidx.compose.runtime.Composable
import androidx.compose.runtime.rememberCoroutineScope
import kotlinx.coroutines.launch

@Composable
fun HandleSnackbarState(
    snackbarHostState: SnackbarHostState,
    snackbarEvent: SnackbarEvent?,
    onActionExecuted: (SnackbarEvent.Action) -> Unit,
) {
    if (snackbarEvent == null) return
    val coroutineScope = rememberCoroutineScope()
    val action: SnackbarEvent.Action? = snackbarEvent.action
    snackbarEvent.consume()?.let { message ->
        coroutineScope.launch {
            snackbarHostState.currentSnackbarData?.dismiss()
            val executionResult = snackbarHostState.showSnackbar(
                message = message,
                actionLabel = snackbarEvent.action?.label,
                duration = if (action != null) SnackbarDuration.Long else SnackbarDuration.Short
            )
            if (action != null && executionResult == SnackbarResult.ActionPerformed) {
                onActionExecuted(action)
            }
        }
    }
}