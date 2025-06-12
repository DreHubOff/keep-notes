package com.jksol.keep.notes.ui.screens.edit.checklist

import androidx.compose.animation.animateContentSize
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.text.selection.LocalTextSelectionColors
import androidx.compose.foundation.text.selection.TextSelectionColors
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Clear
import androidx.compose.material3.Checkbox
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.scale
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.focus.onFocusChanged
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.input.key.Key
import androidx.compose.ui.input.key.key
import androidx.compose.ui.input.key.onKeyEvent
import androidx.compose.ui.text.TextRange
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.TextFieldValue
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.jksol.keep.notes.ui.focus.ElementFocusRequest
import com.jksol.keep.notes.ui.theme.ApplicationTheme
import com.jksol.keep.notes.ui.theme.themedCheckboxColors
import kotlinx.coroutines.delay

private val textFieldTranslationX = (-10).dp

@Composable
fun EditableChecklistCheckbox(
    modifier: Modifier = Modifier,
    text: String,
    checked: Boolean,
    isDragging: Boolean = false,
    focusRequest: ElementFocusRequest? = null,
    onCheckedChange: (Boolean) -> Unit = {},
    onTextChanged: (String) -> Unit = {},
    onDoneClicked: () -> Unit = {},
    onDeleteClick: () -> Unit = {},
    onItemFocused: () -> Unit = {},
) {
    Row(
        modifier = modifier
            .clickable(enabled = checked) { onCheckedChange(!checked) }
            .animateContentSize(),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        var isTextFieldFocused by remember { mutableStateOf(false) }
        var textFieldValue by remember { mutableStateOf(TextFieldValue(text)) }
        var lastNonEmptyText by remember { mutableStateOf(textFieldValue.text) }
        val focusRequester = remember { FocusRequester() }

        LaunchedEffect(focusRequest, isDragging, isTextFieldFocused) {
            if (isTextFieldFocused) {
                focusRequest?.confirmProcessing()
            }
            if (!isDragging && focusRequest?.isHandled() == false) {
                focusRequester.requestFocus()
                focusRequest.confirmProcessing()
                textFieldValue = textFieldValue.copy(selection = TextRange(textFieldValue.text.length))
            }
        }

        Checkbox(
            modifier = Modifier
                .scale(0.8f)
                .height(36.dp)
                .graphicsLayer { this.translationX = textFieldTranslationX.toPx() },
            checked = checked,
            onCheckedChange = onCheckedChange,
            colors = themedCheckboxColors()
        )

        val textColor = MaterialTheme.colorScheme.run { if (checked) onSurface else onSurfaceVariant }
        val textStyle = TextStyle(
            color = textColor,
            fontSize = 14.sp,
            lineHeight = 18.sp,
            letterSpacing = 0.sp,
            fontWeight = FontWeight.Normal,
            textDecoration = if (checked) TextDecoration.LineThrough else null,
        )

        val transparentTextSelection = rememberDynamicTextSelectionColors(isDragging)

        CompositionLocalProvider(LocalTextSelectionColors provides transparentTextSelection) {
            BasicTextField(
                value = textFieldValue,
                cursorBrush = SolidColor(MaterialTheme.colorScheme.onSurface),
                onValueChange = { newValue ->
                    lastNonEmptyText = textFieldValue.text
                    textFieldValue = newValue
                    onTextChanged(newValue.text)
                },
                modifier = Modifier
                    .weight(1f)
                    .wrapContentHeight()
                    .graphicsLayer { this.translationX = textFieldTranslationX.toPx() }
                    .focusRequester(focusRequester)
                    .onFocusChanged { focusState ->
                        isTextFieldFocused = focusState.isFocused
                        if (isTextFieldFocused) onItemFocused()
                    }
                    .onKeyEvent { event ->
                        if (event.key == Key.Backspace) {
                            if (lastNonEmptyText.isEmpty()) {
                                onDeleteClick()
                                return@onKeyEvent true
                            }
                            if (lastNonEmptyText.isNotEmpty() && textFieldValue.text.isEmpty()) {
                                lastNonEmptyText = ""
                            }
                        }
                        false
                    },
                textStyle = textStyle,
                enabled = !checked,
                keyboardOptions = KeyboardOptions(imeAction = ImeAction.Done),
                keyboardActions = KeyboardActions(onDone = { onDoneClicked() }),
                decorationBox = { innerTextField ->
                    Box(
                        Modifier
                            .fillMaxWidth()
                            .padding(vertical = 4.dp)
                    ) {
                        innerTextField()
                    }
                },
            )
        }

        val endIconModifier = remember { Modifier.size(32.dp) }
        if (isTextFieldFocused) {
            DeleteIcon(modifier = endIconModifier, onDeleteClick = onDeleteClick)
        } else {
            Box(modifier = endIconModifier)
        }
    }
}

@Composable
private fun DeleteIcon(
    modifier: Modifier = Modifier,
    onDeleteClick: () -> Unit,
) {
    IconButton(
        modifier = modifier,
        onClick = onDeleteClick
    ) {
        Icon(
            modifier = Modifier.size(20.dp),
            imageVector = Icons.Default.Clear,
            contentDescription = null,
        )
    }
}

@Composable
private fun rememberDynamicTextSelectionColors(
    isDragging: Boolean,
    primaryColor: Color = MaterialTheme.colorScheme.primary,
): TextSelectionColors {
    var colors by remember {
        mutableStateOf(
            TextSelectionColors(
                handleColor = primaryColor,
                backgroundColor = primaryColor.copy(alpha = 0.4f)
            )
        )
    }

    LaunchedEffect(isDragging) {
        if (isDragging) {
            colors = TextSelectionColors(Color.Transparent, Color.Transparent)
        } else {
            delay(350)
            colors = TextSelectionColors(primaryColor, primaryColor.copy(alpha = 0.4f))
        }
    }

    return colors
}

@Preview(name = "Checked", showBackground = true)
@Composable
private fun Preview() {
    ApplicationTheme {
        EditableChecklistCheckbox(
            text = "This is a text",
            checked = true,
        )
    }
}

@Preview(name = "Not checked", showBackground = true)
@Composable
private fun PreviewNotChecked() {
    ApplicationTheme {
        EditableChecklistCheckbox(
            text = "This is a text",
            checked = false,
        )
    }
}

@Preview(name = "Not checked long", showBackground = true)
@Composable
private fun PreviewNotCheckedLong() {
    ApplicationTheme {
        EditableChecklistCheckbox(
            text = "\uD83D\uDCBB Finish coding the checklist feature, \uD83D\uDCBB Finish coding the checklist feature",
            checked = false,
        )
    }
}

@Preview(name = "Not checked long", showBackground = true)
@Composable
private fun PreviewNotCheckedLongFocused() {
    ApplicationTheme {
        EditableChecklistCheckbox(
            text = "\uD83D\uDCBB Finish coding the checklist feature, \uD83D\uDCBB Finish coding the checklist feature",
            checked = false,
            focusRequest = ElementFocusRequest(),
        )
    }
}

@Preview(name = "Dragged", showBackground = true)
@Composable
private fun PreviewDragged() {
    ApplicationTheme {
        EditableChecklistCheckbox(
            text = "\uD83D\uDCBB Finish coding the checklist feature, \uD83D\uDCBB Finish coding the checklist feature",
            checked = false,
            focusRequest = ElementFocusRequest(),
            isDragging = true
        )
    }
}