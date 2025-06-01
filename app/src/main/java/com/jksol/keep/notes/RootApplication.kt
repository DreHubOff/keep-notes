package com.jksol.keep.notes

import android.app.Application
import android.util.Log
import com.jksol.keep.notes.data.FileManagerRepository
import dagger.hilt.android.HiltAndroidApp
import kotlinx.coroutines.CoroutineExceptionHandler
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.launch
import javax.inject.Inject
import javax.inject.Provider
import kotlin.time.Duration.Companion.days
import kotlin.time.Duration.Companion.milliseconds

private val TAG = RootApplication::class.simpleName

@HiltAndroidApp
class RootApplication : Application() {

    private val coroutineExceptionHandler = CoroutineExceptionHandler { _, throwable ->
        Log.e(TAG, "Uncaught exception", throwable)
    }

    val applicationScope = CoroutineScope(SupervisorJob() + Dispatchers.Default + coroutineExceptionHandler)

    @Inject
    lateinit var fileManagerRepository: Provider<FileManagerRepository>

    override fun onCreate() {
        super.onCreate()
        applicationScope.launch(Dispatchers.IO) {
            val currentDate = System.currentTimeMillis().milliseconds
            fileManagerRepository.get().getSharableDir().listFiles()?.forEach { file ->
                val modificationDate = file.lastModified().milliseconds
                if ((currentDate - modificationDate) > 1.days) {
                    if (file.delete()) {
                        Log.d(TAG, "File deleted: ${file.name}")
                    } else {
                        Log.e(TAG, "Failed to delete file: ${file.name}")
                    }
                }
            }
        }
    }
}