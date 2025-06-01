package com.jksol.keep.notes.data

import android.content.Context
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.io.File
import javax.inject.Inject

class FileManagerRepository @Inject constructor(
    @ApplicationContext
    private val context: Context,
) {

    suspend fun createSharableFile(fileName: String): File {
        return withContext(Dispatchers.IO) {
            File(getSharableDir(), fileName).apply {
                if (!exists()) {
                    createNewFile()
                }
            }
        }
    }

    suspend fun getSharableDir(): File {
        return withContext(Dispatchers.IO) {
            File(context.cacheDir, "sharable").apply { mkdirs() }
        }
    }
}