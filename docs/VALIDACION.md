# Validación del laboratorio Rick and Morty

Fecha: 2026-11-08, zona America/Lima. Variante: `species=Alien`.

## Compilación y pruebas ejecutadas

- Compilación desde limpio: `./gradlew clean :app:assembleDebug :app:assembleDebugAndroidTest :app:testDebugUnitTest :app:lintDebug` — correcta.
- Pruebas unitarias: 8 de `ListaViewModel`, 3 de `LoginViewModel` y 1 prueba de plantilla; todas correctas.
- Room en Android: las 5 pruebas pasaron, incluida la persistencia del favorito al cerrar y reabrir la base. Registro: `evidencias/room-pruebas.txt`.
- Recorrido real: `CatalogoRealJourneyTest#recorridoConCuentasReales` pasó completo en Medium_Phone (72,147 segundos). Registro: `evidencias/recorrido-pruebas.txt`.
- Cierre de sesión entre procesos: dos pruebas separadas aprobadas (8,594 y 1,748 segundos). Los PID distintos y el login visible se registran en `evidencias/procesos-sesion.json`.
- Las 14 pruebas de aceptación quedaron verificadas mediante el recorrido, las comprobaciones adicionales entre procesos y las pruebas de estados y persistencia.
- Lint: 0 errores. Hay advertencias de mantenimiento, versiones sugeridas y recursos heredados; no se declara que el análisis esté libre de advertencias.
- API real: 205 personajes Alien en 11 páginas al verificar. Esta cifra puede cambiar si el servicio actualiza su catálogo.
- Firebase real: ambas cuentas existentes aceptan sus credenciales y leen el rol esperado. La lectura del perfil ajeno y la escritura del rol propio devuelven HTTP 403. Evidencia sin credenciales: `evidencias/firebase-validacion.json`.

## Pruebas de aceptación

| Caso | Evidencia y estado |
| --- | --- |
| PA-01 Usuario sin Admin | Comprobado en el recorrido: login usuario, catálogo y ausencia del acceso Admin. |
| PA-02 Admin | Comprobado en el emulador: acceso al catálogo y panel con 205 guardados y 1 favorito. |
| PA-03 Contraseña incorrecta | Comprobado en el recorrido: error y sin acceso al catálogo. |
| PA-04 Campos vacíos | Comprobado en el recorrido y en prueba unitaria: validación sin llamar al repositorio de autenticación. |
| PA-05 Primera sincronización | Lista real y notificación comprobadas; estado Cargando cubierto en prueba unitaria. |
| PA-06 Detalle | Comprobado con personaje 6 y segundo endpoint; respaldo local cubierto por prueba Room. |
| PA-07 Favorito al cerrar | Comprobado en Room y en la app tras cerrar y reabrir su proceso: el favorito permanece en Favoritos. |
| PA-08 Sin red con datos | Comprobado en emulador con conexión deshabilitada y aviso de datos guardados. |
| PA-09 Sin red sin datos | Comprobado en el recorrido: sin caché ni conexión aparece Error con Reintentar; al recuperar conexión se descarga la lista. |
| PA-10 Sincronizar conserva favorito | Comprobado en UI y Room. |
| PA-11 Limpiar caché | Comprobado en UI y Room: caché vacía y totales en cero; el reintento recupera los datos. La regresión del error antiguo está cubierta. |
| PA-12 Cerrar sesión y reabrir | Comprobado en dos instrumentaciones separadas: PID 10236 cierra sesión y PID 10565 muestra el login. Evidencia: `evidencias/procesos-sesion.json`. |
| PA-13 Rotación | Comprobado en el recorrido: recreación y rotación del emulador conservan los datos y el favorito. |
| PA-14 Avisos denegados | Comprobado en el recorrido con avisos desactivados y, además, denegando POST_NOTIFICATIONS en el diálogo real: la sincronización termina y la app sigue funcionando. |

## Incidencias resueltas

1. Dependencia heredada de Fragment incompatible con Activity Result según lint. Se fijó una versión compatible y lint pasó.
2. Iconos oscuros sobre fondo oscuro en las barras del sistema. Se configuraron iconos claros y se verificaron visualmente.
3. Observadores repetidos al reintentar Favoritos y manejo de errores del flujo Room. Se corrigieron sin añadir dependencias Android a los ViewModel.
4. Limpiar caché después de un fallo de conexión retenía el error antiguo. Se corrigió la transición a lista vacía y se añadió una prueba de regresión.
5. Selector ambiguo Favoritos en el recorrido: se distinguió la pestaña con acción del contador Admin.

## Ejecución en el emulador

La primera sesión se interrumpió por cierres del emulador relacionados con EGL/Vulkan. Después de que la propietaria lo abrió de forma estable, se ejecutaron las cinco pruebas de Room y el recorrido completo con resultado correcto. Las ejecuciones fallidas previas no se contaron como aprobadas. Se corrigieron selectores ambiguos y una espera de sincronización en la prueba. La comprobación de cierre de sesión se ejecutó por separado en dos procesos distintos y también pasó. El reinicio completo del AVD restauró una instantánea anterior; por eso se verificó la persistencia entre procesos sin depender de esa instantánea.

No se ejecutan dos instrumentaciones simultáneas. El recorrido real necesita las cuatro credenciales mediante argumentos de ejecución y un emulador estable. Las contraseñas y tokens no se guardan en fuentes, capturas ni registros del repositorio.

## Historial y requisitos administrativos

- `main`: 17 commits en total, incluyendo el commit base conservado y los hitos H1–H16 en orden.
- `mejora-ia`: pruebas, correcciones y documentación de asistencia en `PROMPTS.md`.
- La autoría corresponde a la cuenta real configurada. No se simularon seis aportes de un segundo integrante.
- El JSON local de Firebase se retiró del seguimiento en H1. El commit base previo se conservó: no se reescribió el historial publicado.
- El repositorio se conserva público por instrucción explícita de su propietaria. Esta decisión difiere del requisito de repositorio privado de la consigna.
