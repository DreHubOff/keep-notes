package com.jksol.keep.notes.ui.screens.main.drawer

import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Delete
import androidx.compose.material.icons.outlined.Edit
import androidx.compose.material3.DrawerState
import androidx.compose.material3.DrawerValue
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalDrawerSheet
import androidx.compose.material3.NavigationDrawerItem
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.jksol.keep.notes.R
import com.jksol.keep.notes.ui.theme.ApplicationTheme
import com.jksol.keep.notes.util.getAppVersionName
import kotlinx.coroutines.launch

@Composable
fun MainDrawer(
    drawerState: DrawerState,
    modifier: Modifier = Modifier,
    openTrashClick: () -> Unit = {},
) {
    val coroutineScope = rememberCoroutineScope()
    val context = LocalContext.current
    val versionName = remember { context.getAppVersionName() }
    ModalDrawerSheet(
        modifier = modifier,
        drawerState = drawerState,
    ) {
        Spacer(modifier = Modifier.height(40.dp))
        Text(
            text = stringResource(id = R.string.my_notes_title),
            fontSize = 24.sp,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
            modifier = Modifier.padding(horizontal = 32.dp),
            fontWeight = FontWeight.Normal
        )
        Text(
            text = versionName,
            color = MaterialTheme.colorScheme.onSurface,
            modifier = Modifier.padding(horizontal = 32.dp),
        )
        Spacer(modifier = Modifier.height(26.dp))
        NavigationDrawerItem(
            modifier = Modifier.padding(horizontal = 16.dp),
            label = { Text(text = stringResource(R.string.notes)) },
            icon = { Icon(imageVector = Icons.Outlined.Edit, contentDescription = null) },
            selected = true,
            onClick = {
                coroutineScope.launch {
                    drawerState.close()
                }
            }
        )
        NavigationDrawerItem(
            modifier = Modifier.padding(horizontal = 16.dp),
            label = { Text(text = stringResource(R.string.trash)) },
            icon = { Icon(imageVector = Icons.Outlined.Delete, contentDescription = null) },
            selected = false,
            onClick = {
                coroutineScope.launch {
                    drawerState.close()
                    openTrashClick()
                }
            }
        )
    }
}

@Preview
@Composable
private fun Preview() {
    ApplicationTheme {
        MainDrawer(
            drawerState = DrawerState(DrawerValue.Open),
        )
    }
}