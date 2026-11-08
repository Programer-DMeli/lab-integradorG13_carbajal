package com.carbajal.rickandmorty.ui.navigation

import androidx.compose.runtime.*
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
    NavHost(navController = nav, startDestination = "lista") {
        composable("lista") {
            val vm: ListaViewModel = viewModel(factory = container.factory)
            ListaScreen(vm.estado.collectAsStateWithLifecycle().value, vm::refrescar,
                abrir = { nav.navigate("detalle/$it") },
                sincronizando = vm.sincronizando.collectAsStateWithLifecycle().value)
        }
        composable("detalle/{id}", arguments = listOf(navArgument("id") { type = NavType.IntType })) { entry ->
            val id = requireNotNull(entry.arguments).getInt("id")
            val vm: DetalleViewModel = viewModel(factory = container.factory)
            LaunchedEffect(id) { vm.cargar(id) }
            DetalleScreen(vm.estado.collectAsStateWithLifecycle().value, { nav.popBackStack() }, { vm.cargar(id, true) })
        }
    }
}
