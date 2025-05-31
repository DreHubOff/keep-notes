package com.jksol.keep.notes.core.interactor

import android.util.Log
import com.jksol.keep.notes.BuildConfig
import kotlinx.coroutines.flow.firstOrNull
import java.time.Duration
import java.time.OffsetDateTime
import javax.inject.Inject
import kotlin.time.Duration.Companion.seconds
import kotlin.time.toKotlinDuration

class PermanentlyDeleteOldTrashRecordsInteractor @Inject constructor(
    private val observeApplicationMainTypeTrashed: ObserveApplicationMainTypeTrashedInteractor,
    private val permanentlyDeleteApplicationMainDataType: PermanentlyDeleteApplicationMainDataTypeInteractor,
) {

    suspend operator fun invoke() {
        val trashedItems = observeApplicationMainTypeTrashed().firstOrNull().orEmpty()
        Log.d("PermanentlyDeleteInteractor", "Observed ${trashedItems.size} trashed items.")

        val now = OffsetDateTime.now()
        val expirationThreshold = BuildConfig.TRASH_ITEM_MAX_LIFETIME_SECONDS.seconds

        val itemsToRemove = trashedItems.filter { item ->
            val trashedDate = item.trashedDate
            if (trashedDate == null) {
                Log.w("PermanentlyDeleteInteractor", "Skipping item with null trashedDate, id=${item.id}")
                return@filter false
            }
            val trashedDuration = Duration.between(trashedDate, now).toKotlinDuration()
            val expired = trashedDuration > expirationThreshold
            if (expired) {
                Log.d("PermanentlyDeleteInteractor", "Item id=${item.id} expired (trashed $trashedDuration ago)")
            }
            expired
        }

        if (itemsToRemove.isNotEmpty()) {
            Log.d("PermanentlyDeleteInteractor", "Deleting ${itemsToRemove.size} expired trashed items...")
            permanentlyDeleteApplicationMainDataType(*itemsToRemove.toTypedArray())
            Log.d("PermanentlyDeleteInteractor", "Deletion completed.")
        } else {
            Log.d("PermanentlyDeleteInteractor", "No expired trashed items to delete.")
        }
    }

}