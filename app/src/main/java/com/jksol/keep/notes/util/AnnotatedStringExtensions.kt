package com.jksol.keep.notes.util

import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.style.TextDecoration

fun String.asStrikethroughText(fromIndex: Int = 0, toIndex: Int = this.length): AnnotatedString {
    return buildAnnotatedString {
        append(this@asStrikethroughText, 0, fromIndex)

        pushStyle(SpanStyle(textDecoration = TextDecoration.LineThrough))
        append(this@asStrikethroughText, fromIndex, toIndex)
        pop()

        append(this@asStrikethroughText, toIndex, this@asStrikethroughText.length)
    }
}