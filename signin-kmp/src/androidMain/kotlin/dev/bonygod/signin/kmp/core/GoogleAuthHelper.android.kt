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
import androidx.credentials.exceptions.GetCredentialProviderConfigurationException
import androidx.credentials.exceptions.NoCredentialException
import com.google.android.libraries.identity.googleid.GetGoogleIdOption
import com.google.android.libraries.identity.googleid.GetSignInWithGoogleOption
import com.google.android.libraries.identity.googleid.GoogleIdTokenCredential
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.GoogleAuthProvider
import dev.bonygod.signin.kmp.BuildConfig
import kotlinx.coroutines.tasks.await
import org.koin.core.component.KoinComponent
import org.koin.core.component.inject
import org.koin.core.qualifier.named

private const val TAG = "SignInKMP"
private const val PLACEHOLDER_ID = "CONFIGURE_IN_LOCAL_PROPERTIES"

actual class GoogleAuthHelper(
    private val context: Context,
    private val credentialManager: CredentialManager
) : KoinComponent {

    actual suspend fun signInWithGoogle(
        onSuccess: (String, String, String, String) -> Unit,
        onError: (String) -> Unit
    ) {
        // ── 1. Resolver client ID ──────────────────────────────────────────────
        val clientId = resolveClientId()
        if (clientId == null) {
            Log.e(TAG, "CLIENT_ID no configurado. Regístralo en Koin con named(\"CLIENT_ID\") " +
                    "o añade CLIENT_ID en local.properties del proyecto que consuma la librería.")
            onError("Google Sign-In no está configurado: CLIENT_ID es inválido o no se encontró.")
            return
        }

        // ── 2. Verificar Activity en la cadena de contexto ────────────────────
        //    Solo usamos findActivity() como guard de seguridad.
        //    Para getCredential() pasamos siempre el context original (LocalContext.current)
        //    que en Compose es un ContextThemeWrapper: el CredentialManager lo necesita
        //    para anclar el bottom sheet al borde inferior de la pantalla correctamente.
        if (context.findActivity() == null) {
            Log.e(TAG, "No se encontró una Activity en la cadena del contexto. " +
                    "Asegúrate de que GoogleSignin() se usa dentro de una Activity/ComposeActivity.")
            onError("Google Sign-In requiere un contexto de Activity. " +
                    "Asegúrate de llamarlo desde una pantalla activa.")
            return
        }

        // ── 3. Intento principal: GetGoogleIdOption ────────────────────────────
        //    Muestra el bottom sheet nativo deslizándose desde la parte inferior
        //    de la pantalla — UX estándar en la mayoría de dispositivos Android.
        //    setFilterByAuthorizedAccounts(false) fuerza el selector completo de
        //    cuentas en lugar de solo las ya autorizadas.
        try {
            Log.d(TAG, "Intentando GetGoogleIdOption (bottom sheet nativo)…")
            val googleIdOption = GetGoogleIdOption.Builder()
                .setServerClientId(clientId)
                .setFilterByAuthorizedAccounts(false)
                .setAutoSelectEnabled(false)
                .build()
            val request = GetCredentialRequest.Builder()
                .addCredentialOption(googleIdOption)
                .build()
            val result = credentialManager.getCredential(context, request)
            processCredential(result.credential, onSuccess, onError)
            return
        } catch (e: GetCredentialCancellationException) {
            Log.i(TAG, "El usuario canceló el selector de cuentas.")
            onError("SignIn_Cancelled: El usuario cerró el selector de cuentas.")
            return
        } catch (e: GetCredentialException) {
            Log.w(TAG, "GetGoogleIdOption falló [${e::class.simpleName}]: ${e.message}. " +
                    "Pasando al fallback GetSignInWithGoogleOption (HyperOS/OEM)…")
            // fall-through al Step 4
        } catch (e: Exception) {
            Log.w(TAG, "GetGoogleIdOption error inesperado [${e::class.simpleName}]: ${e.message}. " +
                    "Pasando al fallback GetSignInWithGoogleOption (HyperOS/OEM)…")
            // fall-through al Step 4
        }

        // ── 4. Fallback: GetSignInWithGoogleOption ─────────────────────────────
        //    Flujo alternativo para OEMs (HyperOS, MIUI) donde GetGoogleIdOption
        //    falla porque el sistema reporta "sin cuentas autorizadas".
        //    Este flujo omite esa capa y fuerza el selector de cuentas del sistema.
        try {
            Log.d(TAG, "Intentando fallback GetSignInWithGoogleOption (OEM/HyperOS)…")
            val siwgOption = GetSignInWithGoogleOption.Builder(clientId).build()
            val request = GetCredentialRequest.Builder()
                .addCredentialOption(siwgOption)
                .build()
            val result = credentialManager.getCredential(context, request)
            processCredential(result.credential, onSuccess, onError)
        } catch (e: GetCredentialCancellationException) {
            Log.i(TAG, "El usuario canceló el selector de cuentas (fallback).")
            onError("SignIn_Cancelled: El usuario cerró el selector de cuentas.")
        } catch (e: NoCredentialException) {
            Log.e(TAG, "NoCredentialException: ${e.message}. " +
                    "Posibles causas: no hay cuenta Google en el dispositivo, " +
                    "Play Services desactualizado, o restricciones OEM (HyperOS).")
            onError("No se encontró ninguna cuenta de Google en este dispositivo. " +
                    "Asegúrate de tener una cuenta Google configurada en Ajustes.")
        } catch (e: GetCredentialProviderConfigurationException) {
            Log.e(TAG, "GetCredentialProviderConfigurationException [${e::class.simpleName}]: ${e.message}. " +
                    "Verifica que CLIENT_ID sea el Web Client ID correcto de Firebase/Google Cloud.")
            onError("Error de configuración de Google Sign-In. " +
                    "Verifica el CLIENT_ID en la consola de Firebase.")
        } catch (e: GetCredentialException) {
            Log.e(TAG, "GetCredentialException [${e::class.simpleName}]: ${e.message}")
            onError("Google Sign-In falló: ${e::class.simpleName}. " +
                    "Consulta los logs con tag '$TAG' para más detalles.")
        } catch (e: Exception) {
            Log.e(TAG, "Error inesperado [${e::class.simpleName}]: ${e.message}", e)
            onError("Google Sign-In falló inesperadamente: ${e::class.simpleName}.")
        }
    }

    /**
     * Resuelve el CLIENT_ID con la siguiente prioridad:
     * 1. Koin named("CLIENT_ID") — permite al host sobreescribir en runtime.
     * 2. BuildConfig.CLIENT_ID  — configurado vía local.properties en desarrollo.
     *
     * Retorna null si ninguna fuente produce un valor válido.
     */
    private fun resolveClientId(): String? {
        // Fuente 1: Koin (host app puede inyectar su propio CLIENT_ID)
        try {
            val koinId: String by inject(named("CLIENT_ID"))
            if (koinId.isNotBlank() && koinId != PLACEHOLDER_ID) {
                Log.d(TAG, "CLIENT_ID resuelto desde Koin.")
                return koinId
            }
        } catch (_: Exception) {
            Log.d(TAG, "Koin named(\"CLIENT_ID\") no disponible, usando BuildConfig como fallback.")
        }

        // Fuente 2: BuildConfig (librería compilada con local.properties)
        val buildConfigId = BuildConfig.CLIENT_ID
        if (buildConfigId.isNotBlank() && buildConfigId != PLACEHOLDER_ID) {
            Log.d(TAG, "CLIENT_ID resuelto desde BuildConfig.")
            return buildConfigId
        }

        return null
    }

    /**
     * Procesa la credencial obtenida y autentica contra Firebase Auth.
     */
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
                val firebaseAuth = FirebaseAuth.getInstance()
                val user = firebaseAuth.signInWithCredential(authCredential).await().user

                Log.i(TAG, "Firebase Sign-In exitoso. UID: ${user?.uid}")
                onSuccess(
                    user?.displayName ?: "",
                    user?.uid ?: "",
                    user?.email ?: "",
                    user?.photoUrl?.toString() ?: ""
                )
            } catch (e: Exception) {
                Log.e(TAG, "Firebase Sign-In falló [${e::class.simpleName}]: ${e.message}", e)
                onError("La autenticación con Firebase falló: ${e::class.simpleName}.")
            }
        } else {
            Log.e(TAG, "Tipo de credencial inesperado recibido: ${credential.type}")
            onError("Tipo de credencial no reconocido: ${credential.type}.")
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
