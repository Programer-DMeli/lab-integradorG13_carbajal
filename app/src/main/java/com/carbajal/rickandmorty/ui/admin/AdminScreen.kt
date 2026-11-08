package com.carbajal.rickandmorty.ui.admin

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.carbajal.rickandmorty.ui.UiState
import com.carbajal.rickandmorty.ui.components.*

@Composable
fun AdminScreen(estado: UiState<ResumenAdmin>, ocupado: Boolean, aviso: String?,
    sincronizar: () -> Unit, limpiar: () -> Unit) {
    var confirmar by remember { mutableStateOf(false) }
    when (estado) {
        UiState.Cargando -> Cargando()
        is UiState.Error -> ErrorView(estado.mensaje, sincronizar)
        is UiState.Exito -> Column(Modifier.fillMaxSize().verticalScroll(rememberScrollState()).padding(20.dp), verticalArrangement = Arrangement.spacedBy(20.dp)) {
            Text("Control del portal", style = MaterialTheme.typography.headlineMedium)
            Text("Administra los datos guardados en este dispositivo.", color = MaterialTheme.colorScheme.onSurfaceVariant)
            Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                Card(Modifier.weight(1f)) { Column(Modifier.padding(20.dp)) {
                    Text("${estado.datos.guardados}", style = MaterialTheme.typography.displaySmall, color = MaterialTheme.colorScheme.primary)
                    Text("Guardados")
                } }
                Card(Modifier.weight(1f)) { Column(Modifier.padding(20.dp)) {
                    Text("${estado.datos.favoritos}", style = MaterialTheme.typography.displaySmall, color = MaterialTheme.colorScheme.primary)
                    Text("Favoritos")
                } }
            }
            Button(onClick = sincronizar, enabled = !ocupado, modifier = Modifier.fillMaxWidth()) { Text(if (ocupado) "Procesando…" else "Sincronizar") }
            OutlinedButton(onClick = { confirmar = true }, enabled = !ocupado, modifier = Modifier.fillMaxWidth()) { Text("Limpiar caché") }
            aviso?.let { Text(it, color = MaterialTheme.colorScheme.primary) }
        }
    }
    if (confirmar) AlertDialog(onDismissRequest = { confirmar = false },
        title = { Text("¿Limpiar datos locales?") },
        text = { Text("Se eliminarán los personajes guardados y sus favoritos de este dispositivo. Puedes volver a descargar los personajes al sincronizar.") },
        confirmButton = { TextButton(onClick = { confirmar = false; limpiar() }) { Text("Limpiar") } },
        dismissButton = { TextButton(onClick = { confirmar = false }) { Text("Cancelar") } })
}
