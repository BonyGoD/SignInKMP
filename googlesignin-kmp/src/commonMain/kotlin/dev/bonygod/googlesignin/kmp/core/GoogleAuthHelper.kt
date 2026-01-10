package dev.bonygod.googlesignin.kmp.core

expect class GoogleAuthHelper {
    suspend fun signInWithGoogle(onSuccess: (String, String, String, String) -> Unit, onError: (String) -> Unit)
}
