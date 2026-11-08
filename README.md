# Rick and Morty Alien

Aplicación Android del laboratorio integrador G13. Catálogo de personajes de Rick and Morty con filtro `species=Alien`, Jetpack Compose, MVVM, Retrofit, Room y Firebase Authentication con roles en Firestore.

## Preparar y ejecutar

1. Abrir el proyecto en Android Studio y usar el JDK configurado por `gradle/gradle-daemon-jvm.properties`. El wrapper administra Gradle.
2. Instalar Android SDK 35 y disponer de un emulador con Google Play o un dispositivo Android 7.0 o posterior.
3. Configurar `sdk.dir` en `local.properties` (archivo local excluido de Git).
4. Registrar en Firebase la app con `applicationId` **com.carbajal.rickandmorty**. Descargar su `google-services.json` a `app/`; no agregarlo a Git.
5. Activar Authentication → Correo electrónico/contraseña y crear las dos cuentas de prueba. No hay registro desde la app.
6. Crear Firestore Database `(default)`. Para cada cuenta, usar su UID exacto como ID de documento en `usuarios/{uid}`, con los campos de texto `rol` (`admin` o `usuario`) y `nombre`.
7. Publicar las reglas de `firebase/firestore.rules`: cada cuenta solo puede leer su propio perfil; las escrituras de clientes están bloqueadas.
8. Compilar e instalar:

```bash
./gradlew :app:assembleDebug
./gradlew :app:installDebug
```

APK generado: `app/build/outputs/apk/debug/app-debug.apk`. Las contraseñas de prueba se comparten fuera del repositorio.

## Uso

- Ingresar con una cuenta asignada. El rol se obtiene de Firestore, nunca del correo ni de una selección en pantalla.
- Catálogo: sincronizar, consultar un personaje y marcar o desmarcar la estrella.
- Favoritos: consultar la colección persistida en el dispositivo.
- Admin: ver los totales, sincronizar y limpiar la caché. La limpieza también borra los favoritos locales y pide confirmación.
- La sesión se conserva al reabrir; Cerrar sesión regresa al login.
- Sin conexión se conserva el catálogo con un aviso. Sin datos locales aparece error con Reintentar. Los detalles previamente descargados también tienen respaldo local.
- La sincronización exitosa solicita una notificación «Se guardaron N elementos». En Android 13 o posterior se solicita permiso después del ingreso; rechazarlo no impide usar la app.

## Arquitectura

Paquete raíz: `com.carbajal.rickandmorty`.

| Capa | Responsabilidad |
| --- | --- |
| `domain/model` | `Item`, `Sesion`, `Rol` |
| `data/remote` | Dos endpoints, DTO y cliente Retrofit |
| `data/local` | Entity, DAO, base Room y esquema versionado |
| `data/mapper` | Conversiones DTO, Entity y modelo de UI |
| `data/repository` | Contrato e implementación; Room es fuente de verdad |
| `data/auth` | Contrato de autenticación, Firebase Auth y lectura de roles |
| `ui` | Pantallas Compose, ViewModel y `StateFlow<UiState<T>>` |
| `notification` | Contrato e implementación Android de notificaciones |
| `AppContainer` | Inyección manual y `ViewModelProvider.Factory` |

La lista de la API tiene envoltorio `info/results`. La sincronización recorre todas las páginas conservando `species=Alien`; solo guarda después de completar la descarga. La escritura transaccional preserva los favoritos. La lista visible se observa desde Room. El detalle consulta el segundo endpoint `/character/{id}` y utiliza el registro local si la consulta falla.

Referencias: [API Rick and Morty](https://rickandmortyapi.com/documentation), [Room](https://developer.android.com/jetpack/androidx/releases/room), [Firebase Android](https://firebase.google.com/docs/android/setup).

## Historial y asistencia de IA

Se conservó el commit base existente y se implementaron H1–H16 en orden, con commits independientes por avance. El desarrollo asistido por Codex se documenta en `PROMPTS.md`; no se presenta como una fase realizada sin IA. La rama `mejora-ia` se destina a pruebas y correcciones posteriores.

La autoría del historial es la cuenta Git configurada por la persona que realiza cada cambio. No se simulan aportes de un segundo integrante. El reparto y los datos del segundo integrante deben completarse con sus aportes reales.

## Validación y evidencias

Los hitos H1–H15 compilaron antes de su commit. Firebase se configuró en la consola del proyecto existente. La fase siguiente incorpora las pruebas de ViewModel y persistencia, las capturas y el registro verificable de las 14 pruebas de aceptación. La compilación por sí sola no acredita esas pruebas funcionales.
