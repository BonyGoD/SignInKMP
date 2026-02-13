# Ejemplos de Uso - AppleSignIn

## Configuración Previa

### iOS

#### 1. Habilitar Sign in with Apple en tu proyecto
1. En Xcode, selecciona tu target
2. Ve a **Signing & Capabilities**
3. Click en **+ Capability**
4. Busca y agrega **Sign in with Apple**

#### 2. Configurar Firebase
Asegúrate de que Apple esté habilitado como proveedor de autenticación en Firebase Console:
1. Ve a Firebase Console → Authentication → Sign-in method
2. Habilita **Apple**
3. Sigue las instrucciones para configurar tu Apple Service ID

#### 3. Configurar Info.plist (si es necesario)
Agrega las siguientes claves a tu `Info.plist` si planeas usar datos adicionales:

```xml
<key>NSFaceIDUsageDescription</key>
<string>We use Face ID to authenticate you securely</string>
```

### Android

Para Android, Apple Sign-In se maneja a través de Firebase Auth OAuthProvider. Asegúrate de:

1. Habilitar Apple como proveedor en Firebase Console
2. Configurar tu proyecto en la consola de desarrolladores de Apple
3. Agregar las dependencias de Firebase en tu `build.gradle.kts`

## Ejemplos de Implementación

### Ejemplo 1: Botón Simple

```kotlin
import androidx.compose.runtime.Composable
import dev.bonygod.signin.kmp.ui.AppleSignin

@Composable
fun SimpleAppleSignIn() {
    AppleSignin(
        onSuccess = { displayName, uid, email, photoUrl ->
            println("✅ Login exitoso!")
            println("Nombre: $displayName")
            println("UID: $uid")
            println("Email: $email")
        },
        onError = { errorMessage ->
            println("❌ Error: $errorMessage")
        }
    )
}
```

### Ejemplo 2: Botón Personalizado con Estilo de Apple

```kotlin
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import dev.bonygod.signin.kmp.ui.AppleSignin
import org.jetbrains.compose.resources.painterResource

@Composable
fun StyledAppleSignIn() {
    AppleSignin(
        modifier = Modifier
            .fillMaxWidth()
            .padding(16.dp)
            .background(Color.Black, RoundedCornerShape(8.dp))
            .clip(RoundedCornerShape(8.dp))
            .height(56.dp),
        text = "Sign in with Apple",
        textColor = Color.White,
        icon = painterResource(Res.drawable.apple_icon),
        onSuccess = { displayName, uid, email, photoUrl ->
            // Navegar a la pantalla principal
            navigateToHome(uid)
        },
        onError = { errorMessage ->
            // Mostrar error al usuario
            showErrorDialog(errorMessage)
        }
    )
}
```

### Ejemplo 3: Pantalla de Login Completa con Google y Apple

```kotlin
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import dev.bonygod.signin.kmp.ui.AppleSignin
import dev.bonygod.signin.kmp.ui.GoogleSignin
import org.jetbrains.compose.resources.painterResource

@Composable
fun LoginScreen(
    onLoginSuccess: (uid: String, displayName: String, email: String) -> Unit
) {
    var isLoading by remember { mutableStateOf(false) }
    var errorMessage by remember { mutableStateOf<String?>(null) }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(24.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        // Logo o título de la app
        Text(
            text = "Bienvenido",
            fontSize = 32.sp,
            fontWeight = FontWeight.Bold,
            modifier = Modifier.padding(bottom = 48.dp)
        )

        // Botón de Google
        GoogleSignin(
            modifier = Modifier
                .fillMaxWidth()
                .border(1.dp, Color.Gray, RoundedCornerShape(12.dp))
                .clip(RoundedCornerShape(12.dp))
                .height(56.dp),
            text = "Continuar con Google",
            textColor = Color.Black,
            icon = painterResource(Res.drawable.google_icon),
            onSuccess = { displayName, uid, email, photoUrl ->
                isLoading = false
                onLoginSuccess(uid, displayName, email)
            },
            onError = { error ->
                isLoading = false
                errorMessage = error
            }
        )

        Spacer(modifier = Modifier.height(16.dp))

        // Botón de Apple
        AppleSignin(
            modifier = Modifier
                .fillMaxWidth()
                .background(Color.Black, RoundedCornerShape(12.dp))
                .clip(RoundedCornerShape(12.dp))
                .height(56.dp),
            text = "Continuar con Apple",
            textColor = Color.White,
            icon = painterResource(Res.drawable.apple_icon),
            onSuccess = { displayName, uid, email, photoUrl ->
                isLoading = false
                onLoginSuccess(uid, displayName, email)
            },
            onError = { error ->
                isLoading = false
                errorMessage = error
            }
        )

        // Mostrar errores si los hay
        errorMessage?.let { error ->
            Spacer(modifier = Modifier.height(16.dp))
            Text(
                text = error,
                color = Color.Red,
                fontSize = 14.sp
            )
        }
    }
}
```

### Ejemplo 4: Manejo Avanzado con Estados y Navegación

```kotlin
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import dev.bonygod.signin.kmp.ui.AppleSignin
import dev.bonygod.signin.kmp.ui.GoogleSignin
import kotlinx.coroutines.launch

sealed class LoginState {
    object Idle : LoginState()
    object Loading : LoginState()
    data class Success(val uid: String, val name: String, val email: String) : LoginState()
    data class Error(val message: String) : LoginState()
}

@Composable
fun AdvancedLoginScreen(
    onNavigateToHome: (String) -> Unit,
    viewModel: LoginViewModel // Tu ViewModel
) {
    var loginState by remember { mutableStateOf<LoginState>(LoginState.Idle) }
    val scope = rememberCoroutineScope()

    Box(
        modifier = Modifier.fillMaxSize(),
        contentAlignment = Alignment.Center
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(24.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            when (loginState) {
                is LoginState.Loading -> {
                    CircularProgressIndicator()
                }
                else -> {
                    // Botones de login
                    GoogleSignin(
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(56.dp),
                        onSuccess = { displayName, uid, email, photoUrl ->
                            loginState = LoginState.Loading
                            scope.launch {
                                try {
                                    // Guardar datos del usuario
                                    viewModel.saveUserData(uid, displayName, email, photoUrl)
                                    loginState = LoginState.Success(uid, displayName, email)
                                    onNavigateToHome(uid)
                                } catch (e: Exception) {
                                    loginState = LoginState.Error(e.message ?: "Error desconocido")
                                }
                            }
                        },
                        onError = { error ->
                            loginState = LoginState.Error(error)
                        }
                    )

                    Spacer(modifier = Modifier.height(16.dp))

                    AppleSignin(
                        modifier = Modifier
                            .fillMaxWidth()
                            .background(Color.Black, RoundedCornerShape(12.dp))
                            .clip(RoundedCornerShape(12.dp))
                            .height(56.dp),
                        textColor = Color.White,
                        onSuccess = { displayName, uid, email, photoUrl ->
                            loginState = LoginState.Loading
                            scope.launch {
                                try {
                                    viewModel.saveUserData(uid, displayName, email, photoUrl)
                                    loginState = LoginState.Success(uid, displayName, email)
                                    onNavigateToHome(uid)
                                } catch (e: Exception) {
                                    loginState = LoginState.Error(e.message ?: "Error desconocido")
                                }
                            }
                        },
                        onError = { error ->
                            loginState = LoginState.Error(error)
                        }
                    )

                    // Mostrar mensajes de error
                    if (loginState is LoginState.Error) {
                        Spacer(modifier = Modifier.height(16.dp))
                        Text(
                            text = (loginState as LoginState.Error).message,
                            color = Color.Red
                        )
                    }
                }
            }
        }
    }
}
```

## Notas Importantes

### Sobre Apple Sign-In

1. **Primera vez vs siguientes veces**: Apple solo proporciona el nombre completo del usuario en el primer inicio de sesión. En los siguientes, solo recibirás el email y el UID. Asegúrate de guardar esta información en tu backend.

2. **Email privado**: Los usuarios pueden optar por ocultar su email real. En ese caso, Apple proporcionará un email relay (ejemplo: `xyz123@privaterelay.appleid.com`).

3. **Requisitos de Apple**: Si ofreces Google Sign-In, según las políticas de Apple, también debes ofrecer Sign in with Apple en iOS.

4. **Permisos**: Apple Sign-In no requiere permisos especiales en iOS, pero sí necesitas la capacidad "Sign in with Apple" habilitada en tu proyecto de Xcode.

### Testing

Para probar Apple Sign-In:
- **iOS Simulator**: Funciona con cuentas de Apple ID reales
- **Android**: Requiere configuración adicional en Firebase Console y la consola de desarrolladores de Apple

### Troubleshooting

Si encuentras errores:

1. **"Invalid Apple Credential"**: Verifica que el nonce esté configurado correctamente
2. **"No se pudo obtener el view controller"**: Asegúrate de inicializar `SignInCallbackHelper.shared` en tu AppDelegate
3. **Firebase Auth Error**: Verifica que Apple esté habilitado en Firebase Console y configurado correctamente

## Recursos Adicionales

- [Apple Sign-In Guidelines](https://developer.apple.com/design/human-interface-guidelines/sign-in-with-apple)
- [Firebase Apple Auth Documentation](https://firebase.google.com/docs/auth/ios/apple)
- [Sign in with Apple for Android](https://firebase.google.com/docs/auth/android/apple)

