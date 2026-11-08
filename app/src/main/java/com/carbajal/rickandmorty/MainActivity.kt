package com.carbajal.rickandmorty

import android.os.Bundle
import android.os.Build
import android.Manifest
import android.content.pm.PackageManager
import androidx.activity.result.contract.ActivityResultContracts
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.ui.Modifier
import com.carbajal.rickandmorty.ui.navigation.AppNav
import com.carbajal.rickandmorty.ui.theme.RickandmortyTheme

class MainActivity : ComponentActivity() {
    private val container get() = (application as CatalogoApplication).container
    private val permiso = registerForActivityResult(ActivityResultContracts.RequestPermission()) { }
    private fun pedirNotificaciones() {
        if (Build.VERSION.SDK_INT >= 33 && checkSelfPermission(Manifest.permission.POST_NOTIFICATIONS) != PackageManager.PERMISSION_GRANTED) {
            val preferencias = getPreferences(MODE_PRIVATE)
            if (!preferencias.getBoolean("notificaciones_solicitadas", false)) {
                preferencias.edit().putBoolean("notificaciones_solicitadas", true).apply()
                permiso.launch(Manifest.permission.POST_NOTIFICATIONS)
            }
        }
    }
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            RickandmortyTheme {
                Scaffold { padding -> Box(Modifier.padding(padding)) { AppNav(container, ::pedirNotificaciones) } }
            }
        }
    }
}
