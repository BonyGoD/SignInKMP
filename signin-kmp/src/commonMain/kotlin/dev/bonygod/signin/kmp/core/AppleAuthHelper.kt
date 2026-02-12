package dev.bonygod.signin.kmp.core

expect class AppleAuthHelper {
    suspend fun signInWithApple(onSuccess: (String, String, String, String) -> Unit, onError: (String) -> Unit)
}

