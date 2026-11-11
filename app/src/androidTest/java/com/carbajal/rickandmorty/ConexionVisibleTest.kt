package com.carbajal.rickandmorty

import androidx.compose.ui.test.junit4.createAndroidComposeRule
import androidx.compose.ui.test.onAllNodesWithText
import androidx.test.platform.app.InstrumentationRegistry
import androidx.test.uiautomator.UiDevice
import org.junit.Assert.assertTrue
import org.junit.Rule
import org.junit.Test
import java.io.File

class ConexionVisibleTest {
    @get:Rule val compose = createAndroidComposeRule<MainActivity>()
    private val device get() = UiDevice.getInstance(InstrumentationRegistry.getInstrumentation())

    private fun esperar(texto: String) {
        compose.waitUntil(60_000) {
            runCatching { compose.onAllNodesWithText(texto).fetchSemanticsNodes().isNotEmpty() }.getOrDefault(false)
        }
    }

    @Test fun indicadorCambiaSinPulsarSincronizarYSobreviveRecreacion() {
        val wifi = device.executeShellCommand("settings get global wifi_on").trim() == "1"
        val datos = device.executeShellCommand("settings get global mobile_data").trim() == "1"
        try {
            device.executeShellCommand("svc wifi enable")
            esperar("En línea")
            device.executeShellCommand("svc wifi disable")
            device.executeShellCommand("svc data disable")
            esperar("Modo sin conexión")
            compose.activityRule.scenario.recreate()
            esperar("Modo sin conexión")
            val destino = File(InstrumentationRegistry.getInstrumentation().targetContext.getExternalFilesDir(null), "conexion-visible.png")
            assertTrue(device.takeScreenshot(destino))
            device.executeShellCommand("svc wifi enable")
            esperar("En línea")
        } finally {
            device.executeShellCommand(if (wifi) "svc wifi enable" else "svc wifi disable")
            device.executeShellCommand(if (datos) "svc data enable" else "svc data disable")
        }
    }
}
