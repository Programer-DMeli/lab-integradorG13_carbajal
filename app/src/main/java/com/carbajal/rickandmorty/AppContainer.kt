package com.carbajal.rickandmorty

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.carbajal.rickandmorty.data.repository.ItemRepositoryImpl
import com.carbajal.rickandmorty.data.remote.RetrofitClient
import com.carbajal.rickandmorty.data.repository.ItemRepository
import com.carbajal.rickandmorty.ui.lista.ListaViewModel
import com.carbajal.rickandmorty.ui.detalle.DetalleViewModel

class AppContainer(val items: ItemRepository = ItemRepositoryImpl(RetrofitClient.crear())) {
    val factory = object : ViewModelProvider.Factory {
        @Suppress("UNCHECKED_CAST")
        override fun <T : ViewModel> create(modelClass: Class<T>): T = when {
            modelClass.isAssignableFrom(ListaViewModel::class.java) -> ListaViewModel(items) as T
            modelClass.isAssignableFrom(DetalleViewModel::class.java) -> DetalleViewModel(items) as T
            else -> error("ViewModel no registrado: ${modelClass.name}")
        }
    }
}
