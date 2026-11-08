package com.carbajal.rickandmorty

import androidx.room.Room
import androidx.test.core.app.ApplicationProvider
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.carbajal.rickandmorty.data.local.AppDatabase
import com.carbajal.rickandmorty.data.repository.ItemRepositoryImpl
import com.carbajal.rickandmorty.data.remote.ApiService
import com.carbajal.rickandmorty.data.remote.dto.*
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.runBlocking
import org.junit.*
import org.junit.Assert.*
import org.junit.runner.RunWith
import java.io.IOException

@RunWith(AndroidJUnit4::class)
class RoomRepositoryTest {
    private lateinit var db: AppDatabase
    private lateinit var api: FakeApi
    private lateinit var repo: ItemRepositoryImpl
    @Before fun preparar() {
        db = Room.inMemoryDatabaseBuilder(ApplicationProvider.getApplicationContext(), AppDatabase::class.java).build()
        api = FakeApi(); repo = ItemRepositoryImpl(api, db)
    }
    @After fun cerrar() { db.close() }
    @Test fun paginaCompletaSeGuardaSinPerderFavoritos() = runBlocking {
        assertEquals(2, repo.refrescar().getOrThrow())
        repo.alternarFavorito(6)
        repo.refrescar().getOrThrow()
        assertEquals(2, repo.cantidadGuardada())
        assertEquals(1, repo.cantidadFavoritos())
        assertEquals(6, repo.favoritos.first().single().id)
        assertEquals(listOf(1,2,1,2),api.paginas)
    }
    @Test fun falloEnSegundaPaginaNoPublicaDatosParciales() = runBlocking {
        repo.refrescar().getOrThrow(); repo.alternarFavorito(6)
        api.fallarSegunda = true
        assertTrue(repo.refrescar().isFailure)
        assertEquals(2,repo.items.first().size)
        assertEquals(1,repo.cantidadFavoritos())
    }
    @Test fun detalleUsaSegundoEndpointYCacheSinRed() = runBlocking {
        repo.refrescar().getOrThrow()
        assertEquals(6,repo.obtenerDetalle(6).getOrThrow().id)
        assertEquals(1,api.detalles)
        api.sinRed = true
        assertEquals(6,repo.obtenerDetalle(6).getOrThrow().id)
        assertTrue(repo.obtenerDetalle(999).isFailure)
    }
    @Test fun limpiarCacheDejaAmbosTotalesEnCero() = runBlocking {
        repo.refrescar().getOrThrow(); repo.alternarFavorito(6)
        repo.limpiarCache()
        assertEquals(0,repo.cantidadGuardada()); assertEquals(0,repo.cantidadFavoritos())
        assertTrue(repo.items.first().isEmpty())
    }
    private class FakeApi : ApiService {
        val paginas = mutableListOf<Int>()
        var fallarSegunda = false
        var sinRed = false
        var detalles = 0
        private fun item(id: Int) = ItemDto(id,"Alien $id","Alive","Alien","unknown",PlaceDto("Origen"),PlaceDto("Lugar"),"",emptyList())
        override suspend fun listar(pagina: Int, especie: String): CharacterPageDto {
            check(especie == "Alien")
            paginas += pagina
            if (sinRed || (fallarSegunda && pagina == 2)) throw IOException()
            return CharacterPageDto(PageInfoDto(2,2,if(pagina == 1) "pagina2" else null), listOf(item(if(pagina == 1) 6 else 14)))
        }
        override suspend fun detalle(id: Int): ItemDto { detalles++; if (sinRed) throw IOException(); return item(id) }
    }
}
