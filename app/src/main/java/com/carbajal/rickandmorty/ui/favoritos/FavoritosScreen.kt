package com.carbajal.rickandmorty.ui.favoritos

import androidx.compose.runtime.Composable
import com.carbajal.rickandmorty.domain.model.Item
import com.carbajal.rickandmorty.ui.UiState
import com.carbajal.rickandmorty.ui.lista.ListaScreen

@Composable
fun FavoritosScreen(estado: UiState<List<Item>>, abrir: (Int) -> Unit,
    favorito: (Int) -> Unit, reintentar: () -> Unit) {
    ListaScreen(estado, reintentar, abrir, favorito, soloFavoritos = true)
}
