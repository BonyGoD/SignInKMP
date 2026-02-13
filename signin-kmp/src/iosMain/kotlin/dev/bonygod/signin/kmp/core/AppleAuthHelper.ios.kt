package dev.bonygod.signin.kmp.core

import kotlinx.coroutines.suspendCancellableCoroutine
import platform.Foundation.NSNotificationCenter
import platform.Foundation.NSNotification
import platform.Foundation.NSOperationQueue
import kotlin.coroutines.resume

@OptIn(kotlinx.cinterop.ExperimentalForeignApi::class)
actual class AppleAuthHelper {

    actual suspend fun signInWithApple(
        onSuccess: (String, String, String, String) -> Unit,
        onError: (String) -> Unit,
    ) {
        suspendCancellableCoroutine<Unit> { cont ->
            var successObserverRef: Any? = null
            var errorObserverRef: Any? = null

            val successObserver = NSNotificationCenter.defaultCenter.addObserverForName(
                name = "AppleSignInSuccess",
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

                    successObserverRef?.let { NSNotificationCenter.defaultCenter.removeObserver(it) }
                    errorObserverRef?.let { NSNotificationCenter.defaultCenter.removeObserver(it) }
                }
            )

            val errorObserver = NSNotificationCenter.defaultCenter.addObserverForName(
                name = "AppleSignInError",
                `object` = null,
                queue = NSOperationQueue.mainQueue,
                usingBlock = { notification: NSNotification? ->
                    val userInfo = notification?.userInfo
                    val errorMessage = userInfo?.get("error") as? String ?: "Unknown error"

                    onError(errorMessage)
                    cont.resume(Unit)

                    successObserverRef?.let { NSNotificationCenter.defaultCenter.removeObserver(it) }
                    errorObserverRef?.let { NSNotificationCenter.defaultCenter.removeObserver(it) }
                }
            )

            successObserverRef = successObserver
            errorObserverRef = errorObserver

            NSNotificationCenter.defaultCenter.postNotificationName(
                "AppleSignInRequested",
                `object` = null
            )
        }
    }
}

