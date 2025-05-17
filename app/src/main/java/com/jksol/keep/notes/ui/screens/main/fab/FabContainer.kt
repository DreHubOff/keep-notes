package com.jksol.keep.notes.ui.screens.main.fab

import androidx.annotation.StringRes
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.layout.Arrangement.Absolute.spacedBy
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.CheckBox
import androidx.compose.material.icons.sharp.ModeEdit
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import com.jksol.keep.notes.R
import com.jksol.keep.notes.ui.theme.ApplicationTheme

private data class SecondaryFAB(
    val icon: ImageVector,
    @StringRes val text: Int,
    @StringRes val description: Int,
    val clickAction: () -> Unit = {},
)

@Composable
fun MainFabContainer(
    expanded: Boolean,
    onAddTextNoteClick: () -> Unit = {},
    onAddChecklistClick: () -> Unit = {},
    onMainFabClicked: () -> Unit = {},
) {
    val secondaryFabs = listOf(
        SecondaryFAB(
            icon = Icons.Rounded.CheckBox,
            text = R.string.fab_add_checklist,
            description = R.string.fab_add_checklist_desc,
            clickAction = onAddChecklistClick,
        ),
        SecondaryFAB(
            icon = Icons.Sharp.ModeEdit,
            text = R.string.fab_add_note,
            description = R.string.fab_add_text_note_desc,
            clickAction = onAddTextNoteClick,
        ),
    )

    Box(modifier = Modifier.fillMaxSize()) {
        Column(
            modifier = Modifier
                .align(Alignment.BottomEnd)
                .padding(16.dp),
            horizontalAlignment = Alignment.End,
            verticalArrangement = spacedBy(16.dp),
        ) {
            secondaryFabs.forEachIndexed { index, (icon, text, description, action) ->
                AnimatedSecondaryFab(
                    visible = expanded,
                    icon = icon,
                    text = text,
                    description = description,
                    index = index,
                    onClick = {
                        action()
                    }
                )
            }

            MainFab(
                clicked = expanded,
                onClick = { onMainFabClicked() },
            )
        }
    }
}

@Composable
private fun AnimatedSecondaryFab(
    visible: Boolean,
    icon: ImageVector,
    @StringRes text: Int,
    @StringRes description: Int,
    onClick: () -> Unit,
    index: Int,
    startTranslationY: Dp = 180.dp,
    startTranslationX: Dp = 90.dp,
) {
    val animDuration = 250 - index * 20

    val scale by animateFloatAsState(
        targetValue = if (visible) 1f else 0f,
        animationSpec = tween(durationMillis = animDuration),
        label = "ScaleAnim"
    )

    val alpha by animateFloatAsState(
        targetValue = if (visible) 1f else 0f,
        animationSpec = tween(durationMillis = animDuration),
        label = "AlphaAnim"
    )

    val translationY by animateFloatAsState(
        targetValue = if (visible) 0f else startTranslationY.value,
        animationSpec = tween(durationMillis = animDuration),
        label = "TranslationYAnim"
    )

    val translationX by animateFloatAsState(
        targetValue = if (visible) 0f else startTranslationX.value,
        animationSpec = tween(durationMillis = animDuration),
        label = "TranslationYAnim"
    )

    if (scale > 0f || visible) {
        SecondaryFAB(
            icon = icon,
            text = text,
            description = description,
            onClick = onClick,
            modifier = Modifier
                .graphicsLayer {
                    this.scaleX = scale
                    this.scaleY = scale
                    this.alpha = alpha
                    this.translationY = translationY
                    this.translationX = translationX
                }
        )
    }
}


@Preview(showSystemUi = true, showBackground = true)
@Composable
private fun Preview() {
    ApplicationTheme {
        MainFabContainer(true)
    }
}