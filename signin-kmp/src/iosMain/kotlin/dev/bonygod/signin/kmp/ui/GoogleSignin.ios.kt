package dev.bonygod.signin.kmp.ui

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.painter.Painter
import dev.bonygod.signin.kmp.core.GoogleAuthHelper
import dev.bonygod.signin.kmp.ui.components.GoogleButton

@Composable
actual fun GoogleSignin(
    modifier: Modifier,
    text: String,
    textColor: Color,
    icon: Painter?,
    onSuccess: (displayName: String, uid: String, email: String, photoUrl: String) -> Unit,
    onError: (errorMessage: String) -> Unit
) {
    val googleAuthHelper = GoogleAuthHelper()

    GoogleButton(
        googleAuthHelper = googleAuthHelper,
        modifier = modifier,
        text = text,
        textColor = textColor,
        icon = icon,
        onSuccess = onSuccess,
        onError = onError
    )
}

