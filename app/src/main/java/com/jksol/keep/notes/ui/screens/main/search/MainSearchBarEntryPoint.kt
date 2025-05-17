package com.jksol.keep.notes.ui.screens.main.search

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.sharp.Menu
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonColors
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.jksol.keep.notes.R
import com.jksol.keep.notes.ui.theme.ApplicationTheme

@Composable
fun MainSearchBarEntryPoint(
    modifier: Modifier = Modifier,
    innerPadding: PaddingValues,
    onClick: () -> Unit = {},
) {
    Box(
        modifier = modifier
            .fillMaxWidth()
            .wrapContentHeight()
            .padding(top = innerPadding.calculateTopPadding())
            .padding(
                top = SearchBarDefaults.searchButtonExtraPaddingTop,
                start = SearchBarDefaults.searchButtonHorizontalPadding,
                end = SearchBarDefaults.searchButtonHorizontalPadding,
            ),
    ) {
        Button(
            modifier = Modifier
                .height(46.dp)
                .fillMaxWidth(),
            onClick = onClick,
            elevation = null,
            colors = ButtonColors(
                containerColor = SearchBarDefaults.searchBackgroundColor(),
                contentColor = SearchBarDefaults.searchContentColor(),
                disabledContainerColor = Color.Transparent,
                disabledContentColor = Color.Transparent,
            ),
            shape = RoundedCornerShape(SearchBarDefaults.searchButtonCornerRadius)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically,
            ) {
                Icon(Icons.Sharp.Menu, contentDescription = null)
                Spacer(Modifier.width(14.dp))
                Text(
                    text = stringResource(id = R.string.search_notes),
                    color = MaterialTheme.colorScheme.onSurface,
                    fontStyle = FontStyle.Normal
                )
            }
        }
    }
}

@Preview
@Composable
private fun Preview() {
    ApplicationTheme {
        MainSearchBarEntryPoint(innerPadding = PaddingValues(10.dp))
    }
}