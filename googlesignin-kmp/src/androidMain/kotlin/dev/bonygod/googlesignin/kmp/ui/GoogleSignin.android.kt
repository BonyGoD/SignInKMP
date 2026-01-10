package dev.bonygod.googlesignin.kmp.ui

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.credentials.CredentialManager
import dev.bonygod.googlesignin.kmp.core.GoogleAuthHelper
import dev.bonygod.googlesignin.kmp.ui.components.GoogleButton

@Composable
actual fun GoogleSignin(
    onSuccess: (displayName: String, uid: String, email: String, photoUrl: String) -> Unit,
    onError: (errorMessage: String) -> Unit
) {
    val context = LocalContext.current
    val googleAuthHelper = GoogleAuthHelper(context, CredentialManager.create(context))

    GoogleButton(
        googleAuthHelper = googleAuthHelper,
        onSuccess = onSuccess,
        onError = onError
    )
}


