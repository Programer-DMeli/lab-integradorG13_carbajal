package com.carbajal.rickandmorty.ui.login

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.unit.dp
import com.carbajal.rickandmorty.domain.model.Sesion
import com.carbajal.rickandmorty.ui.UiState
import com.carbajal.rickandmorty.ui.components.Cargando

@Composable
fun LoginScreen(estado: UiState<Sesion?>, ingresar: (String, String) -> Unit, restaurar: () -> Unit) {
    var correo by rememberSaveable { mutableStateOf("") }
    var clave by remember { mutableStateOf("") }
    if (estado is UiState.Cargando) { Cargando(); return }
    Column(Modifier.fillMaxSize().imePadding().verticalScroll(rememberScrollState()).padding(28.dp),
        verticalArrangement = Arrangement.Center, horizontalAlignment = Alignment.Start) {
        Text("◎", style = MaterialTheme.typography.displayLarge, color = MaterialTheme.colorScheme.primary)
        Spacer(Modifier.height(24.dp))
        Text("RICK AND MORTY", style = MaterialTheme.typography.labelLarge, color = MaterialTheme.colorScheme.primary)
        Text("Tu portal al\nmultiverso", style = MaterialTheme.typography.displaySmall)
        Text("Explora personajes Alien y guarda tus favoritos.", Modifier.padding(top = 12.dp, bottom = 32.dp),
            color = MaterialTheme.colorScheme.onSurfaceVariant)
        OutlinedTextField(correo, { correo = it }, label = { Text("Correo electrónico") },
            singleLine = true, keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Email), modifier = Modifier.fillMaxWidth())
        Spacer(Modifier.height(12.dp))
        OutlinedTextField(clave, { clave = it }, label = { Text("Contraseña") }, singleLine = true,
            visualTransformation = PasswordVisualTransformation(), keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Password), modifier = Modifier.fillMaxWidth())
        if (estado is UiState.Error) Text(estado.mensaje, Modifier.padding(top = 16.dp), color = MaterialTheme.colorScheme.error)
        Button(onClick = { ingresar(correo, clave); clave = "" }, modifier = Modifier.fillMaxWidth().padding(top = 24.dp).heightIn(min = 52.dp)) { Text("Entrar al portal") }
        TextButton(onClick = restaurar) { Text("Reintentar recuperar sesión") }
        Text("Acceso con tu cuenta asignada · Catálogo Alien", style = MaterialTheme.typography.labelMedium,
            color = MaterialTheme.colorScheme.onSurfaceVariant, modifier = Modifier.padding(top = 24.dp))
    }
}
