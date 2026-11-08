package com.carbajal.rickandmorty.ui.navigation

import androidx.compose.runtime.*
import androidx.compose.material3.*
import androidx.compose.foundation.layout.*
import androidx.compose.ui.Modifier
import com.carbajal.rickandmorty.ui.favoritos.*
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavType
import androidx.navigation.compose.*
import androidx.navigation.navArgument
import com.carbajal.rickandmorty.AppContainer
import com.carbajal.rickandmorty.ui.lista.*
import com.carbajal.rickandmorty.ui.detalle.*

@Composable
fun AppNav(container: AppContainer) {
    val nav = rememberNavController()
    val entry by nav.currentBackStackEntryAsState()
    Scaffold(bottomBar = {
        if (entry?.destination?.route != "detalle/{id}") NavigationBar {
            listOf("lista" to "Catálogo", "favoritos" to "Favoritos").forEach { (ruta, titulo) ->
                NavigationBarItem(selected = entry?.destination?.route == ruta,
                    onClick = { nav.navigate(ruta) { popUpTo("lista") { saveState = true }; launchSingleTop = true; restoreState = true } },
                    icon = { Text(if (ruta == "lista") "◎" else "★") }, label = { Text(titulo) })
            }
        }
    }) { padding -> Box(Modifier.padding(padding)) {
    NavHost(navController = nav, startDestination = "lista") {
        composable("lista") {
            val vm: ListaViewModel = viewModel(factory = container.factory)
            ListaScreen(vm.estado.collectAsStateWithLifecycle().value, vm::refrescar,
                abrir = { nav.navigate("detalle/$it") }, favorito = vm::alternarFavorito,
                sincronizando = vm.sincronizando.collectAsStateWithLifecycle().value)
        }
        composable("favoritos") {
            val vm: FavoritosViewModel = viewModel(factory = container.factory)
            FavoritosScreen(vm.estado.collectAsStateWithLifecycle().value,
                { nav.navigate("detalle/$it") }, vm::alternarFavorito, vm::observar)
        }
        composable("detalle/{id}", arguments = listOf(navArgument("id") { type = NavType.IntType })) { entry ->
            val id = requireNotNull(entry.arguments).getInt("id")
            val vm: DetalleViewModel = viewModel(factory = container.factory)
            LaunchedEffect(id) { vm.cargar(id) }
            DetalleScreen(vm.estado.collectAsStateWithLifecycle().value, { nav.popBackStack() }, { vm.cargar(id, true) })
        }
    }
    } }
}

