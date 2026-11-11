package com.carbajal.rickandmorty

import com.carbajal.rickandmorty.data.repository.ItemRepository
import com.carbajal.rickandmorty.domain.model.Item
import com.carbajal.rickandmorty.notification.Notificador
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.map

class FakeRepository : ItemRepository {
    val datos = MutableStateFlow<List<Item>>(emptyList())
    override val items = datos
    override val favoritos = datos.map { lista -> lista.filter { it.esFavorito } }
    var llamadas = 0
    var accion: suspend () -> Result<Int> = { Result.success(datos.value.size) }
    override suspend fun refrescar(): Result<Int> { llamadas++; return accion() }
    override suspend fun obtenerDetalle(id: Int) = runCatching { datos.value.first { it.id == id } }
    override suspend fun alternarFavorito(id: Int) { datos.value = datos.value.map { if (it.id == id) it.copy(esFavorito = !it.esFavorito) else it } }
    override suspend fun cantidadGuardada() = datos.value.size
    override suspend fun cantidadFavoritos() = datos.value.count { it.esFavorito }
    override suspend fun limpiarCache() { datos.value = emptyList() }
}
class FakeNotificador : Notificador {
    val mensajes = mutableListOf<String>()
    override fun mostrar(titulo: String, texto: String) { mensajes += texto }
}
