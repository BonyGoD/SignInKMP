package dev.bonygod.signin.kmp.ui

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.painter.Painter
import dev.bonygod.signin.kmp.core.AppleAuthHelper
import dev.bonygod.signin.kmp.ui.components.AppleButton

@Composable
actual fun AppleSignin(
    modifier: Modifier,
    text: String,
    textColor: Color,
    icon: Painter?,
    onSuccess: (displayName: String, uid: String, email: String, photoUrl: String) -> Unit,
    onError: (errorMessage: String) -> Unit
) {
    val appleAuthHelper = AppleAuthHelper()

    AppleButton(
        appleAuthHelper = appleAuthHelper,
        modifier = modifier,
        text = text,
        textColor = textColor,
        icon = icon,
        onSuccess = onSuccess,
        onError = onError
    )
}

