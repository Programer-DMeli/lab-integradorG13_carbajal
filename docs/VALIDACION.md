# Validación del laboratorio Rick and Morty

Fecha: 2026-11-08, zona America/Lima. Variante: `species=Alien`.

## Compilación y pruebas ejecutadas

- Compilación desde limpio: `./gradlew clean :app:assembleDebug :app:assembleDebugAndroidTest :app:testDebugUnitTest :app:lintDebug` — correcta.
- Pruebas unitarias: 8 de `ListaViewModel`, 3 de `LoginViewModel` y 1 prueba de plantilla; todas correctas.
- Room en Android: 4 pruebas ejecutadas correctamente (paginación, favoritos al sincronizar, atomicidad frente a descarga parcial, detalle con respaldo local y limpieza de totales). Se añadió además una quinta prueba de cierre/reapertura de la base: compila y su ejecución final queda pendiente por la interrupción del emulador.
- Lint: 0 errores. Hay advertencias de mantenimiento, versiones sugeridas y recursos heredados; no se declara que el análisis esté libre de advertencias.
- API real: 205 personajes Alien en 11 páginas al verificar. Esta cifra puede cambiar si el servicio actualiza su catálogo.
- Firebase real: ambas cuentas existentes aceptan sus credenciales y leen el rol esperado. La lectura del perfil ajeno y la escritura del rol propio devuelven HTTP 403. Evidencia sin credenciales: `evidencias/firebase-validacion.json`.

## Pruebas de aceptación

| Caso | Evidencia y estado |
| --- | --- |
| PA-01 Usuario sin Admin | Login y rol usuario comprobados por API; navegación del usuario pendiente de cerrar en el recorrido completo. |
| PA-02 Admin | Comprobado en el emulador: acceso al catálogo y panel con 205 guardados y 1 favorito. |
| PA-03 Contraseña incorrecta | Comprobado en el recorrido: error y sin acceso al catálogo. |
| PA-04 Campos vacíos | Comprobado en el recorrido y en prueba unitaria: validación sin llamar al repositorio de autenticación. |
| PA-05 Primera sincronización | Lista real y notificación comprobadas; estado Cargando cubierto en prueba unitaria. |
| PA-06 Detalle | Comprobado con personaje 6 y segundo endpoint; respaldo local cubierto por prueba Room. |
| PA-07 Favorito al cerrar | Favorito conservado al recrear Activity y al sincronizar; verificación entre procesos y quinta prueba Room pendientes. |
| PA-08 Sin red con datos | Comprobado en emulador con conexión deshabilitada y aviso de datos guardados. |
| PA-09 Sin red sin datos | Error y reintento cubiertos en pruebas unitarias; ejecución final del escenario UI pendiente. |
| PA-10 Sincronizar conserva favorito | Comprobado en UI y Room. |
| PA-11 Limpiar caché | Totales en cero comprobados con Room. Se detectó y corrigió un estado de error antiguo; regresión unitaria aprobada, repetición UI pendiente. |
| PA-12 Cerrar sesión y reabrir | Lógica cubierta en prueba unitaria; cierre y reapertura reales pendientes en el recorrido completo. |
| PA-13 Rotación | Recreación de Activity con favoritos comprobada; rotación física del emulador pendiente de ejecución final. |
| PA-14 Avisos denegados | Implementado el control de permiso y notificaciones desactivadas; comprobación UI final pendiente. |

## Incidencias resueltas

1. Dependencia heredada de Fragment incompatible con Activity Result según lint. Se fijó una versión compatible y lint pasó.
2. Iconos oscuros sobre fondo oscuro en las barras del sistema. Se configuraron iconos claros y se verificaron visualmente.
3. Observadores repetidos al reintentar Favoritos y manejo de errores del flujo Room. Se corrigieron sin añadir dependencias Android a los ViewModel.
4. Limpiar caché después de un fallo de conexión retenía el error antiguo. Se corrigió la transición a lista vacía y se añadió una prueba de regresión.
5. Selector ambiguo Favoritos en el recorrido: se distinguió la pestaña con acción del contador Admin.

## Límite de la validación en el emulador

`Medium_Phone` consiguió iniciar y ejecutar parte del recorrido. Luego terminó por un fallo gráfico de EGL/Vulkan; los intentos de arranque posteriores no se mantuvieron disponibles en ADB. Se probaron arranque sin instantánea y renderizado por software. Los cierres del emulador no se cuentan como pruebas aprobadas de la aplicación.

No se ejecutan dos instrumentaciones simultáneas. El recorrido real necesita las cuatro credenciales mediante argumentos de ejecución y un emulador estable. Las contraseñas y tokens no se guardan en fuentes, capturas ni registros del repositorio.

## Historial y requisitos administrativos

- `main`: 17 commits en total, incluyendo el commit base conservado y los hitos H1–H16 en orden.
- `mejora-ia`: pruebas, correcciones y documentación de asistencia en `PROMPTS.md`.
- La autoría corresponde a la cuenta real configurada. No se simularon seis aportes de un segundo integrante.
- El JSON local de Firebase se retiró del seguimiento en H1. El commit base previo se conservó: no se reescribió el historial publicado.
- La visibilidad del repositorio se revisa con la persona propietaria, ya que inicialmente era público y la consigna pide privado.
