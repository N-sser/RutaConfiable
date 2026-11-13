package me.mauromax.rutaconfiable.ui.components

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Inventory2
import androidx.compose.material.icons.filled.Map
import androidx.compose.material.icons.filled.Person
import androidx.compose.material3.Icon
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.NavigationBarItemDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.unit.sp
import me.mauromax.rutaconfiable.ui.theme.CardBackground
import me.mauromax.rutaconfiable.ui.theme.Secondary
import me.mauromax.rutaconfiable.ui.theme.SecondaryContainer

/** Índices: 0 Inicio, 1 Cargas, 2 Viajes, 3 Perfil. */
@Composable
fun AppBottomBar(
    selectedIndex: Int,
    onItemClick: (Int) -> Unit = {}
) {
    NavigationBar(containerColor = CardBackground) {
        listOf(
            Icons.Default.Home to "Inicio",
            Icons.Default.Inventory2 to "Cargas",
            Icons.Default.Map to "Viajes",
            Icons.Default.Person to "Perfil"
        ).forEachIndexed { i, (icon, label) ->
            NavigationBarItem(
                selected = i == selectedIndex,
                onClick = { onItemClick(i) },
                icon = { Icon(icon, label) },
                label = { Text(label, fontSize = 11.sp) },
                colors = NavigationBarItemDefaults.colors(
                    selectedIconColor = Secondary,
                    selectedTextColor = Secondary,
                    indicatorColor = SecondaryContainer.copy(alpha = 0.2f)
                )
            )
        }
    }
}