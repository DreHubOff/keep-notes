package com.jksol.keep.notes.core.interactor

import android.content.Context
import com.jksol.keep.notes.R
import dagger.hilt.android.qualifiers.ApplicationContext
import java.time.Duration
import java.time.OffsetDateTime
import java.time.format.DateTimeFormatter
import java.util.Locale
import javax.inject.Inject

class BuildModificationDateTextInteractor @Inject constructor(
    @ApplicationContext private val context: Context,
) {
    private val timeFormat by lazy { DateTimeFormatter.ofPattern("h:mm a", Locale.getDefault()) }
    private val dateShortFormat by lazy { DateTimeFormatter.ofPattern("MMM d", Locale.getDefault()) }
    private val dateLongFormat by lazy { DateTimeFormatter.ofPattern("MMM d, yyyy", Locale.getDefault()) }

    operator fun invoke(modificationDate: OffsetDateTime): String {
        val currentTime = OffsetDateTime.now()
        val duration = Duration.between(modificationDate, currentTime)

        return when {
            modificationDate.year != currentTime.year -> {
                val time = modificationDate.toLocalDate().format(dateLongFormat)
                context.getString(R.string.edited_pattern).format(time).lowercase()
            }

            modificationDate.dayOfYear != currentTime.dayOfYear -> {
                val time = modificationDate.toLocalDate().format(dateShortFormat)
                context.getString(R.string.edited_pattern).format(time)
            }

            duration.toMinutes() >= 1L -> {
                val time = modificationDate.toLocalTime().format(timeFormat)
                context.getString(R.string.edited_pattern).format(time).lowercase()
            }

            else -> context.getString(R.string.edited_just_now)
        }
    }
}