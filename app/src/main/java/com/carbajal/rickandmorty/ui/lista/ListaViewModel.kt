package com.carbajal.rickandmorty.ui.lista

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.carbajal.rickandmorty.data.repository.ItemRepository
import com.carbajal.rickandmorty.domain.model.Item
import com.carbajal.rickandmorty.data.resultado
import com.carbajal.rickandmorty.ui.UiState
import com.carbajal.rickandmorty.ui.mensajeDe
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch

class ListaViewModel(private val repo: ItemRepository) : ViewModel() {
    private val _estado = MutableStateFlow<UiState<List<Item>>>(UiState.Cargando)
    val estado = _estado.asStateFlow()
    private val _sincronizando = MutableStateFlow(false)
    val sincronizando = _sincronizando.asStateFlow()
    private val _aviso = MutableStateFlow<String?>(null)
    val aviso = _aviso.asStateFlow()
    private var primeraCargaTerminada = false
    init {
        viewModelScope.launch { repo.items.collect {
            if (it.isNotEmpty() || primeraCargaTerminada) _estado.value = UiState.Exito(it)
        } }
        refrescar()
    }
    fun alternarFavorito(id: Int) { viewModelScope.launch {
        resultado { repo.alternarFavorito(id) }.onFailure { _estado.value = UiState.Error(mensajeDe(it)) }
    } }
    fun refrescar() {
        if (_sincronizando.value) return
        _sincronizando.value = true
        if (_estado.value is UiState.Error) _estado.value = UiState.Cargando
        viewModelScope.launch {
            try {
                val resultado = repo.refrescar()
                primeraCargaTerminada = true
                resultado.onSuccess {
                    _aviso.value = null
                    _estado.value = UiState.Exito(repo.items.first())
                }.onFailure {
                    val guardados = repo.items.first()
                    if (guardados.isEmpty()) _estado.value = UiState.Error(mensajeDe(it))
                    else {
                        _estado.value = UiState.Exito(guardados)
                        _aviso.value = if (it is java.io.IOException) "Sin conexión: mostrando datos guardados"
                            else "No se pudo actualizar: mostrando datos guardados"
                    }
                }
            } finally { _sincronizando.value = false }
        }
    }
}
