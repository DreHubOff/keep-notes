package com.jksol.keep.notes.ui.shared

import androidx.compose.animation.core.animateDp
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.tween
import androidx.compose.animation.core.updateTransition
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.jksol.keep.notes.R
import kotlinx.coroutines.delay

@Composable
fun PinCheckbox(
    modifier: Modifier = Modifier,
    isChecked: Boolean,
    onCheckedChange: (Boolean) -> Unit,
    contentDescription: String,
    iconTint: Color = MaterialTheme.colorScheme.onSurfaceVariant,
) {
    var triggerChange by remember { mutableStateOf(false) }
    val transition = updateTransition(targetState = triggerChange, label = "PinCheckboxTransition")

    val offsetY by transition.animateDp(
        transitionSpec = { tween(durationMillis = 150) },
        label = "OffsetY"
    ) { state -> if (state) (-12).dp else 0.dp }

    val rotation by transition.animateFloat(
        transitionSpec = { tween(durationMillis = 150) },
        label = "Rotation"
    ) { state -> if (state) (if (isChecked) -20f else 20f) else 0f }

    val scale by transition.animateFloat(
        transitionSpec = { tween(durationMillis = 150) },
        label = "Scale"
    ) { state -> if (state) 0.8f else 1f }

    LaunchedEffect(triggerChange) {
        if (triggerChange) {
            delay(150)
            onCheckedChange(!isChecked)
            triggerChange = false
        }
    }

    IconButton(
        onClick = {
            triggerChange = true
        },
        modifier = modifier
    ) {
        val iconRes = if (isChecked) R.drawable.ic_material_keep_filled else R.drawable.ic_material_keep
        Icon(
            painter = painterResource(id = iconRes),
            contentDescription = contentDescription,
            modifier = Modifier.graphicsLayer {
                translationY = offsetY.toPx()
                rotationZ = rotation
                scaleY = scale
            },
            tint = iconTint
        )
    }
}

@Preview(showBackground = true)
@Composable
private fun PinCheckboxPreview() {
    var isChecked by remember { mutableStateOf(true) }

    Surface(modifier = Modifier.fillMaxSize(), color = MaterialTheme.colorScheme.background) {
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(32.dp),
            contentAlignment = Alignment.Center
        ) {
            PinCheckbox(
                isChecked = isChecked,
                onCheckedChange = { isChecked = it },
                contentDescription = "Toggle pin",
                iconTint = MaterialTheme.colorScheme.onSurfaceVariant,
            )
        }
    }
}