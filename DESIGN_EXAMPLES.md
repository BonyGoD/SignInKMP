# 🎨 Ejemplos Visuales - Google & Apple Sign-In

## Pantalla de Login Completa

### Opción 1: Diseño Vertical (Recomendado)

```kotlin
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Divider
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
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
fun ModernLoginScreen(
    onLoginSuccess: (uid: String) -> Unit,
    onError: (message: String) -> Unit
) {
    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Color(0xFFF5F5F5)),
        contentAlignment = Alignment.Center
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth(0.85f)
                .background(Color.White, RoundedCornerShape(24.dp))
                .padding(32.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            // Logo o Icono de la App
            Text(
                text = "🔐",
                fontSize = 64.sp,
                modifier = Modifier.padding(bottom = 16.dp)
            )
            
            Text(
                text = "Bienvenido",
                fontSize = 28.sp,
                fontWeight = FontWeight.Bold,
                color = Color(0xFF212121)
            )
            
            Text(
                text = "Inicia sesión para continuar",
                fontSize = 16.sp,
                color = Color(0xFF757575),
                modifier = Modifier.padding(top = 8.dp, bottom = 32.dp)
            )

            // Botón de Google
            GoogleSignin(
                modifier = Modifier
                    .fillMaxWidth()
                    .border(1.5.dp, Color(0xFFE0E0E0), RoundedCornerShape(12.dp))
                    .clip(RoundedCornerShape(12.dp))
                    .height(56.dp),
                text = "Continuar con Google",
                textColor = Color(0xFF212121),
                icon = painterResource(Res.drawable.google_icon),
                onSuccess = { _, uid, _, _ ->
                    onLoginSuccess(uid)
                },
                onError = { error ->
                    onError(error)
                }
            )

            // Espaciador con "o"
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 20.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Divider(
                    modifier = Modifier.weight(1f),
                    color = Color(0xFFE0E0E0)
                )
                Text(
                    text = "o",
                    modifier = Modifier.padding(horizontal = 16.dp),
                    color = Color(0xFF757575),
                    fontSize = 14.sp
                )
                Divider(
                    modifier = Modifier.weight(1f),
                    color = Color(0xFFE0E0E0)
                )
            }

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
                onSuccess = { _, uid, _, _ ->
                    onLoginSuccess(uid)
                },
                onError = { error ->
                    onError(error)
                }
            )

            // Términos y condiciones
            Text(
                text = "Al continuar, aceptas nuestros Términos y Condiciones",
                fontSize = 12.sp,
                color = Color(0xFF757575),
                modifier = Modifier.padding(top = 24.dp),
                textAlign = androidx.compose.ui.text.style.TextAlign.Center
            )
        }
    }
}
```

### Opción 2: Diseño Minimalista

```kotlin
@Composable
fun MinimalistLoginScreen(
    onLoginSuccess: (uid: String) -> Unit,
    onError: (message: String) -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Color.White)
            .padding(horizontal = 24.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        // Título simple
        Text(
            text = "Iniciar Sesión",
            fontSize = 32.sp,
            fontWeight = FontWeight.Light,
            modifier = Modifier.padding(bottom = 64.dp)
        )

        // Botones sin bordes, solo iconos y texto
        GoogleSignin(
            modifier = Modifier
                .fillMaxWidth()
                .height(48.dp),
            text = "Google",
            textColor = Color(0xFF212121),
            icon = painterResource(Res.drawable.google_icon),
            onSuccess = { _, uid, _, _ -> onLoginSuccess(uid) },
            onError = onError
        )

        Spacer(modifier = Modifier.height(16.dp))

        AppleSignin(
            modifier = Modifier
                .fillMaxWidth()
                .height(48.dp),
            text = "Apple",
            textColor = Color(0xFF212121),
            icon = painterResource(Res.drawable.apple_icon),
            onSuccess = { _, uid, _, _ -> onLoginSuccess(uid) },
            onError = onError
        )
    }
}
```

### Opción 3: Diseño con Gradiente

```kotlin
import androidx.compose.ui.graphics.Brush

@Composable
fun GradientLoginScreen(
    onLoginSuccess: (uid: String) -> Unit,
    onError: (message: String) -> Unit
) {
    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(
                brush = Brush.verticalGradient(
                    colors = listOf(
                        Color(0xFF667EEA),
                        Color(0xFF764BA2)
                    )
                )
            ),
        contentAlignment = Alignment.Center
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth(0.9f)
                .background(
                    Color.White.copy(alpha = 0.95f),
                    RoundedCornerShape(20.dp)
                )
                .padding(40.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(
                text = "🚀",
                fontSize = 56.sp,
                modifier = Modifier.padding(bottom = 24.dp)
            )

            Text(
                text = "¡Comienza Ahora!",
                fontSize = 26.sp,
                fontWeight = FontWeight.Bold,
                modifier = Modifier.padding(bottom = 40.dp)
            )

            // Google con sombra
            GoogleSignin(
                modifier = Modifier
                    .fillMaxWidth()
                    .shadow(
                        elevation = 4.dp,
                        shape = RoundedCornerShape(12.dp)
                    )
                    .background(Color.White, RoundedCornerShape(12.dp))
                    .clip(RoundedCornerShape(12.dp))
                    .height(56.dp),
                text = "Continuar con Google",
                textColor = Color(0xFF212121),
                icon = painterResource(Res.drawable.google_icon),
                onSuccess = { _, uid, _, _ -> onLoginSuccess(uid) },
                onError = onError
            )

            Spacer(modifier = Modifier.height(16.dp))

            // Apple con sombra
            AppleSignin(
                modifier = Modifier
                    .fillMaxWidth()
                    .shadow(
                        elevation = 4.dp,
                        shape = RoundedCornerShape(12.dp)
                    )
                    .background(Color.Black, RoundedCornerShape(12.dp))
                    .clip(RoundedCornerShape(12.dp))
                    .height(56.dp),
                text = "Continuar con Apple",
                textColor = Color.White,
                icon = painterResource(Res.drawable.apple_icon),
                onSuccess = { _, uid, _, _ -> onLoginSuccess(uid) },
                onError = onError
            )
        }
    }
}
```

### Opción 4: Diseño Horizontal (Para Tablets/Desktop)

```kotlin
@Composable
fun HorizontalLoginScreen(
    onLoginSuccess: (uid: String) -> Unit,
    onError: (message: String) -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxSize()
            .background(Color(0xFFFAFAFA))
    ) {
        // Lado izquierdo: Información
        Box(
            modifier = Modifier
                .weight(1f)
                .fillMaxHeight()
                .background(
                    brush = Brush.horizontalGradient(
                        colors = listOf(
                            Color(0xFF667EEA),
                            Color(0xFF764BA2)
                        )
                    )
                ),
            contentAlignment = Alignment.Center
        ) {
            Column(
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Text(
                    text = "🎯",
                    fontSize = 80.sp
                )
                Text(
                    text = "Tu App",
                    fontSize = 40.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color.White,
                    modifier = Modifier.padding(top = 16.dp)
                )
                Text(
                    text = "La mejor experiencia",
                    fontSize = 18.sp,
                    color = Color.White.copy(alpha = 0.9f),
                    modifier = Modifier.padding(top = 8.dp)
                )
            }
        }

        // Lado derecho: Login
        Column(
            modifier = Modifier
                .weight(1f)
                .fillMaxHeight()
                .padding(64.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            Text(
                text = "Bienvenido",
                fontSize = 36.sp,
                fontWeight = FontWeight.Bold,
                modifier = Modifier.padding(bottom = 8.dp)
            )
            
            Text(
                text = "Elige tu método preferido",
                fontSize = 16.sp,
                color = Color(0xFF757575),
                modifier = Modifier.padding(bottom = 48.dp)
            )

            GoogleSignin(
                modifier = Modifier
                    .fillMaxWidth(0.7f)
                    .border(1.5.dp, Color(0xFFE0E0E0), RoundedCornerShape(12.dp))
                    .clip(RoundedCornerShape(12.dp))
                    .height(56.dp),
                text = "Google",
                textColor = Color(0xFF212121),
                icon = painterResource(Res.drawable.google_icon),
                onSuccess = { _, uid, _, _ -> onLoginSuccess(uid) },
                onError = onError
            )

            Spacer(modifier = Modifier.height(20.dp))

            AppleSignin(
                modifier = Modifier
                    .fillMaxWidth(0.7f)
                    .background(Color.Black, RoundedCornerShape(12.dp))
                    .clip(RoundedCornerShape(12.dp))
                    .height(56.dp),
                text = "Apple",
                textColor = Color.White,
                icon = painterResource(Res.drawable.apple_icon),
                onSuccess = { _, uid, _, _ -> onLoginSuccess(uid) },
                onError = onError
            )
        }
    }
}
```

### Opción 5: Solo Iconos (Compacto)

```kotlin
@Composable
fun CompactSocialLogin(
    onLoginSuccess: (uid: String) -> Unit,
    onError: (message: String) -> Unit
) {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = Modifier.padding(16.dp)
    ) {
        Text(
            text = "Iniciar sesión con",
            fontSize = 14.sp,
            color = Color(0xFF757575),
            modifier = Modifier.padding(bottom = 16.dp)
        )

        Row(
            horizontalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            // Botón circular de Google
            GoogleSignin(
                modifier = Modifier
                    .size(56.dp)
                    .border(1.5.dp, Color(0xFFE0E0E0), androidx.compose.foundation.shape.CircleShape)
                    .clip(androidx.compose.foundation.shape.CircleShape),
                text = "",  // Sin texto
                textColor = Color.Transparent,
                icon = painterResource(Res.drawable.google_icon),
                onSuccess = { _, uid, _, _ -> onLoginSuccess(uid) },
                onError = onError
            )

            // Botón circular de Apple
            AppleSignin(
                modifier = Modifier
                    .size(56.dp)
                    .background(Color.Black, androidx.compose.foundation.shape.CircleShape)
                    .clip(androidx.compose.foundation.shape.CircleShape),
                text = "",  // Sin texto
                textColor = Color.Transparent,
                icon = painterResource(Res.drawable.apple_icon),
                onSuccess = { _, uid, _, _ -> onLoginSuccess(uid) },
                onError = onError
            )
        }
    }
}
```

## 🎨 Paletas de Colores Recomendadas

### Google Brand Colors
```kotlin
val GoogleRed = Color(0xFFDB4437)
val GoogleBlue = Color(0xFF4285F4)
val GoogleYellow = Color(0xFFF4B400)
val GoogleGreen = Color(0xFF0F9D58)
val GoogleBackground = Color(0xFFFFFFFF)
val GoogleBorder = Color(0xFFDADADA)
```

### Apple Brand Colors
```kotlin
val AppleBlack = Color(0xFF000000)
val AppleWhite = Color(0xFFFFFFFF)
val AppleGray = Color(0xFF86868B)
```

## 📐 Dimensiones Recomendadas

```kotlin
// Alturas de botones
val ButtonHeightSmall = 44.dp  // Mínimo recomendado
val ButtonHeightMedium = 50.dp // Estándar
val ButtonHeightLarge = 56.dp  // Grande (recomendado para accesibilidad)

// Radios de borde
val BorderRadiusSmall = 8.dp
val BorderRadiusMedium = 12.dp
val BorderRadiusLarge = 16.dp
val BorderRadiusPill = 28.dp  // Para botones con efecto píldora

// Espaciado
val SpacingSmall = 8.dp
val SpacingMedium = 16.dp
val SpacingLarge = 24.dp
val SpacingXLarge = 32.dp
```

## 🌙 Modo Oscuro

```kotlin
@Composable
fun DarkModeLoginScreen(
    isDarkMode: Boolean,
    onLoginSuccess: (uid: String) -> Unit,
    onError: (message: String) -> Unit
) {
    val backgroundColor = if (isDarkMode) Color(0xFF121212) else Color.White
    val textColor = if (isDarkMode) Color.White else Color(0xFF212121)
    val borderColor = if (isDarkMode) Color(0xFF3A3A3A) else Color(0xFFE0E0E0)

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(backgroundColor)
            .padding(24.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Text(
            text = "Bienvenido",
            fontSize = 28.sp,
            fontWeight = FontWeight.Bold,
            color = textColor,
            modifier = Modifier.padding(bottom = 48.dp)
        )

        // Google adapta al modo
        GoogleSignin(
            modifier = Modifier
                .fillMaxWidth()
                .border(1.5.dp, borderColor, RoundedCornerShape(12.dp))
                .background(
                    if (isDarkMode) Color(0xFF1E1E1E) else Color.White,
                    RoundedCornerShape(12.dp)
                )
                .clip(RoundedCornerShape(12.dp))
                .height(56.dp),
            text = "Continuar con Google",
            textColor = textColor,
            icon = painterResource(Res.drawable.google_icon),
            onSuccess = { _, uid, _, _ -> onLoginSuccess(uid) },
            onError = onError
        )

        Spacer(modifier = Modifier.height(16.dp))

        // Apple siempre negro (cumple con las directrices de marca)
        AppleSignin(
            modifier = Modifier
                .fillMaxWidth()
                .background(Color.Black, RoundedCornerShape(12.dp))
                .clip(RoundedCornerShape(12.dp))
                .height(56.dp),
            text = "Continuar con Apple",
            textColor = Color.White,
            icon = painterResource(Res.drawable.apple_icon),
            onSuccess = { _, uid, _, _ -> onLoginSuccess(uid) },
            onError = onError
        )
    }
}
```

## 💡 Tips de Diseño

1. **Apple Guidelines**: Apple recomienda que su botón sea negro con texto blanco
2. **Espaciado**: Mantén al menos 16dp entre botones sociales
3. **Altura mínima**: 44dp para cumplir con estándares de accesibilidad
4. **Orden**: Si incluyes ambos, Google suele ir primero (más común)
5. **Íconos**: Usa los oficiales de cada marca
6. **Responsive**: Adapta el diseño para diferentes tamaños de pantalla

---

**¡Elige el diseño que mejor se adapte a tu app! 🎨**

