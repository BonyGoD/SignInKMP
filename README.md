# Social Sign-In KMP (Google & Apple)

[![JitPack](https://jitpack.io/v/BonyGoD/SignInKMP.svg)](https://jitpack.io/#BonyGoD/SignInKMP)
[![Kotlin](https://img.shields.io/badge/Kotlin-2.1.0-blue.svg?logo=kotlin)](http://kotlinlang.org)
[![License](https://img.shields.io/badge/License-Proprietary-red.svg)](LICENSE)

Librería Kotlin Multiplatform para integrar Google Sign-In y Apple Sign-In con Firebase en aplicaciones Android e iOS.

## 📦 Características

- ✅ Google Sign-In con Firebase Auth
- ✅ Apple Sign-In con Firebase Auth
- ✅ Soporte para Android e iOS
- ✅ API común en Kotlin Multiplatform
- ✅ Componentes Compose Multiplatform incluidos

## 🚀 Instalación

### Opción 1: Usando Maven Local (Desarrollo)

Para desarrollo local, publica la librería en tu Maven Local:

```bash
./gradlew publishToMavenLocal
```

Luego configura tu proyecto:

#### 1. Agrega el repositorio Maven Local

En tu `settings.gradle.kts`:

```kotlin
dependencyResolutionManagement {
    repositories {
        mavenLocal()  // ← Agrega esto primero
        google()
        mavenCentral()
    }
}
```

#### 2. Agrega la dependencia

En tu módulo `build.gradle.kts`:

```kotlin
dependencies {
    implementation("com.github.BonyGoD:signin-kmp:2.0.1")
}
```

> **Nota:** Cada vez que hagas cambios en la librería, ejecuta `./gradlew publishToMavenLocal` para actualizar la versión local.
> 
> 📖 **[Ver guía completa de desarrollo local →](LOCAL_DEVELOPMENT.md)**

### Opción 2: Usando JitPack (Producción)

#### 1. Agrega el repositorio JitPack

En tu `settings.gradle.kts`:

```kotlin
dependencyResolutionManagement {
    repositories {
        google()
        mavenCentral()
        maven { url = uri("https://jitpack.io") }
    }
}
```

O en tu `build.gradle.kts` raíz:

```kotlin
allprojects {
    repositories {
        google()
        mavenCentral()
        maven { url = uri("https://jitpack.io") }
    }
}
```

#### 2. Agrega la dependencia

En tu módulo `build.gradle.kts`:

```kotlin
dependencies {
    implementation("com.github.BonyGoD:SignInKMP:TAG")
}
```

### Android

1. Agrega la dependencia (ver arriba)

2. Configura Firebase en tu proyecto Android siguiendo la [documentación oficial](https://firebase.google.com/docs/android/setup)

3. Registra el `CLIENT_ID` (Web Client ID de Firebase/Google Cloud) en tu módulo Koin:

```kotlin
// En tu módulo Koin (Application o donde inicialices Koin)
val authModule = module {
    single(named("CLIENT_ID")) { "TU_WEB_CLIENT_ID_DE_FIREBASE.apps.googleusercontent.com" }
}
```

> El **Web Client ID** lo encuentras en Firebase Console → Autenticación → Método de inicio de sesión → Google → ID de cliente web.

#### Requisitos del dispositivo

- Google Play Services instalado y actualizado
- Al menos una cuenta Google configurada en **Ajustes → Cuentas**

#### Troubleshooting OEM (HyperOS, MIUI, OneUI)

La librería usa una estrategia de dos pasos para maximizar la compatibilidad:
1. **Primario**: `GetSignInWithGoogleOption` (flujo de botón Google, más robusto en ROMs personalizadas)
2. **Fallback**: `GetGoogleIdOption` con `filterByAuthorizedAccounts=false` (fuerza el selector completo de cuentas)

Si en un dispositivo HyperOS/MIUI el selector no aparece, comprueba:
- Que Google Play Services tenga permisos de inicio en segundo plano
- Que la app no esté en la lista de "optimización de batería" agresiva
- Que el CLIENT_ID sea el **Web Client ID** (no el Android Client ID)
- Filtra logcat con el tag `SignInKMP` para ver el paso exacto donde falla

### iOS

#### Swift Package desde GitHub

1. En Xcode, abre tu proyecto
2. **File → Add Package Dependencies...**
3. En el campo de búsqueda, pega: `https://github.com/BonyGoD/SignInKMP`
4. En **"Dependency Rule"**, selecciona **"Exact Version"** y escribe `2.0.1`
5. Click **"Add Package"**
6. Selecciona **"SignInKMPSwift"** de la lista de productos
7. Selecciona tu target y click **"Add Package"**

> ⚠️ **Importante:** Debes usar **"Exact Version"** para que funcione correctamente. Las opciones "Up to Next Major" o "Up to Next Minor" pueden causar problemas de resolución de dependencias.


#### Configuración en tu iOSApp.swift:

```swift
import FirebaseCore
import GoogleSignIn
import SignInKMPSwift

class AppDelegate: NSObject, UIApplicationDelegate {
    func application(_ application: UIApplication,
                     didFinishLaunchingWithOptions launchOptions: [UIApplication.LaunchOptionsKey: Any]? = nil) -> Bool {
        FirebaseApp.configure()
        _ = SignInCallbackHelper.shared
        return true
    }

    func application(_ app: UIApplication, open url: URL,
                     options: [UIApplication.OpenURLOptionsKey : Any] = [:]) -> Bool {
        return GIDSignIn.sharedInstance.handle(url)
    }
}
```

3. Configura Firebase en tu proyecto iOS siguiendo la [documentación oficial](https://firebase.google.com/docs/ios/setup)

## 💻 Uso

### Google Sign-In

#### Ejemplo completo con personalización

```kotlin
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import dev.bonygod.googlesignin.kmp.ui.GoogleSignin
import org.jetbrains.compose.resources.painterResource

@Composable
fun LoginScreen() {
    GoogleSignin(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 10.dp)
            .border(1.dp, Color(0xFF000000), RoundedCornerShape(30.dp))
            .clip(shape = RoundedCornerShape(30.dp))
            .height(50.dp),
        text = "Login with google",
        textColor = Color.Black,
        icon = painterResource(Res.drawable.google_icon),
        onSuccess = { displayName, uid, email, photoUrl ->
            // Handle successful sign-in
        },
        onError = { errorMessage ->
            // Handle sign-in error
        }
    )
}
```

#### Ejemplo básico (sin personalización)

```kotlin
import dev.bonygod.googlesignin.kmp.ui.GoogleSignin

@Composable
fun LoginScreen() {
    GoogleSignin(
        onSuccess = { displayName, uid, email, photoUrl ->
            println("Usuario autenticado: $displayName")
            // Maneja el inicio de sesión exitoso
        },
        onError = { errorMessage ->
            println("Error: $errorMessage")
            // Maneja el error
        }
    )
}
```

### Apple Sign-In

#### Ejemplo completo con personalización

```kotlin
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import dev.bonygod.signin.kmp.ui.AppleSignin
import org.jetbrains.compose.resources.painterResource

@Composable
fun LoginScreen() {
    AppleSignin(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 10.dp)
            .border(1.dp, Color(0xFF000000), RoundedCornerShape(30.dp))
            .clip(shape = RoundedCornerShape(30.dp))
            .height(50.dp),
        text = "Sign in with Apple",
        textColor = Color.White,
        icon = painterResource(Res.drawable.apple_icon),
        onSuccess = { displayName, uid, email, photoUrl ->
            // Handle successful sign-in
        },
        onError = { errorMessage ->
            // Handle sign-in error
        }
    )
}
```

#### Ejemplo básico (sin personalización)

```kotlin
import dev.bonygod.signin.kmp.ui.AppleSignin

@Composable
fun LoginScreen() {
    AppleSignin(
        onSuccess = { displayName, uid, email, photoUrl ->
            println("Usuario autenticado: $displayName")
            // Maneja el inicio de sesión exitoso
        },
        onError = { errorMessage ->
            println("Error: $errorMessage")
            // Maneja el error
        }
    )
}
```

### Parámetros disponibles (ambos componentes)

- **`modifier`**: `Modifier = Modifier` - Control completo sobre el estilo del botón
- **`text`**: `String` - Texto del botón (por defecto: "Log in with Google" / "Sign in with Apple")
- **`textColor`**: `Color = Color.Black` - Color del texto
- **`icon`**: `Painter? = null` - Icono opcional (se muestra a la izquierda del texto)
- **`onSuccess`**: Callback cuando el login es exitoso con datos del usuario
- **`onError`**: Callback cuando ocurre un error

> **Nota:** Los iconos de Google y Apple no están incluidos en la librería. Puedes descargarlos desde:
> - Google: [Google Brand Resources](https://developers.google.com/identity/branding-guidelines)
> - Apple: [Apple Design Resources](https://developer.apple.com/design/resources/)
> 
> Agrégalos a tus recursos de Compose (`composeResources/drawable/`).

## 🏗️ Arquitectura

### Android
- Usa directamente las APIs de Google Sign-In, Apple Sign-In (via Firebase OAuthProvider) y Firebase Auth

### iOS
- Comunicación entre Kotlin y Swift mediante `NSNotificationCenter`
- Swift Package (`SignInKMPSwift`) que actúa como bridge para múltiples proveedores de autenticación

### Flujo

```
Usuario → GoogleSignin() / AppleSignin() composable → GoogleAuthHelper / AppleAuthHelper
         ↓
   Android: API nativa directa con Firebase
   iOS: NSNotificationCenter → SignInKMPSwift Bridge → Proveedor (Google/Apple) → Firebase Auth
         ↓
   Callback onSuccess() con datos del usuario
```

> **Arquitectura escalable:** El bridge `SignInKMPSwift` está diseñado para soportar múltiples proveedores (Google, Apple, etc.) usando el mismo patrón de comunicación.

## 📁 Estructura del Proyecto

```
signin-kmp/                 # Librería Kotlin Multiplatform
├── androidMain/            # Implementación Android
│   ├── GoogleAuthHelper.android.kt
│   └── AppleAuthHelper.android.kt
├── iosMain/                # Implementación iOS (Kotlin)
│   ├── GoogleAuthHelper.ios.kt
│   └── AppleAuthHelper.ios.kt
└── commonMain/             # Código común
    ├── GoogleAuthHelper.kt
    ├── AppleAuthHelper.kt
    ├── GoogleButton.kt
    └── AppleButton.kt

SignInKMPSwift/             # Swift Package para iOS
└── Sources/
    └── SignInKMPSwift/
        ├── SignInCallbackHelper.swift
        ├── GoogleSignInBridge.swift
        ├── GoogleUserData.swift
        ├── AppleSignInBridge.swift
        └── AppleUserData.swift
```

## 🤝 Contribuciones

¡Las contribuciones son bienvenidas! Si deseas contribuir al proyecto:

### Reportar Bugs o Sugerir Mejoras
1. Abre un **[Issue](https://github.com/BonyGoD/SignInKMP/issues)** describiendo el problema o la mejora

### Contribuir con Código
1. Haz un **Fork** del repositorio
2. Crea una **rama** con tu feature: `git checkout -b feature/AmazingFeature`
3. **Commit** tus cambios: `git commit -m 'Add some AmazingFeature'`
4. **Push** a la rama: `git push origin feature/AmazingFeature`
5. Abre un **Pull Request** utilizando la plantilla [PULL_REQUEST_TEMPLATE.md](.github/PULL_REQUEST_TEMPLATE.md)

**📋 Importante al crear tu PR:**
- ✅ Completa **todas las secciones** de la plantilla
- ✅ Marca los **checkboxes** aplicables
- ✅ Acepta los **términos de contribución** (cesión de derechos al propietario)
- ✅ Describe claramente los **cambios realizados**
- ✅ Incluye **screenshots** si hay cambios visuales
- ✅ Indica las **plataformas probadas** (Android/iOS)

> Al enviar un Pull Request, aceptas ceder todos los derechos de propiedad intelectual de tu contribución al propietario del repositorio. Consulta la [licencia](LICENSE.md) para más detalles.

## 📄 Licencia

**Licencia de Uso Restringido** - Copyright © 2026 Ivan Boniquet Rodriguez

Esta librería es de código cerrado. Puedes **usar** la librería en tus proyectos, pero **NO puedes**:
- Copiar el código fuente
- Modificar el código fuente
- Redistribuir la librería
- Crear trabajos derivados

Para más detalles, consulta el archivo [LICENSE.md](LICENSE.md).

Para permisos especiales o licencias comerciales, contacta: **bonygod.dev@gmail.com**

## 👤 Autor

**Ivan Boniquet Rodriguez** ([@BonyGoD](https://github.com/BonyGoD))

---

⭐ Si esta librería te ha sido útil, considera darle una estrella en GitHub!
