package me.mauromax.rutaconfiable.ui.screens


import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.Logout
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import me.mauromax.rutaconfiable.ui.components.PrimaryButton
import me.mauromax.rutaconfiable.ui.components.StatusTag
import me.mauromax.rutaconfiable.ui.theme.*

private data class Trip(
    val date: String, val from: String, val to: String,
    val tag: String, val tagBg: Color, val tagFg: Color,
    val price: String, val info: String
)

private val recentTrips = listOf(
    Trip("Mañana · 08:00 AM", "Cali, Valle del Cauca", "Armenia, Quindío",
        "Cupo Libre", StatusAvailableBg, StatusAvailable, "$ 45.000", "Carga disponible: 1.2 Ton"),
    Trip("Viernes, 24 Oct · 11:00 PM", "Bucaramanga, Santander", "Santa Marta, Magdalena",
        "Carga Completa", StatusBookedBg, StatusBooked, "$ 850.000", "Flete pactado 10 Ton"),
    Trip("Sábado, 25 Oct · 05:00 AM", "Pereira, Risaralda", "Manizales, Caldas",
        "Cupo Libre", StatusAvailableBg, StatusAvailable, "$ 22.000", "Paquetería liviana")
)

@Composable
fun ProfileScreen(
    role: RegistrationRole = RegistrationRole.DRIVER,
    onLogout: () -> Unit = {}
) {
    Scaffold(
        containerColor = Background,
        bottomBar = { DashboardBottomBar() }
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .verticalScroll(rememberScrollState())
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            // Encabezado
            Row(verticalAlignment = Alignment.CenterVertically) {
                Icon(Icons.Default.AccountCircle, null, Modifier.size(44.dp), tint = PrimaryLight)
                Spacer(Modifier.width(10.dp))
                Column(Modifier.weight(1f)) {
                    Text("Bienvenido de nuevo", fontSize = 12.sp, color = OnSurfaceVariant)
                    Text("Carlos Méndez", fontSize = 18.sp, fontWeight = FontWeight.Bold,
                        color = PrimaryLight, fontFamily = FontFamily.Serif)
                }
                StatusTag(
                    text = if (role == RegistrationRole.DRIVER) "Conductor PRO" else "Cliente",
                    bgColor = PrimaryLight, textColor = Color.White
                )
                IconButton(onClick = onLogout) {
                    Icon(Icons.AutoMirrored.Filled.Logout, "Cerrar sesión", tint = PrimaryLight)
                }
            }

            // Resumen
            Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                StatCard("Viajes activos", "02", Modifier.weight(1f))
                StatCard("En curso", "01", Modifier.weight(1f))
                StatCard("Completados", "18", Modifier.weight(1f))
            }

            // Próximo viaje
            SectionHeader("Próximo Viaje Asignado") {
                StatusTag("Sale en 45 min", StatusPendingBg, StatusPending)
            }
            NextTripCard()

            // Acciones rápidas
            Row(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                QuickAction(Icons.Default.Add, "Publicar nuevo viaje",
                    "Ofrecer flete o ruta", Modifier.weight(1f))
                QuickAction(Icons.Default.Search, "Buscar trayectos",
                    "Envíos y pasajes libres", Modifier.weight(1f))
            }

            // Viajes recientes
            SectionHeader("Viajes Programados Recientes") {
                Text("Ver todos (6)", fontSize = 12.sp, fontWeight = FontWeight.Bold, color = Secondary)
            }
            recentTrips.forEach { TripRow(it) }
        }
    }
}

@Composable
private fun AppCard(modifier: Modifier = Modifier, content: @Composable ColumnScope.() -> Unit) {
    Surface(
        modifier = modifier,
        color = Color.White,
        shape = RoundedCornerShape(12.dp),
        border = BorderStroke(1.dp, OutlineVariant),
        shadowElevation = 1.dp
    ) { Column(Modifier.padding(14.dp), content = content) }
}

@Composable
private fun SectionHeader(title: String, trailing: @Composable () -> Unit) {
    Row(Modifier.fillMaxWidth(), Arrangement.SpaceBetween, Alignment.CenterVertically) {
        Text(title, fontSize = 17.sp, fontWeight = FontWeight.Bold,
            color = PrimaryLight, fontFamily = FontFamily.Serif)
        trailing()
    }
}

@Composable
private fun StatCard(label: String, value: String, modifier: Modifier = Modifier) {
    AppCard(modifier) {
        Text(label, fontSize = 11.sp, color = OnSurfaceVariant)
        Text(value, fontSize = 26.sp, fontWeight = FontWeight.Bold,
            color = PrimaryLight, fontFamily = FontFamily.Serif)
    }
}

@Composable
private fun NextTripCard() {
    Surface(
        shape = RoundedCornerShape(12.dp),
        color = Color.White,
        border = BorderStroke(1.dp, OutlineVariant),
        shadowElevation = 1.dp
    ) {
        Column {
            Row(
                Modifier.fillMaxWidth().background(PrimaryLight).padding(horizontal = 14.dp, vertical = 10.dp),
                Arrangement.SpaceBetween, Alignment.CenterVertically
            ) {
                Text("ID: RTA-4820B", color = Color.White, fontSize = 13.sp, fontWeight = FontWeight.Bold)
                StatusTag("Confirmado · Seguro", SecondaryContainer, OnSecondaryContainer)
            }
            Column(Modifier.padding(14.dp), verticalArrangement = Arrangement.spacedBy(12.dp)) {
                Row(Modifier.fillMaxWidth(), Arrangement.SpaceBetween, Alignment.CenterVertically) {
                    Column {
                        Text("Terminal salida", fontSize = 11.sp, color = OnSurfaceVariant)
                        Text("Bogotá D.C.", fontSize = 16.sp, fontWeight = FontWeight.Bold, color = PrimaryLight)
                        Text("06:00 AM · Hoy", fontSize = 11.sp, color = OnSurfaceVariant)
                    }
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Text("8h 15m", fontSize = 11.sp, color = OnSurfaceVariant)
                        Icon(Icons.Default.LocalShipping, null, tint = Secondary)
                        Text("Vía Guaduas", fontSize = 11.sp, color = OnSurfaceVariant)
                    }
                    Column(horizontalAlignment = Alignment.End) {
                        Text("Terminal norte", fontSize = 11.sp, color = OnSurfaceVariant)
                        Text("Medellín", fontSize = 16.sp, fontWeight = FontWeight.Bold, color = PrimaryLight)
                        Text("03:15 PM · Est.", fontSize = 11.sp, color = OnSurfaceVariant)
                    }
                }
                Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
                    Row(Modifier.fillMaxWidth(), Arrangement.SpaceBetween) {
                        Text("Inspección de carga completada", fontSize = 12.sp, fontWeight = FontWeight.SemiBold)
                        Text("85% listo", fontSize = 12.sp, fontWeight = FontWeight.Bold, color = Secondary)
                    }
                    LinearProgressIndicator(
                        progress = { 0.85f },
                        modifier = Modifier.fillMaxWidth().height(6.dp),
                        color = SecondaryContainer,
                        trackColor = SurfaceVariant
                    )
                    Text("Vehículo: Kenworth T800 (WTM-294)", fontSize = 11.sp, color = OnSurfaceVariant)
                }
                Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    InfoBox("Carga reservada", "3.8 / 5.0 Toneladas", Modifier.weight(1f))
                    InfoBox("Cupos pasajeros", "14 / 16 ocupados", Modifier.weight(1f))
                }
                PrimaryButton(text = "Ver Manifiesto y Hoja de Ruta", onClick = {})
            }
        }
    }
}

@Composable
private fun InfoBox(label: String, value: String, modifier: Modifier = Modifier) {
    Surface(modifier, color = Color(0xFFEFF4FF), shape = RoundedCornerShape(8.dp)) {
        Column(Modifier.padding(10.dp)) {
            Text(label, fontSize = 11.sp, color = OnSurfaceVariant)
            Text(value, fontSize = 13.sp, fontWeight = FontWeight.Bold, color = PrimaryLight)
        }
    }
}

@Composable
private fun QuickAction(icon: ImageVector, title: String, subtitle: String, modifier: Modifier = Modifier) {
    AppCard(modifier) {
        Icon(icon, null, tint = Secondary)
        Spacer(Modifier.height(6.dp))
        Text(title, fontSize = 13.sp, fontWeight = FontWeight.Bold, color = PrimaryLight)
        Text(subtitle, fontSize = 11.sp, color = OnSurfaceVariant)
    }
}

@Composable
private fun TripRow(t: Trip) {
    AppCard(Modifier.fillMaxWidth()) {
        Row(Modifier.fillMaxWidth(), Arrangement.SpaceBetween, Alignment.CenterVertically) {
            Text(t.date, fontSize = 11.sp, color = OnSurfaceVariant)
            StatusTag(t.tag, t.tagBg, t.tagFg)
        }
        Spacer(Modifier.height(8.dp))
        Row(Modifier.fillMaxWidth(), Arrangement.SpaceBetween, Alignment.Bottom) {
            Column {
                Text(t.from, fontSize = 14.sp, fontWeight = FontWeight.Bold, color = PrimaryLight)
                Text(t.to, fontSize = 14.sp, fontWeight = FontWeight.Bold, color = PrimaryLight)
            }
            Column(horizontalAlignment = Alignment.End) {
                Text("Tarifa", fontSize = 10.sp, color = OnSurfaceVariant)
                Text(t.price, fontSize = 16.sp, fontWeight = FontWeight.Bold, color = Secondary)
            }
        }
        Spacer(Modifier.height(6.dp))
        Text(t.info, fontSize = 11.sp, color = OnSurfaceVariant)
    }
}

@Composable
private fun DashboardBottomBar() {
    NavigationBar(containerColor = Color.White) {
        listOf(
            Icons.Default.Home to "Inicio",
            Icons.Default.Inventory2 to "Cargas",
            Icons.Default.Map to "Viajes",
            Icons.Default.Person to "Perfil"
        ).forEachIndexed { i, (icon, label) ->
            NavigationBarItem(
                selected = i == 3,
                onClick = {},
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

@Preview(showBackground = true)
@Composable
fun ProfileScreenPreview() {
    RutaConfiableTheme { ProfileScreen() }
}