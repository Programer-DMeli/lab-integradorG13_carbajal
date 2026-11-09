package com.carbajal.rickandmorty.ui.lista

import com.carbajal.rickandmorty.notification.Notificador
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
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.launch

class ListaViewModel(private val repo: ItemRepository, private val notificador: Notificador) : ViewModel() {
    private val _estado = MutableStateFlow<UiState<List<Item>>>(UiState.Cargando)
    val estado = _estado.asStateFlow()
    private val _sincronizando = MutableStateFlow(false)
    val sincronizando = _sincronizando.asStateFlow()
    private val _aviso = MutableStateFlow<String?>(null)
    val aviso = _aviso.asStateFlow()
    private var primeraCargaTerminada = false
    private var ultimaFalla: Throwable? = null
    init {
        viewModelScope.launch {
            var habiaDatos = false
            repo.items.catch { _estado.value = UiState.Error(mensajeDe(it)) }.collect { lista ->
                val falla = ultimaFalla
                when {
                    lista.isNotEmpty() -> {
                        habiaDatos = true
                        _estado.value = UiState.Exito(lista)
                    }
                    habiaDatos -> {
                        habiaDatos = false
                        ultimaFalla = null
                        _aviso.value = null
                        _estado.value = UiState.Exito(lista)
                    }
                    falla != null -> _estado.value = UiState.Error(mensajeDe(falla))
                    primeraCargaTerminada -> _estado.value = UiState.Exito(lista)
                }
            }
        }
        refrescar()
    }
    fun alternarFavorito(id: Int) { viewModelScope.launch {
        resultado { repo.alternarFavorito(id) }.onFailure { _estado.value = UiState.Error(mensajeDe(it)) }
    } }
    fun refrescar() {
        if (_sincronizando.value) return
        _sincronizando.value = true
        if ((_estado.value as? UiState.Exito)?.datos.isNullOrEmpty()) _estado.value = UiState.Cargando
        viewModelScope.launch {
            try {
                val resultado = repo.refrescar()
                primeraCargaTerminada = true
                resultado.onSuccess { n ->
                    ultimaFalla = null
                    notificador.mostrar("Datos actualizados", "Se guardaron $n elementos")
                    _aviso.value = null
                    _estado.value = UiState.Exito(repo.items.first())
                }.onFailure {
                    ultimaFalla = it
                    val guardados = repo.items.first()
                    if (guardados.isEmpty()) _estado.value = UiState.Error(mensajeDe(it))
                    else {
                        _estado.value = UiState.Exito(guardados)
                        _aviso.value = if (it is java.io.IOException) "Último intento sin conexión: mostrando datos guardados"
                            else "No se pudo actualizar: mostrando datos guardados"
                    }
                }
            } finally { _sincronizando.value = false }
        }
    }
}
