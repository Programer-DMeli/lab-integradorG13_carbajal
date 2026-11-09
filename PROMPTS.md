# Registro de asistencia de IA
La rama `mejora-ia` incorpora las pruebas y correcciones posteriores.

## Consulta 1 Planificación previa

- Prompt: «Lee este archivo planning_rickandmorty y antes de escribir el código genérame una planificación de paso a paso». Se adjuntaron el documento de planificacion y la imagen con H1–H16.
- Respuesta: plan por hito, verificaciones y mensajes de commit; identificación del paquete, variante Alien y reglas MVVM.
- Qué se usó y cambió: se respetó H1–H16; se conservó el commit base existente y se añadió un ajuste de exclusiones. Las versiones se comprobaron mediante documentación y compilación.
- Qué aprendimos: una planificación debe separar requisitos, secuencia de implementación y evidencia necesaria para dar cada avance por terminado.

## Consulta 2 Implementación ordenada

- Prompt: «Ahora pasamos a la implementación. La secuencia queda así: revisión inicial → H1–H16 en orden → mejoras documentadas y pruebas → validación completa → entrega».
- Respuesta: implementación de Compose, contratos MVVM, Retrofit, Room, favoritos, modo sin conexión, Firebase Auth, roles, Admin y notificaciones, con compilación por hito.
- Qué se usó y cambió: la API devuelve `info/results` y requiere recorrer 11 páginas para los 205 Alien encontrados en la verificación. Se eligió el convertidor Gson de Retrofit para evitar añadir otro plugin al proyecto existente. Room conserva todos los campos de detalle. Las escrituras preservan favoritos y no publican descargas parciales.
- Qué aprendimos: un ejemplo genérico no puede copiarse sin adaptar el envoltorio, la paginación, los modelos y la compatibilidad de dependencias.

## Consulta 3 Emulador y Firebase

- Prompt: ejecutar el emulador y preparar Firestore desde la consola. Después se indicó una sesión autenticada en la consola del proyecto.
- Respuesta: arranque del emulador existente y configuración de Firestore `(default)` en modo de producción, perfiles por UID y reglas limitadas a la lectura del perfil propio.
- Qué se usó y cambió: las cuentas existentes de Authentication se conservaron; sus contraseñas y tokens no se incorporaron al repositorio. Se verificó la lectura de cada rol y el rechazo HTTP 403 al leer el perfil ajeno.
- Qué aprendimos: iniciar sesión en Authentication y disponer del perfil de rol en Firestore son pasos diferentes. La navegación por rol no sustituye las reglas de acceso.

## Consulta 4 Pruebas y revisión

- Prompt: continuación de la fase de mejoras documentadas y pruebas del plan aprobado.
- Respuesta: pruebas con repositorio y notificador falsos, pruebas de login, pruebas de repositorio con Room real y un recorrido instrumentado con cuentas inyectadas en ejecución.
- Qué se usó y cambió: se corrigió una incompatibilidad heredada de Fragment, el contraste de los iconos del sistema, el tratamiento de emisiones vacías después de errores y la creación de observadores repetidos en Favoritos. Un selector ambiguo en la prueba se corrigió para distinguir la pestaña Favoritos del contador Admin.
- Qué aprendimos: compilar no acredita el comportamiento. Las pruebas unitarias controlan estados y reintentos; las instrumentadas verifican persistencia, interacción y capacidades Android. Las capturas acompañan la evidencia y no reemplazan las aserciones.

## Verificación humana y sustentación

Revisar las interfaces `ItemRepository`, `AuthRepository` y `Notificador`, los mappers, la transacción que conserva favoritos y los estados de error. Los resultados comprobados y las limitaciones se registran en `docs/VALIDACION.md`. Ninguna afirmación de «100 % libre de errores» se deduce de las pruebas ejecutadas.

## Consulta 5 Cierre de la validación

- Prompt: «Ya está abierto y se mantiene estable» para continuar en Medium_Phone; «Conservarlo público» para la visibilidad del repositorio.
- Respuesta: ejecución de las cinco pruebas de Room y del recorrido real completo, seguida de comprobaciones entre procesos y del permiso de notificaciones denegado desde el diálogo del sistema.
- Qué se usó y cambió: la prueba espera el final de la sincronización inicial antes de pulsar Sincronizar y guarda una captura de diagnóstico si falla. Se actualizaron las capturas reales y las evidencias. Se mantuvo la visibilidad pública solicitada.
- Qué aprendimos: las pruebas UI deben esperar el estado que habilita la acción; encontrar datos en caché no significa que haya terminado la actualización en segundo plano. El cierre de sesión se confirma en pantalla antes de detener el proceso. Dos instrumentaciones con PID distintos comprobaron que al reabrir se mantiene el login.
