package com.carbajal.rickandmorty.ui.login

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.carbajal.rickandmorty.data.auth.AuthRepository
import com.carbajal.rickandmorty.data.resultado
import com.carbajal.rickandmorty.domain.model.Sesion
import com.carbajal.rickandmorty.ui.UiState
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class LoginViewModel(private val auth: AuthRepository) : ViewModel() {
    private val _estado = MutableStateFlow<UiState<Sesion?>>(UiState.Cargando)
    val estado = _estado.asStateFlow()
    init { restaurar() }
    fun restaurar() {
        _estado.value = UiState.Cargando
        viewModelScope.launch {
            resultado { auth.sesionActual() }.onSuccess { _estado.value = UiState.Exito(it) }
                .onFailure { _estado.value = UiState.Error("No se pudo verificar tu sesión y rol. Revisa la conexión y la configuración de Firestore.") }
        }
    }
    fun ingresar(correo: String, clave: String) {
        if (_estado.value is UiState.Cargando) return
        if (correo.isBlank() || clave.isBlank()) {
            _estado.value = UiState.Error("Completa el correo y la contraseña."); return
        }
        if (!Regex("""^[^\s@]+@[^\s@]+\.[^\s@]+$""").matches(correo.trim())) {
            _estado.value = UiState.Error("Escribe un correo válido."); return
        }
        _estado.value = UiState.Cargando
        viewModelScope.launch {
            auth.iniciarSesion(correo.trim(), clave)
                .onSuccess { _estado.value = UiState.Exito(it) }
                .onFailure { _estado.value = UiState.Error("No se pudo ingresar. Revisa tus credenciales, la conexión y el rol en Firestore.") }
        }
    }
    fun cerrarSesion() {
        auth.cerrarSesion()
        _estado.value = UiState.Exito(null)
    }
}
