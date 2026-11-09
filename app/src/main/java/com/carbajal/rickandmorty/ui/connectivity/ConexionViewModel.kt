package com.carbajal.rickandmorty.ui.connectivity

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.carbajal.rickandmorty.domain.connectivity.EstadoConexion
import com.carbajal.rickandmorty.domain.connectivity.MonitorConexion
import com.carbajal.rickandmorty.ui.UiState
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn

class ConexionViewModel(monitor: MonitorConexion) : ViewModel() {
    val estado = monitor.estado
        .map<EstadoConexion, UiState<EstadoConexion>> { UiState.Exito(it) }
        .catch { emit(UiState.Error("No se pudo verificar la conexión")) }
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5_000), UiState.Cargando)
}
