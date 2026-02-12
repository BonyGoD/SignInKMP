# 🚀 Guía de Configuración Completa - Apple Sign-In

## ⚠️ IMPORTANTE: Apple Sign-In solo para iOS

**Apple Sign-In en esta librería está diseñado SOLO para iOS.**

- ✅ **iOS**: Funciona con AuthenticationServices nativo de Apple
- ❌ **Android**: No está soportado (retorna error si se intenta usar)

**¿Por qué?** 
- Apple Sign-In es principalmente para el ecosistema Apple
- En Android, los usuarios esperan Google Sign-In
- Simplifica la implementación y mantenimiento

---

## Requisitos Previos

- [ ] Cuenta de desarrollador de Apple activa ($99/año) - **Solo para iOS**
- [ ] Proyecto configurado en Firebase
- [ ] Xcode instalado (para iOS)
- [ ] Android Studio con proyecto KMP configurado

---

## Parte 1: Configuración en Apple Developer Console

### Paso 1: Configurar App ID

1. Ve a [Apple Developer Console](https://developer.apple.com/account/)
2. Navega a **Certificates, Identifiers & Profiles**
3. Selecciona **Identifiers** → **App IDs**
4. Selecciona tu App ID existente o crea uno nuevo
5. En **Capabilities**, marca **Sign in with Apple**
6. Guarda los cambios

### Paso 2: Crear Service ID

1. En **Identifiers**, selecciona el botón **+** (agregar)
2. Selecciona **Services IDs** y continúa
3. Completa:
   - **Description**: "My App - Apple Sign In"
   - **Identifier**: `com.tuapp.signin` (debe ser único)
4. Marca **Sign in with Apple**
5. Click en **Configure** junto a "Sign in with Apple"
6. Configura:
   - **Primary App ID**: Selecciona tu App ID principal
   - **Domains and Subdomains**: Agrega el dominio de Firebase de tu proyecto
     
     **¿Cómo obtener tu dominio de Firebase?**
     
     a) Ve a [Firebase Console](https://console.firebase.google.com/)
     
     b) Selecciona tu proyecto
     
     c) En la parte superior verás el nombre de tu proyecto, por ejemplo: `mi-proyecto-123456`
     
     d) Tu dominio será: `mi-proyecto-123456.firebaseapp.com`
     
     **Ejemplos:**
     - Si tu proyecto se llama `myapp-12345`, usa: `myapp-12345.firebaseapp.com`
     - Si tu proyecto se llama `social-login-kmp`, usa: `social-login-kmp.firebaseapp.com`
     
     ⚠️ **IMPORTANTE**: NO incluyas `https://`, solo el dominio
     
     ✅ **Correcto**: `mi-proyecto-123456.firebaseapp.com`
     
     ❌ **Incorrecto**: `https://mi-proyecto-123456.firebaseapp.com`
     
   - **Return URLs**: Agrega la URL completa de Firebase:
     ```
     https://mi-proyecto-123456.firebaseapp.com/__/auth/handler
     ```
     (Reemplaza `mi-proyecto-123456` con el ID real de tu proyecto)
     
7. Guarda y continúa

### Paso 3: Crear Key para Sign in with Apple

1. Ve a **Keys** en el menú lateral
2. Click en el botón **+** (agregar)
3. Completa:
   - **Key Name**: "Apple Sign In Key"
4. Marca **Sign in with Apple**
5. Click en **Configure**
6. Selecciona tu **Primary App ID**
7. Click en **Save**
8. Click en **Continue** y luego **Register**
9. **⚠️ IMPORTANTE**: Descarga la clave (archivo .p8) - solo puedes hacerlo una vez
10. Guarda:
    - **Key ID**: Lo necesitarás para Firebase
    - El archivo **.p8** descargado

---

## Parte 2: Configuración en Firebase Console

### 🔍 PRIMERO: Obtener tu Dominio de Firebase

**Antes de configurar en Apple Developer Console, necesitas obtener estos datos de Firebase:**

1. Ve a [Firebase Console](https://console.firebase.google.com/)
2. Selecciona tu proyecto
3. En la URL del navegador verás algo como:
   ```
   https://console.firebase.google.com/project/mi-proyecto-123456/overview
   ```
   
4. El ID de tu proyecto es: `mi-proyecto-123456`

5. Tu dominio de Firebase es: `mi-proyecto-123456.firebaseapp.com`

**Ejemplo Real:**
```
URL de Firebase Console: 
https://console.firebase.google.com/project/signin-kmp-demo/overview

ID del Proyecto: signin-kmp-demo
Dominio Firebase: signin-kmp-demo.firebaseapp.com
Return URL: https://signin-kmp-demo.firebaseapp.com/__/auth/handler
```

**⚠️ Guarda esta información para configurar en Apple Developer Console**

---

### Paso 1: Habilitar Apple como Proveedor

1. Ve a [Firebase Console](https://console.firebase.google.com/)
2. Selecciona tu proyecto
3. Ve a **Authentication** → **Sign-in method**
4. Encuentra **Apple** en la lista de proveedores
5. Click en **Apple** para configurar

### Paso 2: Configurar Credenciales de Apple

En la configuración de Apple en Firebase, necesitarás:

1. **Service ID**: El identificador que creaste (ej: `com.tuapp.signin`)
2. **Apple Team ID**: 
   - Encuéntralo en Apple Developer Console
   - Ve a **Membership** en el menú superior
   - Copia el **Team ID** (10 caracteres)
3. **Key ID**: Del paso 3 de Apple Developer Console
4. **Private Key**: 
   - Abre el archivo .p8 que descargaste
   - Copia TODO el contenido (incluyendo `-----BEGIN PRIVATE KEY-----` y `-----END PRIVATE KEY-----`)
   - Pégalo en el campo

5. Click en **Save**

### Paso 3: Configurar OAuth Redirect Domain

1. En la configuración de Apple en Firebase, busca **OAuth redirect domains**
2. Copia el dominio que Firebase te proporciona (algo como `tu-proyecto.firebaseapp.com`)
3. Este dominio debe coincidir con el que configuraste en Apple Developer Console

---

## Parte 3: Configuración del Proyecto iOS (Xcode)

### Paso 1: Agregar Capability

1. Abre tu proyecto en Xcode
2. Selecciona tu target en el navegador del proyecto
3. Ve a la pestaña **Signing & Capabilities**
4. Click en **+ Capability**
5. Busca y agrega **Sign in with Apple**

### Paso 2: Actualizar AppDelegate/iOSApp.swift

```swift
import SwiftUI
import FirebaseCore
import GoogleSignIn
import SignInKMPSwift

class AppDelegate: NSObject, UIApplicationDelegate {
    func application(
        _ application: UIApplication,
        didFinishLaunchingWithOptions launchOptions: [UIApplication.LaunchOptionsKey: Any]? = nil
    ) -> Bool {
        // Configurar Firebase
        FirebaseApp.configure()
        
        // Inicializar el helper de callbacks (MUY IMPORTANTE)
        _ = SignInCallbackHelper.shared
        
        return true
    }

    func application(
        _ app: UIApplication,
        open url: URL,
        options: [UIApplication.OpenURLOptionsKey : Any] = [:]
    ) -> Bool {
        // Manejar URLs de Google Sign-In
        return GIDSignIn.sharedInstance.handle(url)
    }
}

@main
struct iOSApp: App {
    @UIApplicationDelegateAdaptor(AppDelegate.self) var appDelegate

    var body: some Scene {
        WindowGroup {
            ContentView()
        }
    }
}
```

### Paso 3: Verificar el Bundle ID

Asegúrate de que el **Bundle Identifier** en Xcode coincida con el App ID configurado en Apple Developer Console.

### Paso 4: Agregar Firebase Configuration

1. Descarga `GoogleService-Info.plist` de Firebase Console
2. Arrastra el archivo a tu proyecto en Xcode
3. Asegúrate de que esté marcado para tu target

---

## Parte 4: Configuración del Proyecto Android

### ⚠️ Apple Sign-In NO está disponible en Android

En Android, el componente `AppleSignin()` mostrará un error si se intenta usar:
```
"Apple Sign-In is only available on iOS devices"
```

**Recomendación:** En Android, usa solo `GoogleSignin()` para autenticación.

### Si quieres mantener una UI consistente:

Puedes ocultar el botón de Apple en Android usando `expect`/`actual`:

```kotlin
// commonMain
expect val isAppleSignInAvailable: Boolean

// androidMain
actual val isAppleSignInAvailable: Boolean = false

// iosMain
actual val isAppleSignInAvailable: Boolean = true

// En tu UI
@Composable
fun LoginScreen() {
    GoogleSignin(...)
    
    if (isAppleSignInAvailable) {
        AppleSignin(...)
    }
}
```

### Configuración Mínima para Android

Solo necesitas tener Firebase configurado para Google Sign-In:

1. **google-services.json** en `android/app/`
2. Dependencias de Firebase en `build.gradle.kts`
3. Usa solo `GoogleSignin()` en tu UI de Android

---

## Parte 5: Usar AppleSignIn en tu Código

### En tu Composable (Común para Android e iOS)

```kotlin
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import dev.bonygod.signin.kmp.ui.AppleSignin
import org.jetbrains.compose.resources.painterResource

@Composable
fun MyLoginScreen() {
    var userUid by remember { mutableStateOf<String?>(null) }
    var errorMessage by remember { mutableStateOf<String?>(null) }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(24.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        AppleSignin(
            modifier = Modifier
                .fillMaxWidth()
                .background(Color.Black, RoundedCornerShape(12.dp))
                .clip(RoundedCornerShape(12.dp))
                .height(56.dp),
            text = "Sign in with Apple",
            textColor = Color.White,
            icon = painterResource(Res.drawable.apple_icon),
            onSuccess = { displayName, uid, email, photoUrl ->
                // ✅ Login exitoso
                userUid = uid
                println("Usuario: $displayName")
                println("Email: $email")
                println("UID: $uid")
                
                // Navegar a la siguiente pantalla
                // navigateToHome(uid)
            },
            onError = { error ->
                // ❌ Error en login
                errorMessage = error
                println("Error: $error")
            }
        )

        // Mostrar errores si los hay
        errorMessage?.let { error ->
            Text(
                text = error,
                color = Color.Red,
                modifier = Modifier.padding(top = 16.dp)
            )
        }
    }
}
```

---

## 🧪 Testing y Troubleshooting

### Testing en iOS Simulator

1. El simulador de iOS soporta Apple Sign-In
2. Necesitarás una cuenta de Apple ID real
3. Ve a **Settings** → **Sign in to your iPhone** en el simulador
4. Inicia sesión con tu Apple ID
5. Prueba tu app

### Testing en Dispositivo iOS Real

1. Conecta tu dispositivo
2. Asegúrate de estar firmado en tu cuenta de Apple
3. Ejecuta la app
4. El flujo de Apple Sign-In debería funcionar normalmente

### Testing en Android

**Apple Sign-In NO funciona en Android.** Si intentas usarlo, recibirás el error:
```
"Apple Sign-In is only available on iOS devices"
```

**Para Android:** Usa solo `GoogleSignin()` en tu aplicación.

### Problemas Comunes y Soluciones

#### ❌ "Invalid Apple Credential"
**Solución**: 
- Verifica que el Service ID esté correctamente configurado en Firebase
- Verifica que la clave privada (.p8) esté correctamente pegada en Firebase

#### ❌ "No se pudo obtener el view controller"
**Solución**: 
- Asegúrate de inicializar `SignInCallbackHelper.shared` en el AppDelegate
- Verifica que el import de SignInKMPSwift esté correcto

#### ❌ Firebase Auth Error
**Solución**: 
- Verifica que Apple esté habilitado en Firebase Console
- Confirma que todos los dominios estén configurados correctamente
- Verifica que la clave privada sea válida

#### ❌ "The operation couldn't be completed"
**Solución**: 
- Verifica que el App ID tenga la capability de Sign in with Apple
- Asegúrate de que el Bundle ID coincida

#### ❌ No recibo el nombre del usuario
**Solución**: 
- Apple solo proporciona el nombre en el **primer** inicio de sesión
- Para pruebas, ve a Settings → Apple ID → Password & Security → Apps Using Your Apple ID
- Elimina tu app de la lista y vuelve a probar

---

## ✅ Checklist Final

### Apple Developer Console
- [ ] App ID configurado con Sign in with Apple
- [ ] Service ID creado y configurado
- [ ] Key (.p8) descargada y guardada
- [ ] Dominios y Return URLs configurados

### Firebase Console
- [ ] Apple habilitado como proveedor
- [ ] Service ID agregado
- [ ] Team ID agregado
- [ ] Key ID agregado
- [ ] Private Key agregada
- [ ] OAuth redirect domains configurados

### Proyecto iOS
- [ ] Capability "Sign in with Apple" agregada
- [ ] SignInCallbackHelper.shared inicializado en AppDelegate
- [ ] GoogleService-Info.plist agregado
- [ ] Bundle ID coincide con Apple Developer Console

### Proyecto Android
- [ ] google-services.json agregado
- [ ] Dependencias de Firebase Auth incluidas
- [ ] Proyecto sincronizado

### Código
- [ ] AppleSignin() implementado en tu UI
- [ ] Callbacks onSuccess y onError manejados
- [ ] Navegación configurada después del login exitoso

---

## 📚 Recursos Adicionales

- [Documentación oficial de Apple Sign-In](https://developer.apple.com/sign-in-with-apple/)
- [Firebase Apple Auth para iOS](https://firebase.google.com/docs/auth/ios/apple)
- [Firebase Apple Auth para Android](https://firebase.google.com/docs/auth/android/apple)
- [Human Interface Guidelines - Sign in with Apple](https://developer.apple.com/design/human-interface-guidelines/sign-in-with-apple)

---

**¡Configuración completa! Ahora tu app está lista para usar Apple Sign-In en iOS y Android. 🎉**






