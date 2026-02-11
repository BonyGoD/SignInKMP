package dev.bonygod.signin.kmp.ui.components

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.size
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.painter.Painter
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import dev.bonygod.signin.kmp.core.GoogleAuthHelper
import kotlinx.coroutines.launch

@Composable
fun GoogleButton(
    googleAuthHelper: GoogleAuthHelper,
    modifier: Modifier,
    text: String,
    textColor: Color ,
    icon: Painter?,
    onSuccess: (displayName: String, uid: String, email: String, photoUrl: String) -> Unit,
    onError: (errorMessage: String) -> Unit
) {

    val scope = rememberCoroutineScope()

    Button(
        modifier = modifier,
        onClick = {
            scope.launch {
                googleAuthHelper.signInWithGoogle(
                    onSuccess = { displayName, uid, email, photoUrl ->
                        onSuccess(displayName, uid, email, photoUrl)
                    },
                    onError = { errorMsg ->
                        onError(errorMsg)
                    })
            }
        },
        colors = ButtonDefaults.buttonColors(
            containerColor = Color.Transparent
        )
    ) {
        Box(modifier = Modifier.fillMaxWidth()) {
            icon?.let {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier.align(Alignment.CenterStart)
                ) {
                    Icon(
                        painter = it,
                        contentDescription = null,
                        tint = Color.Unspecified,
                        modifier = Modifier.size(24.dp)
                    )
                }
            }
            Text(
                text,
                fontWeight = FontWeight.Bold,
                fontSize = 16.sp,
                color = textColor,
                modifier = Modifier.align(Alignment.Center)
            )
        }
    }
}