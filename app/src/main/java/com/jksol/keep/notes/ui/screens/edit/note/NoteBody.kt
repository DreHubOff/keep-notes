@file:OptIn(ExperimentalSharedTransitionApi::class)

package com.jksol.keep.notes.ui.screens.edit.note

import androidx.compose.animation.ExperimentalSharedTransitionApi
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.imePadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.relocation.BringIntoViewRequester
import androidx.compose.foundation.relocation.bringIntoViewRequester
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.focus.onFocusChanged
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.TextRange
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.TextFieldValue
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.jksol.keep.notes.R
import com.jksol.keep.notes.demo_data.MainScreenDemoData
import com.jksol.keep.notes.ui.focus.ElementFocusRequest
import com.jksol.keep.notes.ui.screens.edit.core.ReminderButton
import com.jksol.keep.notes.ui.screens.edit.core.ReminderStateData
import com.jksol.keep.notes.ui.shared.sharedElementTransition
import com.jksol.keep.notes.ui.theme.ApplicationTheme
import com.jksol.keep.notes.ui.theme.plusJakartaSans
import com.jksol.keep.notes.util.asStrikethroughText
import kotlinx.coroutines.launch
import java.time.OffsetDateTime

@Composable
fun NoteBody(
    modifier: Modifier,
    title: String,
    reminderData: ReminderStateData?,
    content: String,
    titleTransitionKey: Any = Unit,
    contentFocusRequest: ElementFocusRequest? = null,
    onTitleChanged: (String) -> Unit = {},
    onContentChanged: (String) -> Unit = {},
    onTitleNextClick: () -> Unit = {},
    onEditReminderClick: () -> Unit = {},
) {
    val bringIntoViewRequester = remember { BringIntoViewRequester() }
    val lazyListState = rememberLazyListState()

    LazyColumn(
        state = lazyListState,
        modifier = Modifier
            .fillMaxSize()
            .padding(top = 16.dp, start = 16.dp, end = 16.dp)
            .imePadding()
            .then(modifier),
        verticalArrangement = Arrangement.spacedBy(16.dp),
        contentPadding = PaddingValues(bottom = 60.dp)
    ) {
        item(key = "Title") {
            Title(
                modifier = Modifier.sharedElementTransition(titleTransitionKey),
                title = title,
                onTitleChanged = onTitleChanged,
                onNextClick = onTitleNextClick,
            )
        }
        if (reminderData != null) {
            item(key = "Reminder") {
                ReminderButton(
                    modifier = Modifier,
                    reminderData = reminderData,
                    onClick = onEditReminderClick,
                )
            }
        }
        item(key = "Content") {
            Content(
                modifier = Modifier,
                title = content,
                onContentChanged = onContentChanged,
                contentFocusRequest = contentFocusRequest,
                bringIntoViewRequester = bringIntoViewRequester,
            )
        }
    }
}

@Composable
private fun Title(
    title: String,
    modifier: Modifier = Modifier,
    onTitleChanged: (String) -> Unit = {},
    onNextClick: () -> Unit = {},
) {
    var titleCache by remember { mutableStateOf(TextFieldValue(title)) }
    val textSize = 22.sp
    BasicTextField(
        value = titleCache,
        onValueChange = { newTextFieldValue ->
            titleCache = newTextFieldValue
            onTitleChanged(newTextFieldValue.text)
        },
        cursorBrush = SolidColor(MaterialTheme.colorScheme.onSurface),
        modifier = modifier
            .fillMaxWidth()
            .wrapContentHeight(),
        textStyle = TextStyle(
            color = MaterialTheme.colorScheme.onSurfaceVariant,
            fontWeight = FontWeight.W600,
            fontSize = textSize,
            fontFamily = plusJakartaSans,
        ),
        keyboardOptions = KeyboardOptions(imeAction = ImeAction.Next),
        keyboardActions = KeyboardActions(onNext = { onNextClick() }),
        decorationBox = { innerTextField ->
            Box(Modifier.fillMaxWidth()) {
                if (titleCache.text.isEmpty()) {
                    Text(
                        text = stringResource(R.string.title),
                        color = MaterialTheme.colorScheme.onSurface,
                        fontWeight = FontWeight.W600,
                        fontSize = textSize,
                        fontFamily = plusJakartaSans,
                    )
                }
                innerTextField()
            }
        }
    )
}

@Composable
private fun Content(
    title: String,
    modifier: Modifier = Modifier,
    onContentChanged: (String) -> Unit = {},
    contentFocusRequest: ElementFocusRequest?,
    bringIntoViewRequester: BringIntoViewRequester,
) {

    val textSize = 17.sp

    val focusRequester = remember { FocusRequester() }
    var isTextFieldFocused by remember { mutableStateOf(false) }
    var textFieldValue by remember { mutableStateOf(TextFieldValue(title)) }
    val coroutineScope = rememberCoroutineScope()

    LaunchedEffect(contentFocusRequest) {
        if (contentFocusRequest == null) return@LaunchedEffect
        if (!contentFocusRequest.isHandled()) {
            focusRequester.requestFocus()
            contentFocusRequest.confirmProcessing()
            textFieldValue = textFieldValue.copy(selection = TextRange(textFieldValue.text.length))
        }
    }

    BasicTextField(
        value = textFieldValue,
        cursorBrush = SolidColor(MaterialTheme.colorScheme.onSurface),
        onValueChange = { newContent ->
            textFieldValue = newContent
            onContentChanged(newContent.text)
        },
        modifier = modifier
            .fillMaxWidth()
            .wrapContentHeight()
            .focusRequester(focusRequester)
            .bringIntoViewRequester(bringIntoViewRequester)
            .imePadding()
            .onFocusChanged {
                if (it.isFocused) {
                    coroutineScope.launch {
                        bringIntoViewRequester.bringIntoView()
                    }
                }
                isTextFieldFocused = it.isFocused
            },
        onTextLayout = {
            val cursorRect = it.getCursorRect(textFieldValue.selection.start)
            coroutineScope.launch {
                bringIntoViewRequester.bringIntoView(cursorRect)
            }
        },
        textStyle = TextStyle(
            color = MaterialTheme.colorScheme.onSurfaceVariant,
            fontSize = textSize,
            fontFamily = plusJakartaSans,
        ),
        decorationBox = { innerTextField ->
            Box(Modifier.fillMaxWidth()) {
                if (textFieldValue.text.isEmpty()) {
                    Text(
                        text = stringResource(R.string.note),
                        color = MaterialTheme.colorScheme.onSurface,
                        fontSize = textSize,
                        fontFamily = plusJakartaSans,
                    )
                }
                innerTextField()
            }
        }
    )
}

@Preview(showBackground = true)
@Composable
private fun Preview() {
    ApplicationTheme {
        NoteBody(
            modifier = Modifier.fillMaxWidth(),
            title = MainScreenDemoData.TextNotes.welcomeBanner.title,
            content = MainScreenDemoData.TextNotes.welcomeBanner.content,
            reminderData = ReminderStateData(
                sourceDate = OffsetDateTime.now(),
                dateString = AnnotatedString("21 May, 10:12 AM"),
                outdated = false,
                reminderColorDay = Color(0x14017FFA)
            ),
        )
    }
}

@Preview(showBackground = true)
@Composable
private fun PreviewOutdatedReminder() {
    ApplicationTheme {
        NoteBody(
            modifier = Modifier.fillMaxWidth(),
            title = MainScreenDemoData.TextNotes.welcomeBanner.title,
            content = MainScreenDemoData.TextNotes.welcomeBanner.content,
            reminderData = ReminderStateData(
                sourceDate = OffsetDateTime.now(),
                dateString = "21 May, 10:12 AM".asStrikethroughText(),
                outdated = true,
                reminderColorDay = Color(0x14017FFA)
            ),
        )
    }
}

@Preview(showBackground = true)
@Composable
private fun PreviewEmpty() {
    ApplicationTheme {
        NoteBody(
            modifier = Modifier.fillMaxWidth(),
            title = "",
            content = "",
            reminderData = ReminderStateData(
                sourceDate = OffsetDateTime.now(),
                dateString = "21 May, 10:12 AM".asStrikethroughText(),
                outdated = true,
                reminderColorDay = Color(0x14017FFA)
            ),
        )
    }
}