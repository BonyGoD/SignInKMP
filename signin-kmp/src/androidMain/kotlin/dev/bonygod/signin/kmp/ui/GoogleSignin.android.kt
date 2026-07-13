package dev.bonygod.signin.kmp.ui

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.painter.Painter
import androidx.compose.ui.platform.LocalContext
import androidx.credentials.CredentialManager
import dev.bonygod.signin.kmp.core.GoogleAuthHelper
import dev.bonygod.signin.kmp.core.findActivity
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
    // LocalContext.current en Compose es la Activity que aloja la pantalla.
    // GoogleAuthHelper.findActivity() sube la cadena ContextWrapper por si acaso
    // el contexto llegara envuelto (p.ej. ContextThemeWrapper en algunos OEMs).
    val context = LocalContext.current
    val activity = context.findActivity() ?: context
    val googleAuthHelper = GoogleAuthHelper(activity, CredentialManager.create(activity))

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

