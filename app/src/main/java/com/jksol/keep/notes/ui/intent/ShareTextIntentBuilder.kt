package com.jksol.keep.notes.ui.intent

import android.content.Intent
import javax.inject.Inject

class ShareTextIntentBuilder @Inject constructor() {

    fun build(
        content: String,
        subject: String? = null,
        chooserTitle: String = content,
    ): Intent? {
        val shareIntent = Intent(Intent.ACTION_SEND).apply {
            type = "text/plain"
            putExtra(Intent.EXTRA_TEXT, content)
            putExtra(Intent.EXTRA_SUBJECT, subject)
        }
        return Intent.createChooser(shareIntent, chooserTitle)
    }
}