package com.carbajal.rickandmorty

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.carbajal.rickandmorty.data.repository.DemoItemRepository
import com.carbajal.rickandmorty.data.repository.ItemRepository
import com.carbajal.rickandmorty.ui.lista.ListaViewModel

class AppContainer(val items: ItemRepository = DemoItemRepository()) {
    val factory = object : ViewModelProvider.Factory {
        @Suppress("UNCHECKED_CAST")
        override fun <T : ViewModel> create(modelClass: Class<T>): T = when {
            modelClass.isAssignableFrom(ListaViewModel::class.java) -> ListaViewModel(items) as T
            else -> error("ViewModel no registrado: ${modelClass.name}")
        }
    }
}
