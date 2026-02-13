package dev.bonygod.signin.kmp.core

import android.content.Context
import androidx.credentials.CredentialManager

actual class AppleAuthHelper(
    private val context: Context,
    private val credentialManager: CredentialManager
) {
    actual suspend fun signInWithApple(
        onSuccess: (String, String, String, String) -> Unit,
        onError: (String) -> Unit
    ) {
        // Apple Sign-In no está disponible en Android
        // Solo funciona en iOS
        onError("Apple Sign-In is only available on iOS devices")
    }
}


