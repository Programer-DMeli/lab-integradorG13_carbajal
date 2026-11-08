package com.carbajal.rickandmorty.ui.detalle

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.carbajal.rickandmorty.data.repository.ItemRepository
import com.carbajal.rickandmorty.domain.model.Item
import com.carbajal.rickandmorty.ui.UiState
import com.carbajal.rickandmorty.ui.mensajeDe
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class DetalleViewModel(private val repo: ItemRepository) : ViewModel() {
    private val _estado = MutableStateFlow<UiState<Item>>(UiState.Cargando)
    val estado = _estado.asStateFlow()
    private var idActual: Int? = null
    private var carga: Job? = null
    fun cargar(id: Int, forzar: Boolean = false) {
        if (idActual == id && !forzar) return
        idActual = id
        carga?.cancel()
        _estado.value = UiState.Cargando
        carga = viewModelScope.launch {
            repo.obtenerDetalle(id).onSuccess { _estado.value = UiState.Exito(it) }
                .onFailure { _estado.value = UiState.Error(mensajeDe(it)) }
        }
    }
}
