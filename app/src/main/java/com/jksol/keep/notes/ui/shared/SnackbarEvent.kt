package com.jksol.keep.notes.ui.shared

class SnackbarEvent(message: String) {
    private var _message: String? = message
    fun consume(): String? {
        val message = _message
        _message = null
        return message
    }
}