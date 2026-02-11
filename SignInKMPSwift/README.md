# SignInKMPSwift

Swift Package para integrar autenticación social (Google, Apple, etc.) con Kotlin Multiplatform en iOS.

Este package actúa como **bridge de comunicación** entre Kotlin Multiplatform y las APIs nativas de autenticación de iOS, permitiendo implementar múltiples proveedores de sign-in de forma unificada.

## 📦 Proveedores Soportados

- ✅ **Google Sign-In** - Implementado
- 🔜 **Apple Sign-In** - Próximamente
- 🔜 **Otros proveedores** - En el roadmap

## 📦 Instalación

### Desde GitHub (Recomendado)

1. En Xcode, abre tu proyecto
2. **File → Add Package Dependencies...**
3. Pega la URL: `https://github.com/BonyGoD/GoogleSignInKMP`
4. Selecciona **"GoogleSignInKMPSwift"** 
5. Versión: `1.0.0` (Up to Next Major)
6. Click **"Add Package"**

### Local (Para desarrollo)

1. En Xcode, abre tu proyecto
2. **File → Add Package Dependencies → Add Local**
3. Selecciona la carpeta `GoogleSignInKMPSwift`
4. Click **"Add Package"**

## 🔧 Configuración

En tu `iOSApp.swift`:

```swift
import FirebaseCore
import GoogleSignIn
import SignInKMPSwift

class AppDelegate: NSObject, UIApplicationDelegate {
    func application(_ application: UIApplication,
                     didFinishLaunchingWithOptions launchOptions: [UIApplication.LaunchOptionsKey: Any]? = nil) -> Bool {
        FirebaseCore.configure()
        _ = SignInCallbackHelper.shared
        return true
    }

    func application(_ app: UIApplication, open url: URL,
                     options: [UIApplication.OpenURLOptionsKey : Any] = [:]) -> Bool {
        return GIDSignIn.sharedInstance.handle(url)
    }
}
```

## 🏗️ Arquitectura

Este package actúa como **bridge de comunicación** entre tu código Kotlin Multiplatform y las APIs nativas de autenticación de iOS:

### Componentes:

1. **SignInCallbackHelper**: Coordinador central que escucha notificaciones de Kotlin mediante `NSNotificationCenter`
2. **GoogleSignInBridge**: Implementación específica para Google Sign-In con Firebase
3. **AppleSignInBridge**: (Próximamente) Implementación para Apple Sign-In
4. **GoogleUserData**: Modelo de datos del usuario (compatible con múltiples proveedores)

### Flujo de Comunicación (Ejemplo con Google):

```
Kotlin → NSNotificationCenter ("GoogleSignInRequested")
   ↓
SignInCallbackHelper recibe notificación
   ↓
GoogleSignInBridge ejecuta login con Google
   ↓
NSNotificationCenter ("GoogleSignInSuccess" con datos)
   ↓
Kotlin recibe datos del usuario
```

> **Nota:** El mismo patrón de notificaciones se utilizará para Apple Sign-In y otros proveedores, manteniendo una arquitectura consistente.
Kotlin recibe datos del usuario
```

## 📄 Licencia

MIT License

