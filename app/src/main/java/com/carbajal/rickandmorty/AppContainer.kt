package com.carbajal.rickandmorty

import android.content.Context
import com.carbajal.rickandmorty.data.auth.AuthRepository
import com.carbajal.rickandmorty.data.auth.FirebaseAuthRepository
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.room.Room
import com.carbajal.rickandmorty.data.local.AppDatabase
import com.carbajal.rickandmorty.data.repository.ItemRepository
import com.carbajal.rickandmorty.data.repository.ItemRepositoryImpl
import com.carbajal.rickandmorty.data.remote.RetrofitClient
import com.carbajal.rickandmorty.ui.favoritos.FavoritosViewModel
import com.carbajal.rickandmorty.ui.lista.ListaViewModel
import com.carbajal.rickandmorty.ui.detalle.DetalleViewModel

class AppContainer(context: Context) {
    private val db = Room.databaseBuilder(context.applicationContext, AppDatabase::class.java, "catalogo.db").build()
    val auth: AuthRepository by lazy { FirebaseAuthRepository() }
    val items: ItemRepository = ItemRepositoryImpl(RetrofitClient.crear(), db)
    val factory = object : ViewModelProvider.Factory {
        @Suppress("UNCHECKED_CAST")
        override fun <T : ViewModel> create(modelClass: Class<T>): T = when {
            modelClass.isAssignableFrom(ListaViewModel::class.java) -> ListaViewModel(items) as T
            modelClass.isAssignableFrom(DetalleViewModel::class.java) -> DetalleViewModel(items) as T
            modelClass.isAssignableFrom(FavoritosViewModel::class.java) -> FavoritosViewModel(items) as T
            else -> error("ViewModel no registrado: ${modelClass.name}")
        }
    }
}
