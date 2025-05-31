package com.jksol.keep.notes.util

import androidx.lifecycle.SavedStateHandle

fun <T> SavedStateHandle.getAndRemove(key: String): T? {
    val value = get<T>(key)
    remove<T>(key)
    return value
}