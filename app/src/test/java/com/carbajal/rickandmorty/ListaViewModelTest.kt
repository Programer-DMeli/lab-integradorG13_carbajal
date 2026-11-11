package com.carbajal.rickandmorty

import com.carbajal.rickandmorty.domain.model.Item
import com.carbajal.rickandmorty.ui.UiState
import com.carbajal.rickandmorty.ui.lista.ListaViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.delay
import kotlinx.coroutines.test.*
import org.junit.After
import org.junit.Assert.*
import org.junit.Before
import org.junit.Test
import java.io.IOException

@OptIn(ExperimentalCoroutinesApi::class)
class ListaViewModelTest {
    private val dispatcher = StandardTestDispatcher()
    private val repo = FakeRepository()
    private val notificador = FakeNotificador()
    @Before fun preparar() { Dispatchers.setMain(dispatcher) }
    @After fun terminar() { Dispatchers.resetMain() }

    @Test fun cargaInicialNoMuestraVacioAntesDeRespuesta() = runTest(dispatcher) {
        repo.accion = { delay(1000); Result.success(0) }
        val vm = ListaViewModel(repo, notificador)
        runCurrent()
        assertEquals(UiState.Cargando, vm.estado.value)
        assertTrue(vm.sincronizando.value)
        advanceUntilIdle()
        assertEquals(UiState.Exito(emptyList<Item>()), vm.estado.value)
    }
    @Test fun exitoMuestraDatosYNotificaCantidad() = runTest(dispatcher) {
        val item = Item(6, "Abadango Cluster Princess", "Alien")
        repo.accion = { repo.datos.value = listOf(item); Result.success(1) }
        val vm = ListaViewModel(repo, notificador)
        advanceUntilIdle()
        assertEquals(UiState.Exito(listOf(item)), vm.estado.value)
        assertEquals(listOf("Se guardaron 1 elementos"), notificador.mensajes)
        assertFalse(vm.sincronizando.value)
    }
    @Test fun falloSinCacheMuestraErrorYNoNotifica() = runTest(dispatcher) {
        repo.accion = { Result.failure(IOException()) }
        val vm = ListaViewModel(repo, notificador)
        advanceUntilIdle()
        assertTrue(vm.estado.value is UiState.Error)
        assertTrue(notificador.mensajes.isEmpty())
        assertFalse(vm.sincronizando.value)
    }
    @Test fun falloConCacheConservaDatosYMuestraAviso() = runTest(dispatcher) {
        val items = listOf(Item(6, "Princesa", "Alien", esFavorito = true))
        repo.datos.value = items
        repo.accion = { Result.failure(IOException()) }
        val vm = ListaViewModel(repo, notificador)
        advanceUntilIdle()
        assertEquals(UiState.Exito(items), vm.estado.value)
        assertEquals("Último intento sin conexión: mostrando datos guardados", vm.aviso.value)
        assertTrue(notificador.mensajes.isEmpty())
    }
    @Test fun reintentarRecuperaErrorYLimpiaAviso() = runTest(dispatcher) {
        repo.accion = { Result.failure(IOException()) }
        val vm = ListaViewModel(repo, notificador)
        advanceUntilIdle()
        repo.accion = { repo.datos.value = listOf(Item(6,"Princesa","Alien")); Result.success(1) }
        vm.refrescar()
        advanceUntilIdle()
        assertTrue(vm.estado.value is UiState.Exito)
        assertNull(vm.aviso.value)
        assertEquals(2, repo.llamadas)
    }
    @Test fun doblePulsacionNoDuplicaDescarga() = runTest(dispatcher) {
        repo.accion = { delay(100); Result.success(0) }
        val vm = ListaViewModel(repo, notificador)
        vm.refrescar(); vm.refrescar()
        advanceUntilIdle()
        assertEquals(1, repo.llamadas)
        assertEquals(1, notificador.mensajes.size)
    }
    @Test fun alternarFavoritoActualizaListaObservada() = runTest(dispatcher) {
        repo.datos.value = listOf(Item(6,"Princesa","Alien"))
        val vm = ListaViewModel(repo, notificador)
        advanceUntilIdle()
        vm.alternarFavorito(6)
        advanceUntilIdle()
        assertTrue((vm.estado.value as UiState.Exito).datos.single().esFavorito)
    }
    @Test fun limpiarCacheTrasFalloDeRefrescoMuestraVacioSinAvisoAntiguo() = runTest(dispatcher) {
        repo.datos.value = listOf(Item(6,"Princesa","Alien"))
        repo.accion = { Result.failure(IOException()) }
        val vm = ListaViewModel(repo, notificador)
        advanceUntilIdle()
        assertNotNull(vm.aviso.value)
        repo.limpiarCache()
        advanceUntilIdle()
        assertEquals(UiState.Exito(emptyList<Item>()),vm.estado.value)
        assertNull(vm.aviso.value)
        vm.refrescar()
        advanceUntilIdle()
        assertTrue(vm.estado.value is UiState.Error)
    }

}
