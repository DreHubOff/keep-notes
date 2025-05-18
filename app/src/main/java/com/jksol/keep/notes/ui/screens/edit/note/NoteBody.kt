package com.jksol.keep.notes.ui.screens.edit.note

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.jksol.keep.notes.MainScreenDemoData
import com.jksol.keep.notes.R
import com.jksol.keep.notes.ui.theme.ApplicationTheme

@Composable
fun NoteBody(
    modifier: Modifier,
    title: String,
    content: String,
    onTitleChanged: (String) -> Unit = {},
    onContentChanged: (String) -> Unit = {},
) {
    var titleCache by remember { mutableStateOf(title) }
    var contentCache by remember { mutableStateOf(content) }
    Column(modifier = modifier, verticalArrangement = Arrangement.spacedBy(16.dp)) {
        Title(
            title = titleCache,
            onTitleChanged = {
                titleCache = it
                onTitleChanged(it)
            },
            onNextClick = {}
        )
        Content(
            title = contentCache,
            onContentChanged = {
                contentCache = it
                onContentChanged(it)
            },
            onDoneClick = {}
        )
    }
}

@Composable
private fun Title(
    title: String,
    modifier: Modifier = Modifier,
    onTitleChanged: (String) -> Unit = {},
    onNextClick: () -> Unit = {},
) {
    BasicTextField(
        value = title,
        onValueChange = onTitleChanged,
        modifier = modifier
            .fillMaxWidth()
            .wrapContentHeight(),
        textStyle = TextStyle(
            color = MaterialTheme.colorScheme.onSurfaceVariant,
            fontWeight = FontWeight.W600,
            fontSize = 18.sp,
        ),
        keyboardOptions = KeyboardOptions(imeAction = ImeAction.Next),
        keyboardActions = KeyboardActions(onNext = { onNextClick() }),
        decorationBox = { innerTextField ->
            Box(Modifier.fillMaxWidth()) {
                if (title.isEmpty()) {
                    Text(
                        text = stringResource(R.string.title),
                        color = MaterialTheme.colorScheme.onSurface,
                        fontWeight = FontWeight.W600,
                        fontSize = 18.sp
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
    onDoneClick: () -> Unit = {},
) {
    BasicTextField(
        value = title,
        onValueChange = onContentChanged,
        modifier = modifier
            .fillMaxWidth()
            .wrapContentHeight(),
        textStyle = TextStyle(
            color = MaterialTheme.colorScheme.onSurfaceVariant,
            fontSize = 16.sp,
        ),
        keyboardOptions = KeyboardOptions(imeAction = ImeAction.Done),
        keyboardActions = KeyboardActions(onDone = { onDoneClick() }),
        decorationBox = { innerTextField ->
            Box(Modifier.fillMaxWidth()) {
                if (title.isEmpty()) {
                    Text(
                        text = stringResource(R.string.note),
                        color = MaterialTheme.colorScheme.onSurface,
                        fontWeight = FontWeight.W600,
                        fontSize = 16.sp
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
        )
    }
}