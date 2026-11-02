package me.mauromax.rutaconfiable

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import me.mauromax.rutaconfiable.navigation.AppNavGraph
import me.mauromax.rutaconfiable.ui.theme.RutaConfiableTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            RutaConfiableTheme {
                AppNavGraph()
            }
        }
    }
}
