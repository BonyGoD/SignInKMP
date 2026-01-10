# Google Sign-In KMP

[![JitPack](https://jitpack.io/v/BonyGoD/GoogleSignInKMP.svg)](https://jitpack.io/#BonyGoD/GoogleSignInKMP)
[![Kotlin](https://img.shields.io/badge/Kotlin-2.1.0-blue.svg?logo=kotlin)](http://kotlinlang.org)
[![License](https://img.shields.io/badge/License-MIT-green.svg)](https://opensource.org/licenses/MIT)

Librería Kotlin Multiplatform para integrar Google Sign-In con Firebase en aplicaciones Android e iOS.

## 📦 Características

- ✅ Google Sign-In con Firebase Auth
- ✅ Soporte para Android e iOS
- ✅ API común en Kotlin Multiplatform
- ✅ Componente Compose Multiplatform incluido

## 🚀 Instalación

### Usando JitPack

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
    implementation("com.github.BonyGoD:GoogleSignInKMP:1.0.2")
}
```

### Android

1. Agrega la dependencia (ver arriba)

2. Configura Firebase en tu proyecto Android siguiendo la [documentación oficial](https://firebase.google.com/docs/android/setup)

### iOS

#### Opción A: Swift Package desde GitHub (Recomendado)

1. En Xcode, abre tu proyecto
2. **File → Add Package Dependencies...**
3. En el campo de búsqueda, pega: `https://github.com/BonyGoD/GoogleSignInKMP`
4. Selecciona **"GoogleSignInKMPSwift"** de la lista
5. En **"Dependency Rule"**, selecciona **"Up to Next Major"** y versión `1.0.0`
6. Click **"Add Package"**
7. Selecciona tu target y click **"Add Package"**

#### Opción B: Swift Package Local (Para desarrollo)

1. En Xcode, abre tu proyecto
2. **File → Add Package Dependencies → Add Local...**
3. Navega a la carpeta clonada del repositorio
4. Selecciona la carpeta **`GoogleSignInKMPSwift`**
5. Click **"Add Package"**

#### Configuración en tu iOSApp.swift:

```swift
import FirebaseCore
import GoogleSignIn
import GoogleSignInKMPSwift

class AppDelegate: NSObject, UIApplicationDelegate {
    func application(_ application: UIApplication,
                     didFinishLaunchingWithOptions launchOptions: [UIApplication.LaunchOptionsKey: Any]? = nil) -> Bool {
        FirebaseApp.configure()
        _ = GoogleAuthCallbackHelper.shared
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

### Básico (sin icono)

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

### Con icono de Google (personalizado)

```kotlin
import androidx.compose.material3.Icon
import androidx.compose.ui.unit.dp
import dev.bonygod.googlesignin.kmp.ui.GoogleSignin
import org.jetbrains.compose.resources.painterResource

@Composable
fun LoginScreen() {
    GoogleSignin(
        onSuccess = { displayName, uid, email, photoUrl ->
            println("Usuario autenticado: $displayName")
        },
        onError = { errorMessage ->
            println("Error: $errorMessage")
        },
        leadingIcon = {
            Icon(
                painter = painterResource(Res.drawable.google_icon),
                contentDescription = "Google",
                modifier = Modifier.size(24.dp),
                tint = Color.Unspecified
            )
        }
    )
}
```

> **Nota:** El icono de Google no está incluido en la librería. Puedes descargarlo desde [Google Brand Resources](https://developers.google.com/identity/branding-guidelines) y agregarlo a tus recursos de Compose (`composeResources/drawable/`).

## 🏗️ Arquitectura

### Android
- Usa directamente las APIs de Google Sign-In y Firebase Auth

### iOS
- Comunicación entre Kotlin y Swift mediante `NSNotificationCenter`
- Swift Package con helpers para Google Sign-In

### Flujo

```
Usuario → GoogleSignin() composable → GoogleAuthHelper
         ↓
   Android: API nativa directa
   iOS: NSNotificationCenter → Swift → Google Sign-In → Firebase Auth
         ↓
   Callback onSuccess() con datos del usuario
```

## 📁 Estructura del Proyecto

```
googlesignin-kmp/           # Librería Kotlin Multiplatform
├── androidMain/            # Implementación Android
├── iosMain/                # Implementación iOS (Kotlin)
└── commonMain/             # Código común

GoogleSignInKMPSwift/       # Swift Package para iOS
└── Sources/
    └── GoogleSignInKMPSwift/
        ├── GoogleAuthCallbackHelper.swift
        ├── GoogleSignInBridge.swift
        └── GoogleUserData.swift
```

## 📄 Licencia

MIT License

## 👤 Autor

Ivan Boniquet Rodriguez (@BonyGoD)

