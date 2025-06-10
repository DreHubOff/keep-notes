package com.jksol.keep.notes.ui.screens.main.drawer

import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Edit
import androidx.compose.material.icons.outlined.Share
import androidx.compose.material.icons.outlined.VerifiedUser
import androidx.compose.material.icons.sharp.Translate
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
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
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
    onTrashClick: () -> Unit = {},
    onThemeClick: () -> Unit = {},
    onLanguageClick: () -> Unit = {},
    onHelpClick: () -> Unit = {},
    onShareAppClick: () -> Unit = {},
    onRateUsClick: () -> Unit = {},
    onPrivacyPolicyClick: () -> Unit = {},
) {
    val coroutineScope = rememberCoroutineScope()
    val context = LocalContext.current
    val versionName = remember { context.getAppVersionName() }

    val onItemClicked: (() -> Unit) -> Unit = { listener ->
        coroutineScope.launch {
            drawerState.close()
            listener()
        }
    }

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

        DrawerItem(
            modifier = Modifier.padding(horizontal = 16.dp),
            icon = Icons.Outlined.Edit,
            textRes = R.string.notes,
            selected = true,
            onClick = { onItemClicked {} }
        )

        DrawerItem(
            modifier = Modifier.padding(horizontal = 16.dp),
            icon = R.drawable.ic_delete,
            textRes = R.string.trash,
            onClick = { onItemClicked { onTrashClick() } }
        )

        DrawerItem(
            modifier = Modifier.padding(horizontal = 16.dp),
            icon = R.drawable.ic_dark_mode,
            textRes = R.string.theme,
            onClick = { onItemClicked { onThemeClick() } }
        )

        DrawerItem(
            modifier = Modifier.padding(horizontal = 16.dp),
            icon = Icons.Sharp.Translate,
            textRes = R.string.language,
            onClick = { onItemClicked { onLanguageClick() } }
        )

        DrawerItem(
            modifier = Modifier.padding(horizontal = 16.dp),
            icon = R.drawable.ic_help,
            textRes = R.string.help_feedback_menu,
            onClick = { onItemClicked { onHelpClick() } }
        )

        DrawerItem(
            modifier = Modifier.padding(horizontal = 16.dp),
            icon = Icons.Outlined.Share,
            textRes = R.string.share_app,
            onClick = { onItemClicked { onShareAppClick() } }
        )

        DrawerItem(
            modifier = Modifier.padding(horizontal = 16.dp),
            icon = R.drawable.ic_thumb_up,
            textRes = R.string.rate_us,
            onClick = { onItemClicked { onRateUsClick() } }
        )

        DrawerItem(
            modifier = Modifier.padding(horizontal = 16.dp),
            icon = Icons.Outlined.VerifiedUser,
            textRes = R.string.privacy_policy,
            onClick = { onItemClicked { onPrivacyPolicyClick() } }
        )
    }
}

@Composable
private fun DrawerItem(
    modifier: Modifier = Modifier.padding(horizontal = 16.dp),
    textRes: Int,
    selected: Boolean = false,
    icon: ImageVector,
    onClick: () -> Unit,
) {
    DrawerItem(
        modifier = modifier,
        textRes = textRes,
        selected = selected,
        icon = {
            Icon(imageVector = icon, contentDescription = null)
        },
        onClick = onClick
    )
}

@Composable
private fun DrawerItem(
    modifier: Modifier = Modifier,
    textRes: Int,
    selected: Boolean = false,
    icon: Int,
    onClick: () -> Unit,
) {
    DrawerItem(
        modifier = modifier,
        textRes = textRes,
        selected = selected,
        icon = {
            Icon(painter = painterResource(icon), contentDescription = null)
        },
        onClick = onClick
    )
}

@Composable
private fun DrawerItem(
    modifier: Modifier = Modifier,
    textRes: Int,
    selected: Boolean = false,
    icon: @Composable () -> Unit,
    onClick: () -> Unit,
) {
    NavigationDrawerItem(
        modifier = modifier,
        label = {
            Text(
                text = stringResource(textRes),
                fontWeight = FontWeight.Medium,
            )
        },
        icon = icon,
        selected = selected,
        onClick = onClick,
    )
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