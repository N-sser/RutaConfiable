package me.mauromax.rutaconfiable.ui.screens

import androidx.compose.foundation.clickable
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
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import me.mauromax.rutaconfiable.ui.components.AppBottomBar
import me.mauromax.rutaconfiable.ui.components.AppCard
import me.mauromax.rutaconfiable.ui.components.BodyText
import me.mauromax.rutaconfiable.ui.components.ScreenTitle
import me.mauromax.rutaconfiable.ui.components.StandardAppTopBar
import me.mauromax.rutaconfiable.ui.components.StatusTag
import me.mauromax.rutaconfiable.ui.theme.*

/** Pantalla de inicio: saludo y accesos rápidos según el rol. */
@Composable
fun HomeScreen(
    role: RegistrationRole = RegistrationRole.DRIVER,
    userName: String = "Usuario",
    onNavigateToLoads: () -> Unit = {},
    onNavigateToTrips: () -> Unit = {},
    onNavigateToProfile: () -> Unit = {}
) {
    val isDriver = role == RegistrationRole.DRIVER

    Scaffold(
        containerColor = Background,
        topBar = { StandardAppTopBar(showSecurityBadge = false) },
        bottomBar = {
            AppBottomBar(selectedIndex = 0) {
                when (it) {
                    1 -> onNavigateToLoads()
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
            Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
                BodyText("Bienvenido")
                ScreenTitle(userName, 26.sp)
                StatusTag(if (isDriver) "Conductor" else "Cliente", PrimaryLight, Color.White)
            }

            BodyText(
                if (isDriver) "Publica tus viajes con cupos y flete libres y recibe reservas de clientes."
                else "Encuentra viajes con cupos y flete disponibles y reserva en segundos."
            )

            HomeAction(Icons.Default.Search, "Buscar trayectos", "Envíos y pasajes libres", onNavigateToTrips)
            if (isDriver) {
                HomeAction(Icons.Default.Add, "Publicar nuevo viaje", "Ofrecer flete o ruta", onNavigateToLoads)
            } else {
                HomeAction(Icons.Default.Inventory2, "Mis envíos", "Tus reservas de cupos y flete", onNavigateToLoads)
            }
            HomeAction(Icons.Default.Person, "Mi perfil", "Tus datos y viajes", onNavigateToProfile)
        }
    }
}

@Composable
private fun HomeAction(icon: ImageVector, title: String, subtitle: String, onClick: () -> Unit) {
    AppCard(Modifier.fillMaxWidth().clickable { onClick() }) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            Icon(icon, null, Modifier.size(28.dp), tint = Secondary)
            Spacer(Modifier.width(12.dp))
            Column(Modifier.weight(1f)) {
                Text(title, fontSize = 15.sp, fontWeight = FontWeight.Bold, color = PrimaryLight)
                Text(subtitle, fontSize = 12.sp, color = OnSurfaceVariant)
            }
            Icon(Icons.Default.ChevronRight, null, tint = Outline)
        }
    }
}

@Preview(showBackground = true)
@Composable
fun HomeScreenPreview() {
    RutaConfiableTheme { HomeScreen(role = RegistrationRole.CLIENT, userName = "Ana Gómez") }
}