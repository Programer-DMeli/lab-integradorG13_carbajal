package com.carbajal.rickandmorty.ui.components

import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp

@Composable
fun ErrorView(mensaje: String, reintentar: () -> Unit, modifier: Modifier = Modifier) {
    Column(modifier.fillMaxSize().padding(24.dp), horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center) {
        Text("No pudimos abrir el portal", style = MaterialTheme.typography.titleLarge)
        Text(mensaje, modifier = Modifier.padding(vertical = 16.dp), textAlign = TextAlign.Center)
        Button(onClick = reintentar) { Text("Reintentar") }
    }
}

@Composable
fun Vacio(titulo: String, mensaje: String, modifier: Modifier = Modifier) {
    Column(modifier.fillMaxSize().padding(32.dp), horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center) {
        Text(titulo, style = MaterialTheme.typography.titleLarge, textAlign = TextAlign.Center)
        Text(mensaje, modifier = Modifier.padding(top = 12.dp), textAlign = TextAlign.Center)
    }
}
