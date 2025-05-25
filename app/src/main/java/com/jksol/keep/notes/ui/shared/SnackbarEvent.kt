package com.jksol.keep.notes.ui.shared

class SnackbarEvent(
    message: String,
    val action: Action? = null,
) {

    private var _message: String? = message

    fun consume(): String? {
        val message = _message
        _message = null
        return message
    }

    class Action(val label: String, val key: Any)
}