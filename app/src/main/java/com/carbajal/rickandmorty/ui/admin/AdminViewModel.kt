package com.carbajal.rickandmorty.ui.admin

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.carbajal.rickandmorty.data.repository.ItemRepository
import com.carbajal.rickandmorty.data.resultado
import com.carbajal.rickandmorty.ui.UiState
import com.carbajal.rickandmorty.ui.mensajeDe
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

data class ResumenAdmin(val guardados: Int, val favoritos: Int)

class AdminViewModel(private val repo: ItemRepository) : ViewModel() {
    private val _estado = MutableStateFlow<UiState<ResumenAdmin>>(UiState.Cargando)
    val estado = _estado.asStateFlow()
    private val _ocupado = MutableStateFlow(false)
    val ocupado = _ocupado.asStateFlow()
    private val _aviso = MutableStateFlow<String?>(null)
    val aviso = _aviso.asStateFlow()
    init { viewModelScope.launch {
        repo.items.collect { lista -> _estado.value = UiState.Exito(ResumenAdmin(lista.size, lista.count { it.esFavorito })) }
    } }
    fun sincronizar() = operar {
        repo.refrescar().onSuccess { _aviso.value = "Se guardaron $it elementos" }
            .onFailure { _aviso.value = mensajeDe(it) }
    }
    fun limpiarCache() = operar {
        resultado { repo.limpiarCache() }.onSuccess { _aviso.value = "Caché y favoritos eliminados de este dispositivo" }
            .onFailure { _aviso.value = mensajeDe(it) }
    }
    private fun operar(accion: suspend () -> Unit) {
        if (_ocupado.value) return
        _ocupado.value = true
        _aviso.value = null
        viewModelScope.launch { try { accion() } finally { _ocupado.value = false } }
    }
}
