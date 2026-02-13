package dev.bonygod.signin.kmp.ui

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.painter.Painter

@Composable
expect fun AppleSignin(
    modifier: Modifier = Modifier,
    text: String = "Sign in with Apple",
    textColor: Color = Color.Black,
    containerColor: Color = Color.Black,
    contentColor: Color = Color.White,
    icon: Painter? = null,
    onSuccess: (displayName: String, uid: String, email: String, photoUrl: String) -> Unit,
    onError: (errorMessage: String) -> Unit
)
