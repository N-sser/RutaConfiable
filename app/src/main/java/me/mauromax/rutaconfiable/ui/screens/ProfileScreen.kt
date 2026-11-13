package me.mauromax.rutaconfiable.ui.screens

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.Logout
import androidx.compose.material.icons.filled.AccountCircle
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Event
import androidx.compose.material.icons.filled.Inventory2
import androidx.compose.material.icons.filled.LocalShipping
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.filled.Star
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import me.mauromax.rutaconfiable.ui.components.AppBottomBar
import me.mauromax.rutaconfiable.ui.components.AppCard
import me.mauromax.rutaconfiable.ui.components.PrimaryButton
import me.mauromax.rutaconfiable.ui.components.StatusTag
import me.mauromax.rutaconfiable.ui.components.data.Booking
import me.mauromax.rutaconfiable.ui.theme.Background
import me.mauromax.rutaconfiable.ui.theme.CardBackground
import me.mauromax.rutaconfiable.ui.theme.OnSecondaryContainer
import me.mauromax.rutaconfiable.ui.theme.OnSurfaceVariant
import me.mauromax.rutaconfiable.ui.theme.Outline
import me.mauromax.rutaconfiable.ui.theme.OutlineVariant
import me.mauromax.rutaconfiable.ui.theme.PrimaryLight
import me.mauromax.rutaconfiable.ui.theme.RutaConfiableTheme
import me.mauromax.rutaconfiable.ui.theme.Secondary
import me.mauromax.rutaconfiable.ui.theme.SecondaryContainer
import me.mauromax.rutaconfiable.ui.theme.StatusPending
import me.mauromax.rutaconfiable.ui.theme.StatusPendingBg
import me.mauromax.rutaconfiable.ui.theme.SurfaceSoft
import me.mauromax.rutaconfiable.ui.theme.SurfaceVariant

/** Contadores del perfil. Un usuario nuevo empieza con todo en 0. */
data class ProfileStats(
    val active: Int = 0,
    val inProgress: Int = 0,
    val completed: Int = 0,
    val rating: Double = 0.0,
    val ratingCount: Int = 0
)

data class NextTrip(
    val id: String, val leavesIn: String, val from: String, val to: String,
    val departure: String, val arrival: String, val duration: String, val via: String,
    val inspection: Float, val vehicle: String, val cargo: String, val seats: String
)

data class Trip(
    val date: String, val from: String, val to: String,
    val tag: String, val tagBg: Color, val tagFg: Color,
    val price: String, val info: String
)

@Composable
fun ProfileScreen(
    role: RegistrationRole = RegistrationRole.DRIVER,
    userName: String = "Usuario",
    stats: ProfileStats = ProfileStats(),
    nextTrip: NextTrip? = null,
    trips: List<Trip> = emptyList(),
    bookings: List<Booking> = emptyList(),
    onLogout: () -> Unit = {},
    onNavigateToHome: () -> Unit = {},
    onNavigateToTrips: () -> Unit = {},
    onNavigateToLoads: () -> Unit = {}
) {
    val isDriver = role == RegistrationRole.DRIVER

    Scaffold(
        containerColor = Background,
        bottomBar = {
            AppBottomBar(selectedIndex = 3) {
                when (it) {
                    0 -> onNavigateToHome()
                    1 -> onNavigateToLoads()
                    2 -> onNavigateToTrips()
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
            // Encabezado
            Row(verticalAlignment = Alignment.CenterVertically) {
                Icon(Icons.Default.AccountCircle, null, Modifier.size(44.dp), tint = PrimaryLight)
                Spacer(Modifier.width(10.dp))
                Column(Modifier.weight(1f)) {
                    Text("Bienvenido", fontSize = 12.sp, color = OnSurfaceVariant)
                    Text(
                        userName, fontSize = 18.sp, fontWeight = FontWeight.Bold,
                        color = PrimaryLight, fontFamily = FontFamily.Serif
                    )
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(Icons.Default.Star, null, Modifier.size(14.dp), tint = Secondary)
                        Spacer(Modifier.width(2.dp))
                        Text(
                            "%.1f · %d calificaciones".format(stats.rating, stats.ratingCount),
                            fontSize = 11.sp, color = OnSurfaceVariant
                        )
                    }
                }
                StatusTag(
                    text = if (isDriver) "Conductor" else "Cliente",
                    bgColor = PrimaryLight, textColor = Color.White
                )
                IconButton(onClick = onLogout) {
                    Icon(Icons.AutoMirrored.Filled.Logout, "Cerrar sesión", tint = PrimaryLight)
                }
            }

            // Resumen
            Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                StatCard("Viajes activos", stats.active, Modifier.weight(1f))
                StatCard("En curso", stats.inProgress, Modifier.weight(1f))
                StatCard("Completados", stats.completed, Modifier.weight(1f))
            }

            // Próximo viaje
            SectionHeader("Próximo Viaje Asignado") {
                nextTrip?.let { StatusTag(it.leavesIn, StatusPendingBg, StatusPending) }
            }
            if (nextTrip != null) NextTripCard(nextTrip)
            else EmptyState(
                Icons.Default.LocalShipping,
                if (isDriver) "Aún no tienes viajes asignados." else "Aún no tienes reservas ni envíos."
            )

            // Acciones rápidas
            Row(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                if (isDriver) {
                    QuickAction(
                        icon = Icons.Default.Add,
                        title = "Publicar nuevo viaje",
                        subtitle = "Ofrecer flete o ruta",
                        modifier = Modifier.weight(1f),
                        onClick = onNavigateToLoads
                    )
                }
                QuickAction(
                    icon = Icons.Default.Search,
                    title = "Buscar trayectos",
                    subtitle = "Envíos y pasajes libres",
                    modifier = Modifier.weight(1f),
                    onClick = onNavigateToTrips
                )
            }

            // Viajes recientes
            SectionHeader("Viajes Programados Recientes") {
                if (trips.isNotEmpty()) {
                    Text(
                        "Ver todos (${trips.size})", fontSize = 12.sp,
                        fontWeight = FontWeight.Bold, color = Secondary,
                        modifier = Modifier.clickable { onNavigateToTrips() })
                }
            }
            if (trips.isEmpty()) EmptyState(
                Icons.Default.Event,
                "Aún no tienes viajes programados."
            )
            else trips.forEach { TripRow(it) }


            // Reservas recibidas (solo conductor)
            if (isDriver) {
                    SectionHeader("Reservas Recibidas") {}
                    if (bookings.isEmpty()) EmptyState(Icons.Default.Inventory2, "Aún no tienes reservas.")
                    else bookings.forEach { BookingCard(it, showClient = true) }
                }

        }
    }
}

@Composable
private fun EmptyState(icon: ImageVector, text: String) {
    AppCard(Modifier.fillMaxWidth()) {
        Column(Modifier.fillMaxWidth(), horizontalAlignment = Alignment.CenterHorizontally) {
            Icon(icon, null, Modifier.size(28.dp), tint = Outline)
            Spacer(Modifier.height(6.dp))
            Text(text, fontSize = 12.sp, color = OnSurfaceVariant, textAlign = TextAlign.Center)
        }
    }
}

@Composable
private fun SectionHeader(title: String, trailing: @Composable () -> Unit) {
    Row(Modifier.fillMaxWidth(), Arrangement.SpaceBetween, Alignment.CenterVertically) {
        Text(
            title, fontSize = 17.sp, fontWeight = FontWeight.Bold,
            color = PrimaryLight, fontFamily = FontFamily.Serif
        )
        trailing()
    }
}

@Composable
private fun StatCard(label: String, value: Int, modifier: Modifier = Modifier) {
    AppCard(modifier) {
        Text(label, fontSize = 11.sp, color = OnSurfaceVariant)
        Text(
            value.toString(), fontSize = 26.sp, fontWeight = FontWeight.Bold,
            color = PrimaryLight, fontFamily = FontFamily.Serif
        )
    }
}

@Composable
private fun NextTripCard(t: NextTrip) {
    Surface(
        shape = RoundedCornerShape(12.dp),
        color = CardBackground,
        border = BorderStroke(1.dp, OutlineVariant),
        shadowElevation = 1.dp
    ) {
        Column {
            Row(
                Modifier
                    .fillMaxWidth()
                    .background(PrimaryLight)
                    .padding(horizontal = 14.dp, vertical = 10.dp),
                Arrangement.SpaceBetween, Alignment.CenterVertically
            ) {
                Text(
                    "ID: ${t.id}",
                    color = Color.White,
                    fontSize = 13.sp,
                    fontWeight = FontWeight.Bold
                )
                StatusTag("Confirmado · Seguro", SecondaryContainer, OnSecondaryContainer)
            }
            Column(Modifier.padding(14.dp), verticalArrangement = Arrangement.spacedBy(12.dp)) {
                Row(Modifier.fillMaxWidth(), Arrangement.SpaceBetween, Alignment.CenterVertically) {
                    Column {
                        Text("Salida", fontSize = 11.sp, color = OnSurfaceVariant)
                        Text(
                            t.from,
                            fontSize = 16.sp,
                            fontWeight = FontWeight.Bold,
                            color = PrimaryLight
                        )
                        Text(t.departure, fontSize = 11.sp, color = OnSurfaceVariant)
                    }
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Text(t.duration, fontSize = 11.sp, color = OnSurfaceVariant)
                        Icon(Icons.Default.LocalShipping, null, tint = Secondary)
                        Text(t.via, fontSize = 11.sp, color = OnSurfaceVariant)
                    }
                    Column(horizontalAlignment = Alignment.End) {
                        Text("Llegada", fontSize = 11.sp, color = OnSurfaceVariant)
                        Text(
                            t.to,
                            fontSize = 16.sp,
                            fontWeight = FontWeight.Bold,
                            color = PrimaryLight
                        )
                        Text(t.arrival, fontSize = 11.sp, color = OnSurfaceVariant)
                    }
                }
                Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
                    Row(Modifier.fillMaxWidth(), Arrangement.SpaceBetween) {
                        Text(
                            "Inspección de carga",
                            fontSize = 12.sp,
                            fontWeight = FontWeight.SemiBold
                        )
                        Text(
                            "${(t.inspection * 100).toInt()}% listo", fontSize = 12.sp,
                            fontWeight = FontWeight.Bold, color = Secondary
                        )
                    }
                    LinearProgressIndicator(
                        progress = { t.inspection },
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(6.dp),
                        color = SecondaryContainer,
                        trackColor = SurfaceVariant
                    )
                    Text("Vehículo: ${t.vehicle}", fontSize = 11.sp, color = OnSurfaceVariant)
                }
                Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    InfoBox("Carga reservada", t.cargo, Modifier.weight(1f))
                    InfoBox("Cupos pasajeros", t.seats, Modifier.weight(1f))
                }
                PrimaryButton(text = "Ver Manifiesto y Hoja de Ruta", onClick = {})
            }
        }
    }
}

@Composable
private fun InfoBox(label: String, value: String, modifier: Modifier = Modifier) {
    Surface(modifier, color = SurfaceSoft, shape = RoundedCornerShape(8.dp)) {
        Column(Modifier.padding(10.dp)) {
            Text(label, fontSize = 11.sp, color = OnSurfaceVariant)
            Text(value, fontSize = 13.sp, fontWeight = FontWeight.Bold, color = PrimaryLight)
        }
    }
}

@Composable
private fun QuickAction(
    icon: ImageVector,
    title: String,
    subtitle: String,
    modifier: Modifier = Modifier,
    onClick: () -> Unit = {}
) {
    AppCard(modifier.clickable { onClick() }) {
        Icon(icon, null, tint = Secondary, modifier = Modifier.fillMaxWidth())
        Spacer(Modifier.height(6.dp))
        Text(
            title,
            modifier = Modifier.fillMaxWidth(),
            textAlign = TextAlign.Center,
            fontSize = 13.sp,
            fontWeight = FontWeight.Bold,
            color = PrimaryLight
        )
        Text(
            subtitle,
            modifier = Modifier.fillMaxWidth(),
            textAlign = TextAlign.Center,
            fontSize = 11.sp,
            color = OnSurfaceVariant
        )
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

// Usuario nuevo: todo en 0 y sin viajes
@Preview(showBackground = true)
@Composable
fun ProfileScreenPreview() {
    RutaConfiableTheme { ProfileScreen() }
}