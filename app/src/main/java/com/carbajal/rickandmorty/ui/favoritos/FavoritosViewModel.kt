package com.carbajal.rickandmorty.ui.favoritos

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.carbajal.rickandmorty.data.repository.ItemRepository
import com.carbajal.rickandmorty.domain.model.Item
import com.carbajal.rickandmorty.data.resultado
import com.carbajal.rickandmorty.ui.UiState
import com.carbajal.rickandmorty.ui.mensajeDe
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import kotlinx.coroutines.Job

class FavoritosViewModel(private val repo: ItemRepository) : ViewModel() {
    private val _estado = MutableStateFlow<UiState<List<Item>>>(UiState.Cargando)
    val estado = _estado.asStateFlow()
    private var observacion: Job? = null
    init { observar() }
    fun observar() {
        observacion?.cancel()
        _estado.value = UiState.Cargando
        observacion = viewModelScope.launch {
        resultado { repo.favoritos.collect { _estado.value = UiState.Exito(it) } }
            .onFailure { _estado.value = UiState.Error(mensajeDe(it)) }
    } }
    fun alternarFavorito(id: Int) { viewModelScope.launch {
        resultado { repo.alternarFavorito(id) }.onFailure { _estado.value = UiState.Error(mensajeDe(it)) }
    } }
}
