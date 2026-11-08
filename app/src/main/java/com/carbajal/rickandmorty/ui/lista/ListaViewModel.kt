package com.carbajal.rickandmorty.ui.lista

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.carbajal.rickandmorty.data.repository.ItemRepository
import com.carbajal.rickandmorty.domain.model.Item
import com.carbajal.rickandmorty.ui.UiState
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class ListaViewModel(private val repo: ItemRepository) : ViewModel() {
    private val _estado = MutableStateFlow<UiState<List<Item>>>(UiState.Cargando)
    val estado = _estado.asStateFlow()
    init {
        viewModelScope.launch { repo.items.collect { _estado.value = UiState.Exito(it) } }
        refrescar()
    }
    fun refrescar() { viewModelScope.launch {
        repo.refrescar().onFailure { _estado.value = UiState.Error(it.message ?: "No se pudieron obtener los personajes") }
    } }
}
