package com.carbajal.rickandmorty.ui.navigation

import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavType
import androidx.navigation.compose.*
import androidx.navigation.navArgument
import com.carbajal.rickandmorty.ui.connectivity.ConexionViewModel
import com.carbajal.rickandmorty.ui.components.IndicadorConexion
import com.carbajal.rickandmorty.AppContainer
import com.carbajal.rickandmorty.domain.model.Rol
import com.carbajal.rickandmorty.ui.UiState
import com.carbajal.rickandmorty.ui.admin.*
import com.carbajal.rickandmorty.ui.detalle.*
import com.carbajal.rickandmorty.ui.favoritos.*
import com.carbajal.rickandmorty.ui.lista.*
import com.carbajal.rickandmorty.ui.login.*

@Composable
fun AppNav(container: AppContainer, sesionIniciada: () -> Unit = {}) {
    val login: LoginViewModel = viewModel(factory = container.factory)
    val estado by login.estado.collectAsStateWithLifecycle()
    val sesion = (estado as? UiState.Exito)?.datos
    val conexion: ConexionViewModel = viewModel(factory = container.factory)
    val estadoConexion by conexion.estado.collectAsStateWithLifecycle()
    Column(Modifier.fillMaxSize()) {
        IndicadorConexion(estadoConexion, conSesion = sesion != null)
        Box(Modifier.weight(1f)) {
            if (sesion == null) LoginScreen(estado, login::ingresar, login::restaurar)
            else key(sesion.uid) {
                LaunchedEffect(sesion.uid) { sesionIniciada() }
                Column {
                    Row(Modifier.fillMaxWidth().padding(horizontal = 20.dp), horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = androidx.compose.ui.Alignment.CenterVertically) {
                        Column {
                            Text("RICK AND MORTY", style = MaterialTheme.typography.labelLarge, color = MaterialTheme.colorScheme.primary)
                            Text(if (sesion.rol == Rol.ADMIN) "Administrador" else "Explorador", style = MaterialTheme.typography.labelSmall)
                        }
                        TextButton(onClick = login::cerrarSesion) { Text("Cerrar sesión") }
                    }
                    Box(Modifier.weight(1f)) { CatalogoNav(container, sesion.rol) }
                }
            }
        }
    }
}

@Composable
private fun CatalogoNav(container: AppContainer, rol: Rol) {
    val nav = rememberNavController()
    val entry by nav.currentBackStackEntryAsState()
    val destinos = listOf("lista" to "Catálogo", "favoritos" to "Favoritos") +
        if (rol == Rol.ADMIN) listOf("admin" to "Admin") else emptyList()
    Scaffold(bottomBar = {
        if (entry?.destination?.route != "detalle/{id}") NavigationBar {
            destinos.forEach { (ruta, titulo) ->
                NavigationBarItem(selected = entry?.destination?.route == ruta,
                    onClick = { nav.navigate(ruta) {
                        popUpTo("lista") { saveState = true }
                        launchSingleTop = true
                        restoreState = true
                    } },
                    icon = { Text(when (ruta) { "lista" -> "◎"; "favoritos" -> "★"; else -> "⚙" }) },
                    label = { Text(titulo) })
            }
        }
    }) { padding -> Box(Modifier.padding(padding)) {
        NavHost(navController = nav, startDestination = "lista") {
            composable("lista") {
                val vm: ListaViewModel = viewModel(factory = container.factory)
                ListaScreen(vm.estado.collectAsStateWithLifecycle().value, vm::refrescar,
                    abrir = { nav.navigate("detalle/$it") }, favorito = vm::alternarFavorito,
                    sincronizando = vm.sincronizando.collectAsStateWithLifecycle().value,
                    aviso = vm.aviso.collectAsStateWithLifecycle().value)
            }
            composable("favoritos") {
                val vm: FavoritosViewModel = viewModel(factory = container.factory)
                FavoritosScreen(vm.estado.collectAsStateWithLifecycle().value,
                    { nav.navigate("detalle/$it") }, vm::alternarFavorito, vm::observar)
            }
            composable("detalle/{id}", arguments = listOf(navArgument("id") { type = NavType.IntType })) { backEntry ->
                val id = requireNotNull(backEntry.arguments).getInt("id")
                val vm: DetalleViewModel = viewModel(factory = container.factory)
                LaunchedEffect(id) { vm.cargar(id) }
                DetalleScreen(vm.estado.collectAsStateWithLifecycle().value, { nav.popBackStack() }, { vm.cargar(id, true) })
            }
            if (rol == Rol.ADMIN) composable("admin") {
                val vm: AdminViewModel = viewModel(factory = container.factory)
                AdminScreen(vm.estado.collectAsStateWithLifecycle().value,
                    vm.ocupado.collectAsStateWithLifecycle().value, vm.aviso.collectAsStateWithLifecycle().value,
                    vm::sincronizar, vm::limpiarCache)
            }
        }
    } }
}
