package com.carbajal.rickandmorty.data.repository

import com.carbajal.rickandmorty.data.resultado
import com.carbajal.rickandmorty.data.mapper.aModelo
import com.carbajal.rickandmorty.data.remote.ApiService
import com.carbajal.rickandmorty.domain.model.Item
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.map

class ItemRepositoryImpl(private val api: ApiService) : ItemRepository {
    private val datos = MutableStateFlow<List<Item>>(emptyList())
    override val items = datos
    override val favoritos = datos.map { lista -> lista.filter { it.esFavorito } }
    override suspend fun refrescar(): Result<Int> = resultado {
        val nuevos = mutableListOf<Item>()
        var pagina = 1
        do {
            val respuesta = api.listar(pagina = pagina)
            nuevos += respuesta.results.map { it.aModelo() }
            pagina++
        } while (respuesta.info.next != null)
        datos.value = nuevos.distinctBy { it.id }
        datos.value.size
    }
    override suspend fun obtenerDetalle(id: Int) = resultado { api.detalle(id).aModelo() }
    override suspend fun alternarFavorito(id: Int) { datos.value = datos.value.map { if (it.id == id) it.copy(esFavorito = !it.esFavorito) else it } }
    override suspend fun cantidadGuardada() = datos.value.size
    override suspend fun cantidadFavoritos() = datos.value.count { it.esFavorito }
    override suspend fun limpiarCache() { datos.value = emptyList() }
}
