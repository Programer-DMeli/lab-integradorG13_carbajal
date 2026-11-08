package com.carbajal.rickandmorty.ui.detalle

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.unit.dp
import coil.compose.AsyncImage
import com.carbajal.rickandmorty.domain.model.Item
import com.carbajal.rickandmorty.ui.UiState
import com.carbajal.rickandmorty.ui.components.*

@Composable
fun DetalleScreen(estado: UiState<Item>, volver: () -> Unit, reintentar: () -> Unit) {
    Column(Modifier.fillMaxSize()) {
        TextButton(onClick = volver, modifier = Modifier.padding(horizontal = 12.dp)) { Text("← Volver al catálogo") }
        when (estado) {
            UiState.Cargando -> Cargando()
            is UiState.Error -> ErrorView(estado.mensaje, reintentar)
            is UiState.Exito -> Column(Modifier.verticalScroll(rememberScrollState()).padding(20.dp), verticalArrangement = Arrangement.spacedBy(16.dp)) {
                val item = estado.datos
                AsyncImage(item.imagenUrl, contentDescription = item.titulo,
                    modifier = Modifier.fillMaxWidth().height(280.dp), contentScale = ContentScale.Fit)
                Text("ARCHIVO #${item.id}", color = MaterialTheme.colorScheme.primary, style = MaterialTheme.typography.labelLarge)
                Text(item.titulo, style = MaterialTheme.typography.headlineLarge)
                Text("${item.subtitulo} · ${item.estado}", style = MaterialTheme.typography.titleMedium)
                HorizontalDivider()
                Dato("Género", item.genero)
                Dato("Origen", item.origen)
                Dato("Última ubicación", item.ubicacion)
                Dato("Apariciones", "${item.episodios} episodios")
            }
        }
    }
}

@Composable
private fun Dato(titulo: String, valor: String) {
    Column { Text(titulo, color = MaterialTheme.colorScheme.onSurfaceVariant, style = MaterialTheme.typography.labelLarge)
        Text(valor, style = MaterialTheme.typography.bodyLarge) }
}
