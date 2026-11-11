package com.carbajal.rickandmorty.ui.components

import androidx.compose.foundation.layout.*
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.semantics.LiveRegionMode
import androidx.compose.ui.semantics.liveRegion
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.unit.dp
import com.carbajal.rickandmorty.domain.connectivity.EstadoConexion
import com.carbajal.rickandmorty.ui.UiState

@Composable
fun IndicadorConexion(estado: UiState<EstadoConexion>, conSesion: Boolean) {
    val sinConexion = (estado as? UiState.Exito)?.datos == EstadoConexion.SIN_CONEXION
    val titulo = when (estado) {
        UiState.Cargando -> "Comprobando conexión…"
        is UiState.Error -> "Conexión sin verificar"
        is UiState.Exito -> if (sinConexion) "Modo sin conexión" else "En línea"
    }
    Surface(
        color = if (sinConexion) MaterialTheme.colorScheme.secondaryContainer else MaterialTheme.colorScheme.surface,
        modifier = Modifier.fillMaxWidth().semantics { liveRegion = LiveRegionMode.Polite },
    ) {
        Row(Modifier.padding(horizontal = 20.dp, vertical = 8.dp),
            verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(10.dp)) {
            Text(if (sinConexion) "○" else "●", color = MaterialTheme.colorScheme.primary)
            Column {
                Text(titulo, style = MaterialTheme.typography.labelLarge)
                if (sinConexion) Text(
                    if (conSesion) "Puedes consultar tus datos guardados y favoritos."
                    else "Iniciar sesión requiere conexión a internet.",
                    style = MaterialTheme.typography.bodySmall,
                )
            }
        }
    }
}
