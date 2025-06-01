package com.jksol.keep.notes

import android.app.Application
import android.util.Log
import dagger.hilt.android.HiltAndroidApp
import kotlinx.coroutines.CoroutineExceptionHandler
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob

private val TAG = RootApplication::class.simpleName

@HiltAndroidApp
class RootApplication : Application() {

    private val coroutineExceptionHandler = CoroutineExceptionHandler { _, throwable ->
        Log.e(TAG, "Uncaught exception", throwable)
    }

    val applicationScope = CoroutineScope(SupervisorJob() + Dispatchers.Default + coroutineExceptionHandler)
}