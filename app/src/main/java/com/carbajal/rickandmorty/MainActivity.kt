package com.carbajal.rickandmorty

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.ui.Modifier
import androidx.compose.foundation.layout.Box
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.carbajal.rickandmorty.ui.lista.ListaScreen
import com.carbajal.rickandmorty.ui.lista.ListaViewModel
import com.carbajal.rickandmorty.ui.theme.RickandmortyTheme

class MainActivity : ComponentActivity() {
    private val container by lazy { AppContainer() }
    private val vm: ListaViewModel by viewModels { container.factory }
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            RickandmortyTheme {
                Scaffold { padding -> Box(Modifier.padding(padding)) {
                    ListaScreen(vm.estado.collectAsStateWithLifecycle().value, vm::refrescar)
                } }
            }
        }
    }
}
