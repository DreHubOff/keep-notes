@file:OptIn(ExperimentalSharedTransitionApi::class)

package com.jksol.keep.notes.ui.screens.main.search

import androidx.compose.animation.ExperimentalSharedTransitionApi
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.sharp.Menu
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.jksol.keep.notes.R
import com.jksol.keep.notes.ui.shared.sharedBoundsTransition
import com.jksol.keep.notes.ui.theme.ApplicationTheme
import com.jksol.keep.notes.ui.theme.plusJakartaSans

@Composable
fun MainSearchBarEntryPoint(
    modifier: Modifier = Modifier,
    innerPadding: PaddingValues,
    onSearchClick: () -> Unit = {},
    onOpenMenuClick: () -> Unit = {},
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
        Box(
            modifier = Modifier
                .height(46.dp)
                .fillMaxWidth()
                .clip(RoundedCornerShape(SearchBarDefaults.searchButtonCornerRadius))
                .background(SearchBarDefaults.searchBackgroundColor())
                .padding(horizontal = 8.dp)
                .sharedBoundsTransition("ActionBar"),
            contentAlignment = Alignment.CenterStart
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                IconButton(
                    onClick = onOpenMenuClick,
                    modifier = Modifier.size(40.dp)
                ) {
                    Icon(
                        imageVector = Icons.Sharp.Menu,
                        contentDescription = stringResource(R.string.menu_desc),
                        tint = SearchBarDefaults.searchContentColor()
                    )
                }
                TextButton(
                    onClick = onSearchClick,
                    modifier = Modifier.weight(1f),
                    contentPadding = PaddingValues(start = 0.dp),
                ) {
                    Text(
                        modifier = Modifier.fillMaxWidth(),
                        text = stringResource(id = R.string.search_notes),
                        color = MaterialTheme.colorScheme.onSurface,
                        fontSize = 16.sp,
                        fontWeight = FontWeight.Medium,
                        fontFamily = plusJakartaSans,
                    )
                }
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