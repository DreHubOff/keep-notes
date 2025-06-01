package com.jksol.keep.notes.ui.intent

import android.content.Context
import android.content.Intent
import androidx.core.content.FileProvider
import com.jksol.keep.notes.BuildConfig
import dagger.hilt.android.qualifiers.ApplicationContext
import java.io.File
import javax.inject.Inject

class ShareFileIntentBuilder @Inject constructor(@ApplicationContext private val context: Context) {

    fun build(
        file: File,
        mimeType: String = "application/pdf",
        chooserTitle: String = file.name,
    ): Intent? {
        val uri = FileProvider.getUriForFile(context, BuildConfig.FILE_PROVIDER_AUTHORITY, file)

        val shareIntent = Intent(Intent.ACTION_SEND).apply {
            type = mimeType
            putExtra(Intent.EXTRA_STREAM, uri)
            addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)
        }
        return Intent.createChooser(shareIntent, chooserTitle)
    }
}