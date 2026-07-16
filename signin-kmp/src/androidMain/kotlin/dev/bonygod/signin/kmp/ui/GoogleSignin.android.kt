package dev.bonygod.signin.kmp.ui

import android.app.Activity
import android.util.Log
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.ActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.painter.Painter
import androidx.compose.ui.platform.LocalContext
import androidx.credentials.CredentialManager
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.GoogleAuthProvider
import dev.bonygod.signin.kmp.core.GoogleAuthHelper
import dev.bonygod.signin.kmp.core.findActivity
import dev.bonygod.signin.kmp.ui.components.GoogleButton
import kotlinx.coroutines.CancellableContinuation
import kotlinx.coroutines.launch
import kotlinx.coroutines.suspendCancellableCoroutine
import kotlinx.coroutines.tasks.await
import org.koin.compose.koinInject
import org.koin.core.qualifier.named
import java.util.concurrent.atomic.AtomicReference
import kotlin.coroutines.resume

private const val TAG = "SignInKMP"

@Composable
actual fun GoogleSignin(
    modifier: Modifier,
    text: String,
    textColor: Color,
    icon: Painter?,
    onSuccess: (displayName: String, uid: String, email: String, photoUrl: String) -> Unit,
    onError: (errorMessage: String) -> Unit
) {
    val context = LocalContext.current
    val scope = rememberCoroutineScope()
    val clientId: String = koinInject(named("CLIENT_ID"))

    // Referencia mutable para la continuación del legacy launcher.
    // AtomicReference evita problemas de concurrencia entre el launcher callback
    // (main thread) y el coroutine que lo espera.
    val pendingContRef = remember { AtomicReference<CancellableContinuation<ActivityResult?>?>(null) }

    // Launcher para el fallback legacy (GoogleSignInClient.signInIntent).
    // Solo se activa en HyperOS cuando GetSignInWithGoogleOption también falla.
    val legacyLauncher = rememberLauncherForActivityResult(
        ActivityResultContracts.StartActivityForResult()
    ) { result ->
        pendingContRef.getAndSet(null)?.resume(result)
    }

    val googleAuthHelper = remember(context) {
        GoogleAuthHelper(context, CredentialManager.create(context))
    }

    GoogleButton(
        googleAuthHelper = googleAuthHelper,
        modifier = modifier,
        text = text,
        textColor = textColor,
        icon = icon,
        onSuccess = onSuccess,
        onError = { errorMessage ->
            if (errorMessage == "SignIn_OEM_Cancelled") {
                // GetSignInWithGoogleOption fue cancelado por HyperOS.
                // Usamos la API legacy (GoogleSignInClient) que no pasa por WebView
                // y que HyperOS no interfiere.
                scope.launch {
                    try {
                        val activity = context.findActivity() ?: run {
                            Log.e(TAG, "Legacy fallback: Activity no encontrada")
                            onError("SignIn_Cancelled")
                            return@launch
                        }

                        val gso = GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                            .requestIdToken(clientId)
                            .requestEmail()
                            .build()
                        val client = GoogleSignIn.getClient(activity, gso)

                        // Sign out para forzar el selector de cuentas
                        client.signOut().await()

                        // Lanzar el intent y esperar el resultado
                        val activityResult = suspendCancellableCoroutine<ActivityResult?> { cont ->
                            pendingContRef.set(cont)
                            legacyLauncher.launch(client.signInIntent)
                            cont.invokeOnCancellation { pendingContRef.set(null) }
                        }

                        if (activityResult?.resultCode != Activity.RESULT_OK) {
                            Log.i(TAG, "Usuario canceló el legacy selector de cuentas.")
                            onError("SignIn_Cancelled")
                            return@launch
                        }

                        val account = GoogleSignIn
                            .getSignedInAccountFromIntent(activityResult.data)
                            .await()

                        val idToken = account.idToken ?: run {
                            Log.e(TAG, "Legacy fallback: idToken nulo")
                            onError("No Google ID token received")
                            return@launch
                        }

                        val firebaseCredential = GoogleAuthProvider.getCredential(idToken, null)
                        val user = FirebaseAuth.getInstance()
                            .signInWithCredential(firebaseCredential)
                            .await()
                            .user

                        Log.i(TAG, "Legacy GoogleSignIn exitoso. UID: ${user?.uid}")
                        onSuccess(
                            user?.displayName ?: "user",
                            user?.uid ?: "",
                            user?.email ?: "",
                            user?.photoUrl?.toString() ?: ""
                        )

                    } catch (e: Exception) {
                        Log.e(TAG, "Legacy GoogleSignIn falló [${e::class.simpleName}]: ${e.message}", e)
                        onError("Error: ${e.message}")
                    }
                }
            } else {
                onError(errorMessage)
            }
        }
    )
}
