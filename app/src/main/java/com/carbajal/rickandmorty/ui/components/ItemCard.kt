package com.carbajal.rickandmorty.ui.components

import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.compose.ui.layout.ContentScale
import coil.compose.AsyncImage
import com.carbajal.rickandmorty.domain.model.Item

@Composable
fun ItemCard(item: Item, abrir: () -> Unit, favorito: (() -> Unit)? = null) {
    Card(onClick = abrir, modifier = Modifier.fillMaxWidth()) {
        Row(Modifier.padding(16.dp), verticalAlignment = Alignment.CenterVertically) {
            Surface(shape = MaterialTheme.shapes.medium, color = MaterialTheme.colorScheme.primaryContainer) {
                AsyncImage(model = item.imagenUrl, contentDescription = item.titulo,
                    modifier = Modifier.size(76.dp), contentScale = ContentScale.Crop)
            }
            Column(Modifier.weight(1f).padding(start = 16.dp)) {
                Text(item.titulo, style = MaterialTheme.typography.titleMedium)
                Text(item.subtitulo, color = MaterialTheme.colorScheme.onSurfaceVariant)
                Text(item.estado, style = MaterialTheme.typography.labelMedium, color = MaterialTheme.colorScheme.primary)
            }
            favorito?.let { IconButton(onClick = it) { Text(if (item.esFavorito) "★" else "☆", style = MaterialTheme.typography.headlineMedium) } }
        }
    }
}
