package com.jksol.keep.notes.ui.focus

class ElementFocusRequest {

    private var isProcessed: Boolean = false

    fun isHandled(): Boolean = isProcessed
    fun confirmProcessing() {
        isProcessed = true
    }
}