package com.jksol.keep.notes.ui.screens.edit

import androidx.annotation.DrawableRes
import androidx.annotation.StringRes
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonColors
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.jksol.keep.notes.R
import com.jksol.keep.notes.ui.shared.AppAlertDialog
import com.jksol.keep.notes.ui.theme.ApplicationTheme
import com.jksol.keep.notes.ui.theme.plusJakartaSans

enum class ShareContentType(
    @DrawableRes
    val iconRes: Int,
    @StringRes
    val messageRes: Int,
) {
    AS_TEXT(iconRes = R.drawable.ic_format_txt, messageRes = R.string.share_as_text),
    AS_PDF(iconRes = R.drawable.ic_format_pdf, messageRes = R.string.share_as_pdf),
}

private val buttonContentPadding = PaddingValues(
    horizontal = 0.dp,
    vertical = 8.dp
)

@Composable
private fun buttonColors() = ButtonColors(
    containerColor = Color.Transparent,
    contentColor = MaterialTheme.colorScheme.onSurfaceVariant,
    disabledContainerColor = MaterialTheme.colorScheme.onSurface,
    disabledContentColor = MaterialTheme.colorScheme.onSurface,
)

@Composable
fun ShareTypeSelectionDialog(
    title: String,
    onDismiss: () -> Unit = {},
    onTypeSelected: (ShareContentType) -> Unit = {},
    shareTypes: List<ShareContentType> = ShareContentType.entries,
) {
    AppAlertDialog(
        dismissAction = onDismiss,
        title = {
            Text(
                modifier = Modifier.padding(bottom = 18.dp),
                text = title,
                fontSize = 20.sp,
                fontWeight = FontWeight.SemiBold,
                fontFamily = plusJakartaSans,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
            )
        },
        text = {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(bottom = 6.dp),
            ) {
                shareTypes.forEach { shareType ->
                    Button(
                        onClick = { onTypeSelected(shareType) },
                        contentPadding = buttonContentPadding,
                        colors = buttonColors(),
                    ) {
                        Icon(
                            modifier = Modifier.size(24.dp),
                            painter = painterResource(shareType.iconRes),
                            contentDescription = null,
                        )
                        Text(
                            modifier = Modifier.padding(horizontal = 14.dp),
                            text = stringResource(shareType.messageRes),
                            style = TextStyle(
                                fontWeight = FontWeight.SemiBold,
                                letterSpacing = 0.5.sp,
                                fontFamily = plusJakartaSans,
                                fontSize = 18.sp,
                            )
                        )
                    }
                }
            }
        }
    )
}

@Preview
@Composable
private fun Preview() {
    ApplicationTheme {
        ShareTypeSelectionDialog("Share Note")
    }
}