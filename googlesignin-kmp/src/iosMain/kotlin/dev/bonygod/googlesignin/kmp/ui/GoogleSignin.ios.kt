package dev.bonygod.googlesignin.kmp.ui

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import dev.bonygod.googlesignin.kmp.core.GoogleAuthHelper
import dev.bonygod.googlesignin.kmp.ui.components.GoogleButton

@Composable
actual fun GoogleSignin(
    onSuccess: (displayName: String, uid: String, email: String, photoUrl: String) -> Unit,
    onError: (errorMessage: String) -> Unit
) {
    val googleAuthHelper = GoogleAuthHelper()

    GoogleButton(
        googleAuthHelper = googleAuthHelper,
        onSuccess = onSuccess,
        onError = onError
    )
}

