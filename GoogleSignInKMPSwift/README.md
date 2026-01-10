# GoogleSignInKMPSwift

Swift Package para integrar Google Sign-In con Kotlin Multiplatform en iOS.

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
import GoogleSignInKMPSwift

class AppDelegate: NSObject, UIApplicationDelegate {
    func application(_ application: UIApplication,
                     didFinishLaunchingWithOptions launchOptions: [UIApplication.LaunchOptionsKey: Any]? = nil) -> Bool {
        FirebaseCore.configure()
        _ = GoogleAuthCallbackHelper.shared
        return true
    }

    func application(_ app: UIApplication, open url: URL,
                     options: [UIApplication.OpenURLOptionsKey : Any] = [:]) -> Bool {
        return GIDSignIn.sharedInstance.handle(url)
    }
}
```

## 🏗️ Arquitectura

Este package actúa como puente entre tu código Kotlin Multiplatform y las APIs nativas de iOS:

1. **GoogleAuthCallbackHelper**: Escucha notificaciones de Kotlin mediante `NSNotificationCenter`
2. **GoogleSignInBridge**: Ejecuta el flujo de Google Sign-In con Firebase
3. **GoogleUserData**: Modelo de datos del usuario

### Flujo de Comunicación

```
Kotlin → NSNotificationCenter ("GoogleSignInRequested")
   ↓
GoogleAuthCallbackHelper recibe notificación
   ↓
GoogleSignInBridge ejecuta login con Google
   ↓
NSNotificationCenter ("GoogleSignInSuccess" con datos)
   ↓
Kotlin recibe datos del usuario
```

## 📄 Licencia

MIT License

