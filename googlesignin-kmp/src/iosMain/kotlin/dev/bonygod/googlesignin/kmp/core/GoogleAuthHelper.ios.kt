package dev.bonygod.googlesignin.kmp.core

import kotlinx.coroutines.suspendCancellableCoroutine
import platform.Foundation.NSNotificationCenter
import platform.Foundation.NSNotification
import platform.Foundation.NSOperationQueue
import kotlin.coroutines.resume

@OptIn(kotlinx.cinterop.ExperimentalForeignApi::class)
actual class GoogleAuthHelper {

    actual suspend fun signInWithGoogle(
        onSuccess: (String, String, String, String) -> Unit,
        onError: (String) -> Unit,
    ) {
        suspendCancellableCoroutine<Unit> { cont ->
            var observerRef: Any? = null

            val successObserver = NSNotificationCenter.defaultCenter.addObserverForName(
                name = "GoogleSignInSuccess",
                `object` = null,
                queue = NSOperationQueue.mainQueue,
                usingBlock = { notification: NSNotification? ->
                    val userInfo = notification?.userInfo
                    val displayName = userInfo?.get("displayName") as? String ?: ""
                    val uid = userInfo?.get("uid") as? String ?: ""
                    val email = userInfo?.get("email") as? String ?: ""
                    val photoURL = userInfo?.get("photoURL") as? String ?: ""

                    onSuccess(displayName, uid, email, photoURL)
                    cont.resume(Unit)

                    observerRef?.let { NSNotificationCenter.defaultCenter.removeObserver(it) }
                }
            )

            observerRef = successObserver

            NSNotificationCenter.defaultCenter.postNotificationName(
                "GoogleSignInRequested",
                `object` = null
            )
        }
    }
}
