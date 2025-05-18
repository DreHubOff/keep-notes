package com.jksol.keep.notes.ui.screens.edit.note

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.sp
import com.jksol.keep.notes.MainScreenDemoData
import com.jksol.keep.notes.R
import com.jksol.keep.notes.ui.theme.ApplicationTheme

@Composable
fun NoteBody(
    modifier: Modifier,
    title: String,
    content: String,
) {
    Column(modifier = modifier) {
        Title(title = title, onTitleChanged = {}, onNextClick = {})
        Content(title = content, onContentChanged = {}, onDoneClick = {})
    }
}

@Composable
private fun Title(
    title: String,
    modifier: Modifier = Modifier,
    onTitleChanged: (String) -> Unit = {},
    onNextClick: () -> Unit = {},
) {
    TextField(
        value = title,
        onValueChange = onTitleChanged,
        placeholder = {
            Text(stringResource(R.string.title), color = MaterialTheme.colorScheme.onSurface)
        },
        modifier = modifier
            .fillMaxWidth()
            .background(Color.Transparent),
        keyboardOptions = KeyboardOptions(imeAction = ImeAction.Next),
        keyboardActions = KeyboardActions(onNext = { onNextClick() }),
        textStyle = TextStyle(
            fontWeight = FontWeight.W600,
            fontSize = 18.sp,
        ),
        colors = TextFieldDefaults.colors(
            focusedContainerColor = Color.Transparent,
            unfocusedContainerColor = Color.Transparent,
            focusedIndicatorColor = Color.Transparent,
            unfocusedIndicatorColor = Color.Transparent,
            focusedTextColor = MaterialTheme.colorScheme.onSurfaceVariant,
            unfocusedTextColor = MaterialTheme.colorScheme.onSurface,
            cursorColor = MaterialTheme.colorScheme.onSurface,
        )
    )
}

@Composable
private fun Content(
    title: String,
    modifier: Modifier = Modifier,
    onContentChanged: (String) -> Unit = {},
    onDoneClick: () -> Unit = {},
) {
    TextField(
        value = title,
        onValueChange = onContentChanged,
        placeholder = {
            Text(stringResource(R.string.note), color = MaterialTheme.colorScheme.onSurface)
        },
        modifier = modifier
            .fillMaxWidth()
            .background(Color.Transparent),
        keyboardOptions = KeyboardOptions(imeAction = ImeAction.Done),
        keyboardActions = KeyboardActions(onDone = { onDoneClick() }),
        colors = TextFieldDefaults.colors(
            focusedContainerColor = Color.Transparent,
            unfocusedContainerColor = Color.Transparent,
            focusedIndicatorColor = Color.Transparent,
            unfocusedIndicatorColor = Color.Transparent,
            focusedTextColor = MaterialTheme.colorScheme.onSurfaceVariant,
            unfocusedTextColor = MaterialTheme.colorScheme.onSurface,
            cursorColor = MaterialTheme.colorScheme.onSurface,
        )
    )
}

@Preview
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