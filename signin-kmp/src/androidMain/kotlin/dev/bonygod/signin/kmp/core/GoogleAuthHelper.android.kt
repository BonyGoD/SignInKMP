package dev.bonygod.signin.kmp.core

import android.app.Activity
import android.content.Context
import android.content.ContextWrapper
import android.util.Log
import androidx.credentials.CredentialManager
import androidx.credentials.CustomCredential
import androidx.credentials.GetCredentialRequest
import androidx.credentials.exceptions.GetCredentialCancellationException
import androidx.credentials.exceptions.GetCredentialException
import androidx.credentials.exceptions.NoCredentialException
import com.google.android.libraries.identity.googleid.GetGoogleIdOption
import com.google.android.libraries.identity.googleid.GetSignInWithGoogleOption
import com.google.android.libraries.identity.googleid.GoogleIdTokenCredential
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.GoogleAuthProvider
import kotlinx.coroutines.tasks.await
import org.koin.core.component.KoinComponent
import org.koin.core.component.inject
import org.koin.core.qualifier.named

private const val TAG = "SignInKMP"

actual class GoogleAuthHelper(
    private val context: Context,
    private val credentialManager: CredentialManager
) : KoinComponent {

    actual suspend fun signInWithGoogle(
        onSuccess: (String, String, String, String) -> Unit,
        onError: (String) -> Unit
    ) {
        // CLIENT_ID idéntico a v2.0.0 — inyección directa desde Koin
        val clientId: String by inject(named("CLIENT_ID"))

        // ── Intento principal: GetGoogleIdOption ───────────────────────────────
        // Idéntico a v2.0.0. Muestra el bottom sheet desde la parte inferior.
        try {
            val googleIdOption = GetGoogleIdOption.Builder()
                .setServerClientId(clientId)
                .setAutoSelectEnabled(false)
                .setFilterByAuthorizedAccounts(false)
                .build()

            val request = GetCredentialRequest.Builder()
                .addCredentialOption(googleIdOption)
                .build()

            val result = credentialManager.getCredential(context, request)
            processCredential(result.credential, onSuccess, onError)

        } catch (e: GetCredentialCancellationException) {
            // El usuario cerró el selector — no hay fallback, es intencional
            Log.i(TAG, "Usuario canceló el selector de cuentas.")
            onError("SignIn_Cancelled")

        } catch (e: NoCredentialException) {
            // ── Fallback HyperOS/OEM ───────────────────────────────────────────
            // En HyperOS, GetGoogleIdOption falla con NoCredentialException porque
            // el sistema reporta "sin cuentas autorizadas" aunque las haya.
            // GetSignInWithGoogleOption omite esa capa y fuerza el selector del sistema.
            Log.w(TAG, "NoCredentialException en GetGoogleIdOption [${e.message}]. " +
                    "Activando fallback GetSignInWithGoogleOption para OEM/HyperOS…")
            signInWithGoogleOptionFallback(clientId, onSuccess, onError)

        } catch (e: GetCredentialException) {
            Log.e(TAG, "GetCredentialException [${e::class.simpleName}]: ${e.message}")
            onError("GetCredentialException: ${e.message}")

        } catch (e: Exception) {
            Log.e(TAG, "Error inesperado [${e::class.simpleName}]: ${e.message}", e)
            onError("Error: ${e.message}")
        }
    }

    /**
     * Fallback exclusivo para dispositivos OEM (HyperOS, MIUI) donde
     * GetGoogleIdOption lanza NoCredentialException.
     * Solo se activa en ese caso concreto — no afecta a dispositivos normales.
     */
    private suspend fun signInWithGoogleOptionFallback(
        clientId: String,
        onSuccess: (String, String, String, String) -> Unit,
        onError: (String) -> Unit
    ) {
        try {
            val siwgOption = GetSignInWithGoogleOption.Builder(clientId).build()
            val request = GetCredentialRequest.Builder()
                .addCredentialOption(siwgOption)
                .build()

            val result = credentialManager.getCredential(context, request)
            processCredential(result.credential, onSuccess, onError)

        } catch (e: GetCredentialCancellationException) {
            Log.i(TAG, "Usuario canceló el selector de cuentas (fallback OEM).")
            onError("SignIn_Cancelled")

        } catch (e: NoCredentialException) {
            Log.e(TAG, "NoCredentialException en fallback OEM: ${e.message}")
            onError("No email found in your device")

        } catch (e: GetCredentialException) {
            Log.e(TAG, "GetCredentialException en fallback OEM [${e::class.simpleName}]: ${e.message}")
            onError("GetCredentialException: ${e.message}")

        } catch (e: Exception) {
            Log.e(TAG, "Error inesperado en fallback OEM [${e::class.simpleName}]: ${e.message}", e)
            onError("Error: ${e.message}")
        }
    }

    private suspend fun processCredential(
        credential: androidx.credentials.Credential,
        onSuccess: (String, String, String, String) -> Unit,
        onError: (String) -> Unit
    ) {
        if (credential is CustomCredential &&
            credential.type == GoogleIdTokenCredential.TYPE_GOOGLE_ID_TOKEN_CREDENTIAL
        ) {
            try {
                val googleIdTokenCredential = GoogleIdTokenCredential.createFrom(credential.data)
                val idToken = googleIdTokenCredential.idToken
                val authCredential = GoogleAuthProvider.getCredential(idToken, null)
                val fireBase = FirebaseAuth.getInstance()
                val user = fireBase.signInWithCredential(authCredential).await().user
                val uid = user?.uid
                val profileImageUrl = user?.photoUrl?.toString()

                Log.i(TAG, "Firebase Sign-In exitoso. UID: ${user?.uid}")
                onSuccess(
                    user?.displayName ?: "user",
                    uid ?: "",
                    user?.email ?: "",
                    profileImageUrl ?: ""
                )
            } catch (e: Exception) {
                Log.e(TAG, "Firebase Sign-In falló [${e::class.simpleName}]: ${e.message}", e)
                onError("Error: ${e.message}")
            }
        } else {
            Log.e(TAG, "Tipo de credencial inesperado: ${credential.type}")
            onError("Invalid credential type")
        }
    }
}

/**
 * Sube la cadena de ContextWrapper hasta encontrar una Activity.
 * Retorna null si el contexto no está ligado a ninguna Activity activa.
 */
internal fun Context.findActivity(): Activity? {
    var ctx = this
    while (ctx is ContextWrapper) {
        if (ctx is Activity) return ctx
        ctx = ctx.baseContext
    }
    return null
}
