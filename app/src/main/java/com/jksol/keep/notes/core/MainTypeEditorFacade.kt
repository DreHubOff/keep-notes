package com.jksol.keep.notes.core

interface MainTypeEditorFacade {

    suspend fun storePinnedSate(pinned: Boolean, itemId: Long)

    suspend fun storeNewTitle(title: String, itemId: Long)

    suspend fun moveToTrash(itemId: Long)

    suspend fun permanentlyDelete(itemId: Long)

    suspend fun restoreItemFromTrash(itemId: Long)
}