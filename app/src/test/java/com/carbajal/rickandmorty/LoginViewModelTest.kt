package com.carbajal.rickandmorty

import com.carbajal.rickandmorty.data.auth.AuthRepository
import com.carbajal.rickandmorty.domain.model.*
import com.carbajal.rickandmorty.ui.UiState
import com.carbajal.rickandmorty.ui.login.LoginViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.*
import org.junit.*
import org.junit.Assert.*

@OptIn(ExperimentalCoroutinesApi::class)
class LoginViewModelTest {
    private val dispatcher = StandardTestDispatcher()
    private var llamadas = 0
    private var sesion: Sesion? = null
    private val auth = object : AuthRepository {
        override suspend fun iniciarSesion(correo: String, clave: String): Result<Sesion> {
            llamadas++
            return Result.success(Sesion("prueba", correo, Rol.USUARIO))
        }
        override suspend fun sesionActual() = sesion
        override fun cerrarSesion() { sesion = null }
    }
    @Before fun preparar() { Dispatchers.setMain(dispatcher) }
    @After fun terminar() { Dispatchers.resetMain() }
    @Test fun vaciosNoLlamanAlServidor() = runTest(dispatcher) {
        val vm = LoginViewModel(auth); advanceUntilIdle()
        vm.ingresar("", "")
        assertTrue(vm.estado.value is UiState.Error); assertEquals(0,llamadas)
    }
    @Test fun correoInvalidoNoLlamaAlServidor() = runTest(dispatcher) {
        val vm = LoginViewModel(auth); advanceUntilIdle()
        vm.ingresar("incorrecto", "prueba")
        assertTrue(vm.estado.value is UiState.Error); assertEquals(0,llamadas)
    }
    @Test fun restauraRolYCierraSesion() = runTest(dispatcher) {
        sesion = Sesion("prueba", "prueba@example.com", Rol.ADMIN)
        val vm = LoginViewModel(auth); advanceUntilIdle()
        assertEquals(UiState.Exito(sesion),vm.estado.value)
        vm.cerrarSesion()
        assertEquals(UiState.Exito<Sesion?>(null),vm.estado.value)
    }
}
