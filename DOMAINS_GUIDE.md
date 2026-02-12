# 📸 Guía Visual - Configuración de Dominios para Apple Sign-In

## 🎯 Resumen Rápido

### ¿Qué dominio debo usar?

**Respuesta:** El dominio de tu proyecto de Firebase, que tiene el formato:
```
tu-proyecto-id.firebaseapp.com
```

---

## 📋 Paso a Paso con Ejemplos

### 1️⃣ Obtener el Dominio de Firebase

#### Opción A: Desde Firebase Console (Recomendado)

1. **Abre Firebase Console**
   ```
   https://console.firebase.google.com/
   ```

2. **Selecciona tu proyecto**
   
   Verás una lista de tus proyectos. Por ejemplo:
   - Mi App de Login
   - Social Login Demo
   - Test Project

3. **Observa la URL del navegador**
   
   Una vez dentro del proyecto, la URL se verá así:
   ```
   https://console.firebase.google.com/project/social-login-kmp/overview
                                              ^^^^^^^^^^^^^^^^
                                              ESTE ES TU ID
   ```

4. **Tu dominio es:**
   ```
   social-login-kmp.firebaseapp.com
   ```

#### Opción B: Desde Project Settings

1. En Firebase Console, ve a **⚙️ Project Settings** (icono de engranaje arriba a la izquierda)
2. En la pestaña **General**, verás:
   ```
   Project ID: social-login-kmp
   Project Number: 123456789012
   Web API Key: AIza...
   ```
3. Tu dominio es: `[Project ID].firebaseapp.com`

---

## 🔧 Configuración en Apple Developer Console

### Pantalla de Service ID Configuration

Cuando configures tu Service ID, verás algo como:

```
┌─────────────────────────────────────────────────────────┐
│ Configure Sign In with Apple for:                      │
│ com.tuapp.signin                                        │
├─────────────────────────────────────────────────────────┤
│                                                         │
│ Primary App ID:                                         │
│ [▼] Select App ID...                                    │
│     └─ com.tuapp.myapp                                  │
│                                                         │
│ Website URLs:                                           │
│                                                         │
│ Domains and Subdomains:                                 │
│ ┌─────────────────────────────────────────────────┐   │
│ │ social-login-kmp.firebaseapp.com                │   │
│ └─────────────────────────────────────────────────┘   │
│                                                         │
│ Return URLs:                                            │
│ ┌─────────────────────────────────────────────────┐   │
│ │ https://social-login-kmp.firebaseapp.com/       │   │
│ │ __/auth/handler                                  │   │
│ └─────────────────────────────────────────────────┘   │
│                                                         │
│ [Cancel]                              [Save]            │
└─────────────────────────────────────────────────────────┘
```

---

## 📝 Ejemplos con Diferentes Proyectos

### Ejemplo 1: Proyecto "myapp-12345"

```yaml
Firebase Project ID: myapp-12345

# Lo que debes escribir en Apple Developer Console:
Domains and Subdomains: myapp-12345.firebaseapp.com
Return URLs: https://myapp-12345.firebaseapp.com/__/auth/handler
```

### Ejemplo 2: Proyecto "signin-kmp-demo"

```yaml
Firebase Project ID: signin-kmp-demo

# Lo que debes escribir en Apple Developer Console:
Domains and Subdomains: signin-kmp-demo.firebaseapp.com
Return URLs: https://signin-kmp-demo.firebaseapp.com/__/auth/handler
```

### Ejemplo 3: Proyecto "social-auth-2024"

```yaml
Firebase Project ID: social-auth-2024

# Lo que debes escribir en Apple Developer Console:
Domains and Subdomains: social-auth-2024.firebaseapp.com
Return URLs: https://social-auth-2024.firebaseapp.com/__/auth/handler
```

---

## ❌ Errores Comunes

### ❌ Error 1: Incluir https://

**Incorrecto:**
```
Domains and Subdomains: https://myapp.firebaseapp.com
```

**Correcto:**
```
Domains and Subdomains: myapp.firebaseapp.com
```

### ❌ Error 2: Usar un dominio personalizado sin configurar

**Incorrecto (a menos que lo hayas configurado):**
```
Domains and Subdomains: www.myapp.com
```

**Correcto:**
```
Domains and Subdomains: myapp-12345.firebaseapp.com
```

### ❌ Error 3: Olvidar /__/auth/handler en Return URL

**Incorrecto:**
```
Return URLs: https://myapp.firebaseapp.com
```

**Correcto:**
```
Return URLs: https://myapp.firebaseapp.com/__/auth/handler
```

### ❌ Error 4: Usar el dominio equivocado

**Incorrecto:**
```
Domains and Subdomains: firebase.google.com
```

**Correcto:**
```
Domains and Subdomains: [TU-PROYECTO-ID].firebaseapp.com
```

---

## ✅ Checklist de Verificación

Antes de guardar tu configuración en Apple Developer Console, verifica:

- [ ] El dominio NO tiene `https://` al inicio
- [ ] El dominio termina en `.firebaseapp.com`
- [ ] El dominio coincide con tu Project ID en Firebase
- [ ] La Return URL SÍ tiene `https://` al inicio
- [ ] La Return URL termina en `/__/auth/handler`
- [ ] Has copiado exactamente el dominio (sin espacios ni errores de tipeo)

---

## 🔍 Cómo Verificar que Todo Está Correcto

### En Apple Developer Console:

1. Ve a tu Service ID
2. Verifica que "Domains and Subdomains" muestre:
   ```
   tu-proyecto.firebaseapp.com
   ```
3. Verifica que "Return URLs" muestre:
   ```
   https://tu-proyecto.firebaseapp.com/__/auth/handler
   ```

### En Firebase Console:

1. Ve a Authentication → Sign-in method → Apple
2. Verifica que el "OAuth redirect domain" coincida:
   ```
   tu-proyecto.firebaseapp.com
   ```

### Deben Coincidir:

```
Apple Developer Console (Domains):     tu-proyecto.firebaseapp.com
                                       ↓
Firebase (OAuth redirect domain):      tu-proyecto.firebaseapp.com
                                       ↓
                                       ✅ COINCIDEN
```

---

## 🆘 Si Usas un Dominio Personalizado

### ⚠️ Configuración Avanzada

Si tienes un dominio personalizado como `www.miapp.com` configurado en Firebase Hosting:

1. **Primero** debes agregar el dominio en Firebase Hosting
2. **Luego** puedes usarlo en Apple Developer Console

**Pasos:**

1. Firebase Console → Hosting → Add custom domain
2. Verifica la propiedad del dominio
3. Configura los registros DNS
4. Una vez verificado, puedes usar:
   ```
   Domains and Subdomains: www.miapp.com
   Return URLs: https://www.miapp.com/__/auth/handler
   ```

**Pero para comenzar, usa siempre el dominio por defecto de Firebase:**
```
tu-proyecto.firebaseapp.com
```

---

## 🎯 Resumen Final

### Para el 99% de los casos:

**Domains and Subdomains:**
```
[TU-PROJECT-ID].firebaseapp.com
```

**Return URLs:**
```
https://[TU-PROJECT-ID].firebaseapp.com/__/auth/handler
```

### Cómo obtener TU-PROJECT-ID:

1. Ve a Firebase Console
2. Selecciona tu proyecto
3. Mira la URL del navegador
4. Busca el texto entre `/project/` y `/overview`

**Ejemplo:**
```
URL: https://console.firebase.google.com/project/mi-login-app/overview
                                                  ^^^^^^^^^^^^
                                                  ESTE ES TU ID
```

**Tu dominio:** `mi-login-app.firebaseapp.com`

---

**¿Todavía tienes dudas?** El dominio que necesitas es el que termina en `.firebaseapp.com` y está basado en el ID de tu proyecto de Firebase. ¡Así de simple! 🚀

