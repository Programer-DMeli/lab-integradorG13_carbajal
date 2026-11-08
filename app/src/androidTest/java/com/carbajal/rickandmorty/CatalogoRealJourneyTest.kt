package com.carbajal.rickandmorty

import androidx.compose.ui.test.*
import androidx.compose.ui.test.junit4.createAndroidComposeRule
import androidx.test.platform.app.InstrumentationRegistry
import androidx.test.uiautomator.*
import org.junit.Assert.*
import org.junit.Assume.assumeTrue
import org.junit.Rule
import org.junit.Test
import java.io.File

class CatalogoRealJourneyTest {
    @get:Rule val compose = createAndroidComposeRule<MainActivity>()
    private val device get() = UiDevice.getInstance(InstrumentationRegistry.getInstrumentation())
    private fun texto(t: String) = if (t == "Favoritos") compose.onNode(hasText(t) and hasClickAction()) else compose.onNodeWithText(t, useUnmergedTree = false)
    private fun esperar(t: String, timeout: Long = 60000) {
        compose.waitUntil(timeout) { compose.onAllNodesWithText(t).fetchSemanticsNodes().isNotEmpty() }
    }
    private fun foto(nombre: String) {
        compose.waitForIdle()
        if (nombre == "lista" || nombre == "detalle") Thread.sleep(3000)
        val dir = File(InstrumentationRegistry.getInstrumentation().targetContext.getExternalFilesDir(null), "evidencias").apply { mkdirs() }
        assertTrue(device.takeScreenshot(File(dir,"$nombre.png")))
    }
    private fun entrar(correo: String, clave: String) {
        esperar("Entrar al portal")
        compose.onNodeWithTag("correo").performTextReplacement(correo)
        compose.onNodeWithTag("clave").performTextReplacement(clave)
        texto("Entrar al portal").performScrollTo().performClick()
    }
    @Test fun recorridoConCuentasReales() {
        val args = InstrumentationRegistry.getArguments()
        val adminCorreo = args.getString("adminCorreo").orEmpty()
        val adminClave = args.getString("adminClave").orEmpty()
        val usuarioCorreo = args.getString("usuarioCorreo").orEmpty()
        val usuarioClave = args.getString("usuarioClave").orEmpty()
        assumeTrue("Se requieren cuentas inyectadas fuera del repositorio",listOf(adminCorreo,adminClave,usuarioCorreo,usuarioClave).all { it.isNotEmpty() })
        device.executeShellCommand("pm grant com.carbajal.rickandmorty android.permission.POST_NOTIFICATIONS")
        try {
            println("Avance de aceptación")
            // PA-04 y PA-03: validación y credenciales rechazadas.
            esperar("Entrar al portal"); foto("login")
            texto("Entrar al portal").performClick()
            esperar("Completa el correo y la contraseña.")
            entrar(adminCorreo, "incorrecta-para-prueba")
            esperar("No se pudo ingresar. Revisa tus credenciales, la conexión y el rol en Firestore.")
            println("Avance de aceptación")
            // PA-02, PA-05, PA-06 y PA-07.
            entrar(adminCorreo,adminClave)
            esperar("Abadango Cluster Princess",120000)
            texto("Admin").assertExists(); foto("lista")
            texto("Abadango Cluster Princess").performClick()
            esperar("ARCHIVO #6"); foto("detalle")
            texto("← Volver al catálogo").performClick()
            compose.onNodeWithContentDescription("Agregar Abadango Cluster Princess a favoritos").performClick()
            texto("Favoritos").performClick(); esperar("Abadango Cluster Princess"); foto("favoritos")
            println("Avance de aceptación")
            // PA-13: recrear conserva datos y favoritos.
            compose.activityRule.scenario.recreate()
            esperar("Abadango Cluster Princess")
            texto("Catálogo").performClick()
            compose.onNodeWithContentDescription("Quitar Abadango Cluster Princess de favoritos").assertExists()
            texto("Admin").performClick(); esperar("Control del portal"); foto("admin")
            texto("Sincronizar").performClick(); esperar("Se guardaron 205 elementos",120000)
            println("Avance de aceptación")
            // PA-10: la sincronización conserva favoritos.
            texto("Favoritos").performClick(); esperar("Abadango Cluster Princess")
            device.openNotification()
            assertTrue(device.wait(Until.hasObject(By.textContains("Se guardaron")),10000))
            val dir = File(InstrumentationRegistry.getInstrumentation().targetContext.getExternalFilesDir(null),"evidencias")
            assertTrue(device.takeScreenshot(File(dir,"notificacion.png")))
            device.findObject(By.textContains("Se guardaron")).click()
            esperar("Cerrar sesión")
            println("Avance de aceptación")
            // PA-08: red apagada con caché.
            device.executeShellCommand("svc wifi disable")
            device.executeShellCommand("svc data disable")
            texto("Catálogo").performClick(); texto("Sincronizar personajes").performClick()
            esperar("Sin conexión: mostrando datos guardados",60000); foto("sin-conexion")
            println("Avance de aceptación")
            // PA-11 y PA-09: limpiar y volver a sincronizar sin datos ni red.
            texto("Admin").performClick(); texto("Limpiar caché").performClick(); texto("Limpiar").performClick()
            esperar("Caché y favoritos eliminados de este dispositivo")
            texto("Catálogo").performClick(); esperar("No hay personajes guardados")
            texto("Sincronizar personajes").performClick(); esperar("Reintentar",60000)
            device.executeShellCommand("svc wifi enable")
            device.executeShellCommand("svc data enable")
            Thread.sleep(8000)
            texto("Reintentar").performClick(); esperar("Abadango Cluster Princess",120000)
            println("Avance de aceptación")
            // PA-12 y PA-01: salir y entrar como usuario sin acceso Admin.
            texto("Cerrar sesión").performClick(); esperar("Entrar al portal")
            compose.activityRule.scenario.recreate(); esperar("Entrar al portal")
            entrar(usuarioCorreo,usuarioClave); esperar("Abadango Cluster Princess",120000)
            texto("Admin").assertDoesNotExist(); foto("usuario")
            println("Avance de aceptación")
            // PA-14: la sincronización funciona aunque el permiso se revoque.
            device.executeShellCommand("appops set com.carbajal.rickandmorty POST_NOTIFICATION ignore")
            texto("Sincronizar personajes").performClick(); esperar("Sincronizar personajes",120000)
            texto("Abadango Cluster Princess").assertExists()
            compose.onNodeWithContentDescription("Agregar Abadango Cluster Princess a favoritos").performClick()
            device.setOrientationLeft()
            esperar("Abadango Cluster Princess")
            compose.onNodeWithContentDescription("Quitar Abadango Cluster Princess de favoritos").assertExists()
            device.setOrientationNatural()
        } finally {
            device.setOrientationNatural()
            device.executeShellCommand("appops set com.carbajal.rickandmorty POST_NOTIFICATION default")
            device.executeShellCommand("svc wifi enable")
            device.executeShellCommand("svc data enable")
        }
    }
}
