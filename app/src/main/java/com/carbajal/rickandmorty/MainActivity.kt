package com.carbajal.rickandmorty

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.ui.Modifier
import com.carbajal.rickandmorty.ui.navigation.AppNav
import com.carbajal.rickandmorty.ui.theme.RickandmortyTheme

class MainActivity : ComponentActivity() {
    private val container by lazy { AppContainer() }
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            RickandmortyTheme {
                Scaffold { padding -> Box(Modifier.padding(padding)) { AppNav(container) } }
            }
        }
    }
}
