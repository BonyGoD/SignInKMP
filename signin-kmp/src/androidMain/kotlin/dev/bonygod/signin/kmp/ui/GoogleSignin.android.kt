package dev.bonygod.signin.kmp.ui

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.painter.Painter
import androidx.compose.ui.platform.LocalContext
import androidx.credentials.CredentialManager
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
    // Se pasa LocalContext.current directamente (ContextThemeWrapper en Compose).
    // El CredentialManager lo necesita tal cual para anclar el bottom sheet
    // al borde inferior de la pantalla. No usar findActivity() aquí.
    val context = LocalContext.current
    val googleAuthHelper = GoogleAuthHelper(context, CredentialManager.create(context))

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

