package com.carbajal.rickandmorty.data.repository

import com.carbajal.rickandmorty.domain.model.Item
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.map

class DemoItemRepository : ItemRepository {
    private val datos = MutableStateFlow(listOf(
        Item(14, "Alien Morty", "Alien", estado = "unknown", origen = "Unknown"),
        Item(15, "Alien Rick", "Alien", estado = "unknown", origen = "Unknown"),
    ))
    override val items = datos
    override val favoritos = datos.map { lista -> lista.filter { it.esFavorito } }
    override suspend fun refrescar() = Result.success(datos.value.size)
    override suspend fun obtenerDetalle(id: Int) = runCatching { datos.value.first { it.id == id } }
    override suspend fun alternarFavorito(id: Int) { datos.value = datos.value.map { if (it.id == id) it.copy(esFavorito = !it.esFavorito) else it } }
    override suspend fun cantidadGuardada() = datos.value.size
    override suspend fun cantidadFavoritos() = datos.value.count { it.esFavorito }
    override suspend fun limpiarCache() { datos.value = emptyList() }
}
