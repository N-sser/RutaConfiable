package me.mauromax.rutaconfiable.ui.screens

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.Icon
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import me.mauromax.rutaconfiable.ui.components.AppBottomBar
import me.mauromax.rutaconfiable.ui.components.AppCard
import me.mauromax.rutaconfiable.ui.components.BodyText
import me.mauromax.rutaconfiable.ui.components.PrimaryButton
import me.mauromax.rutaconfiable.ui.components.ScreenTitle
import me.mauromax.rutaconfiable.ui.components.StandardAppTopBar
import me.mauromax.rutaconfiable.ui.theme.*

/** "Cargas" del cliente: por ahora solo el estado vacío; luego mostrará sus reservas. */
@Composable
fun MyShipmentsScreen(
    onNavigateToHome: () -> Unit = {},
    onNavigateToTrips: () -> Unit = {},
    onNavigateToProfile: () -> Unit = {}
) {
    Scaffold(
        containerColor = Background,
        topBar = { StandardAppTopBar(showSecurityBadge = false) },
        bottomBar = {
            AppBottomBar(selectedIndex = 1) {
                when (it) {
                    0 -> onNavigateToHome()
                    2 -> onNavigateToTrips()
                    3 -> onNavigateToProfile()
                }
            }
        }
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .verticalScroll(rememberScrollState())
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            ScreenTitle("Mis envíos y reservas", 22.sp)
            BodyText("Aquí verás los cupos y el flete que reserves en los viajes.")

            AppCard(Modifier.fillMaxWidth(), spacing = 10.dp, alignment = Alignment.CenterHorizontally) {
                Icon(Icons.Default.Inventory2, null, Modifier.size(32.dp), tint = Outline)
                Text(
                    "Aún no tienes reservas ni envíos.",
                    fontSize = 12.sp, color = OnSurfaceVariant, textAlign = TextAlign.Center
                )
                PrimaryButton(
                    text = "Buscar trayectos",
                    onClick = onNavigateToTrips,
                    modifier = Modifier.fillMaxWidth(),
                    leadingIcon = Icons.Default.Search,
                    trailingIcon = null
                )
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
fun MyShipmentsScreenPreview() {
    RutaConfiableTheme { MyShipmentsScreen() }
}