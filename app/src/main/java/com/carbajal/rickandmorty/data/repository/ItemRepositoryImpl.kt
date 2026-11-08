package com.carbajal.rickandmorty.data.repository

import androidx.room.withTransaction
import com.carbajal.rickandmorty.data.resultado
import com.carbajal.rickandmorty.data.local.AppDatabase
import com.carbajal.rickandmorty.data.mapper.aEntity
import com.carbajal.rickandmorty.data.mapper.aModelo
import com.carbajal.rickandmorty.data.remote.ApiService
import com.carbajal.rickandmorty.domain.model.Item
import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.sync.Mutex
import kotlinx.coroutines.sync.withLock

class ItemRepositoryImpl(private val api: ApiService, private val db: AppDatabase) : ItemRepository {
    private val dao = db.itemDao()
    private val escritura = Mutex()
    override val items = dao.todos().map { lista -> lista.map { it.aModelo() } }
    override val favoritos = dao.favoritos().map { lista -> lista.map { it.aModelo() } }

    override suspend fun refrescar(): Result<Int> = resultado {
        escritura.withLock {
            val nuevos = mutableListOf<Item>()
            var pagina = 1
            do {
                val respuesta = api.listar(pagina = pagina)
                nuevos += respuesta.results.map { it.aModelo() }
                pagina++
            } while (respuesta.info.next != null)
            val unicos = nuevos.distinctBy { it.id }
            // No se publica una descarga parcial. Favoritos y datos cambian atómicamente.
            db.withTransaction {
                val favoritos = dao.idsFavoritos().toSet()
                dao.guardarTodos(unicos.map { it.copy(esFavorito = it.id in favoritos).aEntity() })
            }
            unicos.size
        }
    }

    override suspend fun obtenerDetalle(id: Int): Result<Item> = resultado {
        try {
            api.detalle(id).aModelo().copy(esFavorito = dao.buscar(id)?.esFavorito ?: false)
        } catch (e: CancellationException) {
            throw e
        } catch (e: Exception) {
            dao.buscar(id)?.aModelo() ?: throw e
        }
    }
    override suspend fun alternarFavorito(id: Int) { escritura.withLock { dao.alternarFavorito(id) } }
    override suspend fun cantidadGuardada() = dao.contar()
    override suspend fun cantidadFavoritos() = dao.contarFavoritos()
    override suspend fun limpiarCache() { escritura.withLock { dao.borrarTodo() } }
}
