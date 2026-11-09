# Modo sin conexión visible

El indicador superior está presente en el login, catálogo, detalle, favoritos y Admin. Cambia automáticamente entre «En línea» y «Modo sin conexión» al detectar cambios de la red. No hace falta pulsar Sincronizar para que cambie el indicador.

## Cómo funciona el almacenamiento

- La lista y los favoritos se leen desde Room, incluso cuando hay internet.
- Retrofit descarga los personajes y actualiza Room al sincronizar. Las marcas de favorito se conservan.
- Sin internet, la lista, los favoritos y los detalles descargados anteriormente siguen disponibles. Las imágenes necesitan estar en la caché de Coil; una imagen que no se había cargado puede no aparecer sin red.
- Si no hay personajes almacenados y la descarga falla, se muestra Error con Reintentar.
- La primera autenticación necesita internet. Para volver a abrir una sesión existente sin red, Firebase también debe disponer del perfil de rol en su caché.
- Recuperar internet cambia el indicador. Para descargar cambios, se usa Sincronizar o Reintentar.

Antes de este cambio, «Sin conexión: mostrando datos guardados» aparecía únicamente después de una descarga fallida con datos locales. Ahora el indicador refleja la conexión actual y el mensaje «Último intento sin conexión: mostrando datos guardados» describe el resultado de la última descarga.

## Comprobación manual

1. Ingresar y sincronizar con internet. Debe aparecer «En línea».
2. Apagar Wi-Fi y datos móviles, manteniendo la sesión abierta. Debe aparecer «Modo sin conexión» sin pulsar Sincronizar.
3. Consultar la lista, abrir un detalle descargado y visitar Favoritos. El indicador debe seguir visible.
4. Reabrir la app y comprobar los datos guardados.
5. Recuperar internet. Debe reaparecer «En línea»; pulsar Sincronizar para actualizar.

## Validación de este cambio

Compilación, pruebas unitarias y lint completados correctamente. Se añadió `ConexionVisibleTest`, que comprueba desconexión, recreación de Activity y reconexión sin pulsar Sincronizar. Su ejecución en dispositivo queda para la comprobación manual solicitada por la propietaria; no se cambió la conectividad del teléfono durante este trabajo.

## Instalación en el teléfono conectado

La actualización directa fue rechazada por Android con `INSTALL_FAILED_UPDATE_INCOMPATIBLE`: la instalación existente usa otra firma. Para conservar los datos hace falta firmar con la clave original. La alternativa es desinstalar la app existente, lo cual borra su sesión, caché y favoritos; ese reemplazo requiere la autorización de su propietaria.

La propietaria eligió conservar la instalación actual y recibir el APK para su comprobación manual. No se desinstaló ni se reemplazó la app del teléfono.
