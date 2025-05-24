package com.jksol.keep.notes.ui.screens.edit.checklist

import androidx.compose.animation.AnimatedVisibility
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
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Clear
import androidx.compose.material3.Checkbox
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
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
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.jksol.keep.notes.ui.focus.ElementFocusRequest
import com.jksol.keep.notes.ui.theme.ApplicationTheme
import com.jksol.keep.notes.ui.theme.themedCheckboxColors

@Composable
fun EditableChecklistCheckbox(
    modifier: Modifier = Modifier,
    text: String,
    checked: Boolean,
    focusRequest: ElementFocusRequest? = null,
    onCheckedChange: (Boolean) -> Unit = {},
    onTextChanged: (String) -> Unit = {},
    onDoneClicked: () -> Unit = {},
    onFocusStateChanged: (Boolean) -> Unit = {},
    onDeleteClick: () -> Unit = {},
) {
    var isFocused by remember { mutableStateOf(false) }

    Row(
        modifier = modifier
            .clickable(enabled = checked) { onCheckedChange(!checked) }
            .animateContentSize(),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        val focusRequester = remember { FocusRequester() }

        LaunchedEffect(focusRequest) {
            if (focusRequest?.isHandled() == false) {
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
        var textCache by remember(text) { mutableStateOf(text) }
        BasicTextField(
            value = textCache,
            onValueChange = {
                textCache = it
                onTextChanged(it)
            },
            modifier = Modifier
                .weight(1f)
                .wrapContentHeight()
                .graphicsLayer { this.translationX = translationX }
                .focusRequester(focusRequester)
                .onFocusChanged {
                    isFocused = it.isFocused
                    onFocusStateChanged(it.isFocused)
                },
            textStyle = TextStyle(
                color = textColor,
                fontWeight = FontWeight.Normal,
                textDecoration = if (checked) TextDecoration.LineThrough else null
            ),
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
        AnimatedVisibility(visible = isFocused) {
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
    }
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