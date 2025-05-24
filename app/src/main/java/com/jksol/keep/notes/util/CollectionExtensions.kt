package com.jksol.keep.notes.util

fun <T> MutableList<T>.moveItem(fromIndex: Int, toIndex: Int) {
    if (fromIndex == toIndex) return
    if (fromIndex !in indices || toIndex !in 0..size) return

    val item = removeAt(fromIndex)
    add(toIndex, item)
}