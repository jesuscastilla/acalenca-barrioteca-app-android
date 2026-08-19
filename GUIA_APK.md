# Guía completa — App Android (Trusted Web Activity) · Barrioteca Acalencá

Esta guía explica, paso a paso y para quien no ha usado Android Studio, cómo trabajar con la app Android: abrir el proyecto, compilar el APK/AAB, subir lo necesario al NAS y publicar en Google Play.

---

## 1. Qué es esta app

La app Android es una **Trusted Web Activity (TWA)**: una app nativa muy ligera que abre la PWA (`https://pelotxo.synology.me/barrioteca/`) dentro de Chrome a pantalla completa, como si fuera una app normal de la tienda.

- **Ventaja principal**: al actualizar la PWA en el NAS, la app se actualiza **sola** (no hay que publicar una versión nueva en Google Play).
- **Qué NO es**: no es una copia del código dentro del APK. La app necesita conexión a internet para cargar la PWA desde el NAS.

### Datos clave

| Dato | Valor |
|---|---|
| Package ID | `barrioteca.app.pelotxo` |
| Nombre | Barrioteca Acalencá |
| minSdk | 28 (Android 9 o superior) |
| targetSdk | 36 |
| versionCode / versionName | 1 / 1.0 |
| URL que carga | `https://pelotxo.synology.me/barrioteca/` |
| Firma | `signing.keystore` (alias `my-key-alias`) |
| Icono | Símbolo de "acalencá" sin letras, fondo blanco |

---

## 2. Requisitos

- **Android Studio** (ya instalado en `C:\Program Files\Android\Android Studio`).
- **JDK**: Android Studio trae el suyo (JBR 21) en `C:\Program Files\Android\Android Studio\jbr`.
- **Android SDK** en `C:\Users\jesus\AppData\Local\Android\Sdk`.
- **Conexión a internet** (la primera compilación descarga dependencias).

---

## 3. Estructura del proyecto

```
barrioteca-android-app/
├── app/
│   ├── build.gradle              # package, minSdk, firma, colores, versiones
│   ├── proguard-rules.pro
│   └── src/main/
│       ├── AndroidManifest.xml   # declaración de la TWA
│       ├── assets/assetlinks.json
│       ├── java/barrioteca/app/pelotxo/
│       │   ├── LauncherActivity.java
│       │   ├── Application.java
│       │   └── DelegationService.java
│       └── res/
│           ├── drawable/splash.png
│           ├── mipmap-*/ic_launcher.png            (icono legacy)
│           ├── mipmap-*/ic_launcher_foreground.png (icono adaptativo)
│           ├── mipmap-anydpi-v26/ic_launcher.xml
│           ├── values/  (colors.xml, strings.xml)
│           └── xml/     (filepaths.xml, shortcuts.xml)
├── build.gradle
├── settings.gradle
├── gradle.properties
├── gradlew / gradlew.bat         # Wrapper de Gradle
├── keystore.properties           # credenciales de firma (NO subir a Git)
├── local.properties              # ruta del SDK (NO subir a Git)
├── signing.keystore              # clave de firma (NO subir a Git)
└── assetlinks.json               # Digital Asset Links (CRÍTICO para Play)
```

---

## 4. Abrir el proyecto en Android Studio

1. Abre **Android Studio**.
2. Pulsa **Open** y selecciona la carpeta `G:\GITHUB\barrioteca-android-app\`.
3. Espera a que Gradle sincronice (la primera vez descarga dependencias; puede tardar varios minutos).
4. Si pide aceptar licencias del SDK, acéptalas.

---

## 5. Firma (keystore)

La app se firma con `signing.keystore`. Las credenciales están en `keystore.properties`:

```properties
storeFile=signing.keystore
storePassword=UP1dTspqbkg7
keyAlias=my-key-alias
keyPassword=UP1dTspqbkg7
```

> **MUY IMPORTANTE**: no pierdas `signing.keystore`. Es la única forma de actualizar la app en Google Play. Guarda una copia de seguridad fuera del repositorio.

---

## 6. Compilar

### Opción A — Línea de comandos (recomendada)

Abre PowerShell y ejecuta:

```powershell
cd G:\GITHUB\barrioteca-android-app
$env:JAVA_HOME='C:\Program Files\Android\Android Studio\jbr'
$env:ANDROID_HOME='C:\Users\jesus\AppData\Local\Android\Sdk'
.\gradlew.bat :app:bundleRelease :app:assembleRelease
```

Resultados:

- **AAB** (para Google Play): `app\build\outputs\bundle\release\app-release.aab`
- **APK** (para instalar directamente en un móvil): `app\build\outputs\apk\release\app-release.apk`

### Opción B — Android Studio (interfaz gráfica)

1. Menú **Build → Generate Signed App Bundle / APK**.
2. Elige **Android App Bundle** (para Google Play).
3. En *Key store path* selecciona `signing.keystore` (raíz del proyecto).
4. Alias `my-key-alias` y las contraseñas de `keystore.properties`.
5. Modo **release** → Create. El AAB se genera en `app\release\`.

## 7. Iconos

El icono es el símbolo de "acalencá" **sin letras**, en fondo blanco.

- **Icono maestro**: `G:\GITHUB\LOGOS\acalenca-icono.png` (1024×1024).
- Los mipmaps del proyecto se generan a partir de ese archivo.

### Regenerar los iconos si cambias el logo

```powershell
powershell -NoProfile -ExecutionPolicy Bypass -File G:\GITHUB\_tmp_bubblewrap\regenerar-mipmaps.ps1
```

Y después vuelve a compilar (sección 6).

> Todos los logos derivados se guardan en `G:\GITHUB\LOGOS\`.

---

## 8. Subir al NAS

La app Android **no se sube al NAS** (el APK/AAB va a Google Play o a los móviles). Lo único que hay que subir al NAS es el **`assetlinks.json`**, necesario para que Google Play valide la TWA:

1. Entra en el NAS (DSM) y abre **File Station** (o la carpeta web `/volume1/web/`).
2. Crea la carpeta `.well-known` en la raíz web del dominio (si no existe).
3. Sube `assetlinks.json` (está en la raíz de `barrioteca-android-app/`) a `/volume1/web/.well-known/assetlinks.json`.
4. Verifica en el navegador que responde:
   `https://pelotxo.synology.me/.well-known/assetlinks.json`
   Debe devolver el JSON (no un 404).

> Si el dominio `pelotxo.synology.me` apunta a una subcarpeta concreta en Web Station, coloca `.well-known/` dentro de esa carpeta.

### ¿Y la PWA?

La PWA se sigue subiendo como siempre (ver `CONTEXT.md`): `PWA/dist/` → `/barrioteca/`. La app Android cargará esa versión automáticamente. No hay que tocar el APK.

---

## 9. Publicar en Google Play (paso a paso)

1. Crea una **cuenta de desarrollador** en [play.google.com/console](https://play.google.com/console) (25 USD, una sola vez).
2. Google pedirá **verificar tu identidad** (DNI/documento). Hazlo.
3. **Crear app** → nombre "Barrioteca Acalencá", idioma español.
4. Completa la ficha: descripción, capturas de pantalla, **icono 512×512** (usa `LOGOS/acalenca-icono.png`) y clasificación de contenido (cuestionario).
5. **Política de privacidad**: necesitas una URL pública. Si no la tienes, crea una página en el NAS, p. ej. `https://pelotxo.synology.me/barrioteca/privacidad.html`, y pon esa URL.
6. **Producción → Crear release** → sube el **AAB** (`app-release.aab`).
7. **Antes de subir**, confirma que `https://pelotxo.synology.me/.well-known/assetlinks.json` responde correctamente (Google lo comprueba y rechaza la app si no valida).
8. Envía a revisión. La aprobación puede tardar desde horas hasta varios días.

---

## 10. Actualizar la app

- **Si solo cambias la PWA (frontend)**: sube `PWA/dist/` al NAS. La app se actualiza sola. **No hace falta publicar una versión nueva en Play.**
- **Si cambias la app Android** (icono, permisos, package, etc.): sube `versionCode` (y `versionName`) en `app/build.gradle`, recompila y sube un nuevo AAB a Google Play.

---

## 11. Problemas comunes

| Problema | Solución |
|---|---|
| Play rechaza: "enlace no verificado" | Falta `assetlinks.json` en el NAS (sección 8). |
| La app abre Chrome en pestaña (no a pantalla completa) | El `assetlinks.json` no valida; revisa que el SHA256 coincida con el keystore. |
| Error de firma al compilar | Revisa `keystore.properties` (ruta y contraseñas). |
| No se ve el icono nuevo | Regenera mipmaps (sección 7) y recompila. |
| Gradle tarda mucho la primera vez | Normal: descarga dependencias una sola vez. |
| "Gradle sync failed" al abrir Android Studio | Comprueba `local.properties` (ruta del SDK) e internet. |

---

## 12. Comandos útiles (resumen)

```powershell
# Regenerar iconos desde LOGOS/acalenca-icono.png
powershell -NoProfile -ExecutionPolicy Bypass -File G:\GITHUB\_tmp_bubblewrap\regenerar-mipmaps.ps1

# Compilar AAB (Google Play) y APK (instalación directa)
cd G:\GITHUB\barrioteca-android-app
$env:JAVA_HOME='C:\Program Files\Android\Android Studio\jbr'
$env:ANDROID_HOME='C:\Users\jesus\AppData\Local\Android\Sdk'
.\gradlew.bat :app:bundleRelease :app:assembleRelease

# Verificar la firma del APK
& 'C:\Users\jesus\AppData\Local\Android\Sdk\build-tools\35.0.0\apksigner.bat' verify --print-certs app\build\outputs\apk\release\app-release.apk
```

