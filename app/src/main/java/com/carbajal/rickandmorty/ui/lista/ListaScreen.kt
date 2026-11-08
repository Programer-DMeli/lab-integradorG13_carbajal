package com.carbajal.rickandmorty.ui.lista

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.carbajal.rickandmorty.domain.model.Item
import com.carbajal.rickandmorty.ui.UiState
import com.carbajal.rickandmorty.ui.components.*

@Composable
fun ListaScreen(estado: UiState<List<Item>>, refrescar: () -> Unit,
    abrir: (Int) -> Unit = {}, favorito: ((Int) -> Unit)? = null, aviso: String? = null,
    sincronizando: Boolean = false, soloFavoritos: Boolean = false) {
    Column(Modifier.fillMaxSize()) {
        Column(Modifier.padding(horizontal = 20.dp, vertical = 12.dp)) {
            Text(if (soloFavoritos) "Tu colección" else "Explora el multiverso", style = MaterialTheme.typography.headlineMedium)
            Text(if (soloFavoritos) "Personajes que quieres volver a encontrar" else "Rick and Morty · Especie Alien", style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant)
            if (!soloFavoritos) TextButton(onClick = refrescar, enabled = !sincronizando) {
                Text(if (sincronizando) "Sincronizando…" else "Sincronizar personajes")
            }
        }
        aviso?.let { Surface(color = MaterialTheme.colorScheme.secondaryContainer, modifier = Modifier.fillMaxWidth()) {
            Text(it, Modifier.padding(16.dp), style = MaterialTheme.typography.bodyMedium)
        } }
        when (estado) {
            UiState.Cargando -> Cargando()
            is UiState.Error -> ErrorView(estado.mensaje, refrescar)
            is UiState.Exito -> if (estado.datos.isEmpty()) {
                Vacio(if (soloFavoritos) "Aún no tienes favoritos" else "No hay personajes guardados",
                    if (soloFavoritos) "Marca una estrella en el catálogo para empezar tu colección." else "Sincroniza para descubrir personajes Alien.")
            } else LazyColumn(contentPadding = PaddingValues(16.dp), verticalArrangement = Arrangement.spacedBy(12.dp)) {
                items(estado.datos, key = { it.id }) { item -> ItemCard(item, { abrir(item.id) }, favorito?.let { { it(item.id) } }) }
            }
        }
    }
}
