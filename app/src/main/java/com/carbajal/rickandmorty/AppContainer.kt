package com.carbajal.rickandmorty

import com.carbajal.rickandmorty.data.repository.ItemRepository

// Inyección manual: las pantallas y ViewModel dependen de los contratos.
class AppContainer(val items: ItemRepository)
