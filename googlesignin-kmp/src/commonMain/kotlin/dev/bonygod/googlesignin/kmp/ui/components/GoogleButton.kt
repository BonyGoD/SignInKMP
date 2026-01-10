package dev.bonygod.googlesignin.kmp.ui.components

import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import dev.bonygod.googlesignin.kmp.core.GoogleAuthHelper
import kotlinx.coroutines.launch

@Composable
fun GoogleButton(
    googleAuthHelper: GoogleAuthHelper,
    onSuccess: (displayName: String, uid: String, email: String, photoUrl: String) -> Unit,
    onError: (errorMessage: String) -> Unit
) {

    val scope = rememberCoroutineScope()

    Button(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 10.dp)
            .border(1.dp, Color(0xFF000000), RoundedCornerShape(30.dp))
            .clip(shape = RoundedCornerShape(30.dp))
            .height(50.dp),
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
            containerColor = Color.Transparent,
            contentColor = Color(0xFF000000)
        )
    ) {
        Box(modifier = Modifier.fillMaxWidth()) {
            Text(
                "Acceder con Google",
                fontWeight = FontWeight.Bold,
                fontSize = 16.sp,
                color = Color.White,
                modifier = Modifier.align(Alignment.Center)
            )
        }
    }
}