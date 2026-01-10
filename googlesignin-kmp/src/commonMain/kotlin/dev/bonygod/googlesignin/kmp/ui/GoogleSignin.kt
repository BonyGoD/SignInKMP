package dev.bonygod.googlesignin.kmp.ui

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.painter.Painter

@Composable
expect fun GoogleSignin(
    modifier: Modifier = Modifier,
    text: String = "Log in with Google",
    textColor: Color = Color.Black,
    icon: Painter? = null,
    onSuccess: (displayName: String, uid: String, email: String, photoUrl: String) -> Unit,
    onError: (errorMessage: String) -> Unit
)
