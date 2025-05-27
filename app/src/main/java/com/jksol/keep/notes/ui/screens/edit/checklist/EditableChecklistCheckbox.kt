package com.jksol.keep.notes.ui.screens.edit.checklist

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.animateContentSize
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.RowScope
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Clear
import androidx.compose.material3.Checkbox
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
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
import androidx.compose.ui.graphics.graphicsLayer
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

@Composable
fun EditableChecklistCheckbox(
    modifier: Modifier = Modifier,
    text: String,
    checked: Boolean,
    isDragged: Boolean = false,
    focusRequest: ElementFocusRequest? = null,
    onCheckedChange: (Boolean) -> Unit = {},
    onTextChanged: (String) -> Unit = {},
    onDoneClicked: () -> Unit = {},
    onFocusStateChanged: (Boolean) -> Unit = {},
    onDeleteClick: () -> Unit = {},
) {
    Row(
        modifier = modifier
            .clickable(enabled = checked) { onCheckedChange(!checked) }
            .animateContentSize(),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        val focusRequester = remember { FocusRequester() }
        var isFocused by remember { mutableStateOf(false) }
        var textField by remember { mutableStateOf(TextFieldValue(text)) }

        if (!isFocused && focusRequest?.isHandled() == false) {
            textField = TextFieldValue(text, selection = TextRange(textField.text.length))
        }

        LaunchedEffect(focusRequest, isDragged, isFocused) {
            if (isFocused) {
                focusRequest?.confirmProcessing()
            }
            if (!isDragged && focusRequest?.isHandled() == false) {
                focusRequester.requestFocus()
                focusRequest.confirmProcessing()
            }
        }

        val translationX = -40.dp.value
        Checkbox(
            modifier = Modifier
                .scale(0.8f)
                .height(36.dp)
                .graphicsLayer { this.translationX = translationX },
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
            textDecoration = if (checked) TextDecoration.LineThrough else null
        )

        if (isDragged) {
            OnDragOverlay(text = textField.text, textStyle = textStyle, translationX = translationX)
        } else {
            BasicTextField(
                value = textField,
                onValueChange = { newValue ->
                    textField = newValue
                    onTextChanged(newValue.text)
                },
                modifier = Modifier
                    .weight(1f)
                    .wrapContentHeight()
                    .graphicsLayer { this.translationX = translationX }
                    .focusRequester(focusRequester)
                    .onFocusChanged { focusState ->
                        isFocused = focusState.isFocused
                        onFocusStateChanged(focusState.isFocused)
                    },
                textStyle = textStyle,
                enabled = !checked,
                keyboardOptions = KeyboardOptions(imeAction = ImeAction.Done),
                keyboardActions = KeyboardActions(onDone = { onDoneClicked() }),
                decorationBox = { innerTextField ->
                    Box(
                        Modifier
                            .fillMaxWidth()
                            .padding(vertical = 2.dp)
                    ) {
                        innerTextField()
                    }
                }
            )
        }

        AnimatedVisibility(visible = focusRequest != null) { DeleteIcon(onDeleteClick) }
    }
}

@Composable
private fun DeleteIcon(onDeleteClick: () -> Unit) {
    IconButton(
        modifier = Modifier.size(32.dp),
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
private fun RowScope.OnDragOverlay(
    text: String,
    textStyle: TextStyle,
    translationX: Float,
) {
    Text(
        text = text,
        color = textStyle.color,
        fontWeight = textStyle.fontWeight,
        fontSize = textStyle.fontSize,
        lineHeight = textStyle.lineHeight,
        letterSpacing = textStyle.letterSpacing,
        textDecoration = textStyle.textDecoration,
        modifier = Modifier
            .weight(1f)
            .wrapContentHeight()
            .graphicsLayer { this.translationX = translationX }
    )
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
            isDragged = true
        )
    }
}