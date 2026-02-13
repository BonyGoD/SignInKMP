package dev.bonygod.signin.kmp.ui.components

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
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
import dev.bonygod.signin.kmp.core.AppleAuthHelper
import kotlinx.coroutines.launch

@Composable
fun AppleButton(
    appleAuthHelper: AppleAuthHelper,
    modifier: Modifier = Modifier,
    text: String = "Sign in with Apple",
    textColor: Color = Color.White,
    containerColor: Color = Color.Black,
    contentColor: Color = Color.White,
    icon: Painter? = null,
    onSuccess: (displayName: String, uid: String, email: String, photoUrl: String) -> Unit,
    onError: (errorMessage: String) -> Unit
) {

    val scope = rememberCoroutineScope()

    Button(
        modifier = modifier,
        onClick = {
            scope.launch {
                appleAuthHelper.signInWithApple(
                    onSuccess = { displayName, uid, email, photoUrl ->
                        onSuccess(displayName, uid, email, photoUrl)
                    },
                    onError = { errorMsg ->
                        onError(errorMsg)
                    })
            }
        },
        colors = ButtonDefaults.buttonColors(
            containerColor = containerColor,
            contentColor = contentColor
        )
    ) {
        Box(modifier = Modifier.fillMaxWidth()) {
            icon?.let {
                Row(
                    modifier = Modifier.align(Alignment.Center),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Icon(
                        painter = it,
                        contentDescription = "Apple logo",
                        tint = Color.Unspecified,
                        modifier = Modifier.size(18.dp).alignByBaseline()
                    )
                    Text(
                        modifier = Modifier
                            .padding(start = 10.dp)
                            .alignByBaseline(),
                        text = text,
                        fontWeight = FontWeight.Normal,
                        fontSize = 16.sp,
                        color = textColor,
                    )
                }
            } ?: run {
                Text(
                    modifier = Modifier.align(Alignment.Center),
                    text = text,
                    fontWeight = FontWeight.Normal,
                    fontSize = 16.sp,
                    color = textColor,
                )
            }
        }
    }
}

//@Preview(showBackground = true)
//@Composable
//fun AppleButtonPreview() {
//    AppleButton(
//        modifier = Modifier.padding(16.dp),
//        text = "Sign in with Apple",
//        textColor = Color.White,
//        icon = painterResource(Res.drawable.applelogo),
//        onSuccess = { _, _, _, _ -> },
//        onError = { }
//    )
//}
