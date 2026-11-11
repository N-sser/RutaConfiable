package me.mauromax.rutaconfiable.ui.screens

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import me.mauromax.rutaconfiable.ui.components.data.TripRepository
import me.mauromax.rutaconfiable.ui.components.AppBottomBar
import me.mauromax.rutaconfiable.ui.components.AppOutlinedTextField
import me.mauromax.rutaconfiable.ui.components.CardSectionContainer
import me.mauromax.rutaconfiable.ui.components.PrimaryButton
import me.mauromax.rutaconfiable.ui.components.StandardAppTopBar
import me.mauromax.rutaconfiable.ui.theme.*
import java.text.NumberFormat
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale
import java.util.TimeZone

@Composable
fun LoadsScreen(
    role: RegistrationRole = RegistrationRole.DRIVER,
    driverName: String = "Conductor",
    onPublished: () -> Unit = {},
    onNavigateToTrips: () -> Unit = {},
    onNavigateToProfile: () -> Unit = {}
) {
    var origin by remember { mutableStateOf("") }
    var destination by remember { mutableStateOf("") }
    var date by remember { mutableStateOf("") }
    var time by remember { mutableStateOf("") }

    var vehicleName by remember { mutableStateOf("") }
    var plate by remember { mutableStateOf("") }

    var hasCargo by remember { mutableStateOf(true) }
    var cargoKg by remember { mutableStateOf("") }
    var cargoPrice by remember { mutableStateOf("") }

    var hasSeats by remember { mutableStateOf(false) }
    var seats by remember { mutableIntStateOf(2) }
    var seatPrice by remember { mutableStateOf("") }

    var error by remember { mutableStateOf<String?>(null) }

    fun validate(): String? = when {
        origin.isBlank() || destination.isBlank() -> "Indica origen y destino."
        date.isBlank() || time.isBlank() -> "Indica fecha y hora de salida."
        vehicleName.isBlank() || plate.isBlank() -> "Indica vehículo y placa."
        !hasCargo && !hasSeats -> "Activa carga o pasajeros."
        hasCargo && (cargoKg.isBlank() || cargoPrice.isBlank()) -> "Completa capacidad y precio de carga."
        hasSeats && seatPrice.isBlank() -> "Completa el precio por cupo."
        else -> null
    }

    fun publish() {
        val msg = validate()
        if (msg != null) {
            error = msg
            return
        }
        error = null
        val kg = cargoKg.toIntOrNull() ?: 0
        TripRepository.publish(
            AvailableTrip(
                id = TripRepository.nextId(),
                departure = time,
                driver = driverName,
                rating = 0.0,
                verifiedTrips = 0,
                vehicle = vehicleName.trim(),
                vehicleDetail = if (hasCargo) "Carga y flete" else "Solo pasajeros",
                plate = plate.trim().uppercase(),
                cargoFreeKg = if (hasCargo) kg else 0,
                cargoTotalKg = if (hasCargo) maxOf(kg, 1) else 1,
                cargoNote = "Espacio de carga",
                seatsFree = if (hasSeats) seats else 0,
                seatsTotal = if (hasSeats) seats else 0,
                seatsNote = "Cupo a bordo",
                cargoPrice = if (hasCargo) formatCop(cargoPrice) else "-",
                seatPrice = if (hasSeats) formatCop(seatPrice) else "-",
                distance = "—",
                duration = "—",
                road = "${origin.trim()} → ${destination.trim()}",
                origin = origin.trim(),
                destination = destination.trim(),
                date = date,
                hasCargo = hasCargo,
                hasSeats = hasSeats
            )
        )
        onPublished()
    }

    Scaffold(
        containerColor = Background,
        topBar = { StandardAppTopBar(showSecurityBadge = false) },
        bottomBar = {
            AppBottomBar(selectedIndex = 1) {
                when (it) {
                    2 -> onNavigateToTrips()
                    3 -> onNavigateToProfile()
                }
            }
        }
    ) { innerPadding ->
        if (role != RegistrationRole.DRIVER) {
            Box(Modifier.fillMaxSize().padding(innerPadding).padding(24.dp), Alignment.Center) {
                Text(
                    "Solo los conductores pueden publicar viajes.",
                    fontSize = 14.sp, color = OnSurfaceVariant, textAlign = TextAlign.Center
                )
            }
            return@Scaffold
        }

        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .imePadding()
                .verticalScroll(rememberScrollState())
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(14.dp)
        ) {
            Text(
                "Publicar nuevo viaje", fontSize = 22.sp, fontWeight = FontWeight.Bold,
                fontFamily = FontFamily.Serif, color = PrimaryLight
            )

            CardSectionContainer(title = "Trayecto y Salida", icon = Icons.Default.Route) {
                AppOutlinedTextField(origin, { origin = it }, label = "Origen",
                    placeholder = "Ej: Bogotá D.C.", leadingIcon = Icons.Default.RadioButtonUnchecked)
                AppOutlinedTextField(destination, { destination = it }, label = "Destino",
                    placeholder = "Ej: Medellín", leadingIcon = Icons.Default.LocationOn)
                DateField("Fecha de salida", date) { date = it }
                AppOutlinedTextField(time, { time = it }, label = "Hora de salida",
                    placeholder = "08:00 AM", leadingIcon = Icons.Default.Schedule)
            }

            CardSectionContainer(title = "Vehículo", icon = Icons.Default.DirectionsCar) {
                AppOutlinedTextField(vehicleName, { vehicleName = it }, label = "Vehículo",
                    placeholder = "Ej: Toyota Hilux 4x4", leadingIcon = Icons.Default.DirectionsCar)
                AppOutlinedTextField(plate, { plate = it.take(7) }, label = "Placa",
                    placeholder = "ABC123", leadingIcon = Icons.Default.Badge)
            }

            CardSectionContainer(title = "Carga", icon = Icons.Default.Inventory2) {
                SwitchRow("Ofrecer espacio de carga", hasCargo) { hasCargo = it }
                if (hasCargo) {
                    AppOutlinedTextField(cargoKg, { cargoKg = it.filter(Char::isDigit).take(6) },
                        label = "Capacidad disponible (kg)", placeholder = "Ej: 450",
                        leadingIcon = Icons.Default.Scale)
                    AppOutlinedTextField(cargoPrice, { cargoPrice = it.filter(Char::isDigit).take(9) },
                        label = "Precio (COP)", placeholder = "Ej: 55000",
                        leadingIcon = Icons.Default.AttachMoney)
                }
            }

            CardSectionContainer(title = "Pasajeros", icon = Icons.Default.Groups) {
                SwitchRow("Ofrecer cupos", hasSeats) { hasSeats = it }
                if (hasSeats) {
                    Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                        (1..4).forEach { n ->
                            val selected = n == seats
                            Surface(
                                modifier = Modifier.weight(1f).clickable { seats = n },
                                shape = RoundedCornerShape(8.dp),
                                color = if (selected) SecondarySoft else SurfaceSoft,
                                border = BorderStroke(
                                    if (selected) 1.5.dp else 1.dp,
                                    if (selected) Secondary else OutlineVariant
                                )
                            ) {
                                Box(Modifier.padding(vertical = 10.dp), Alignment.Center) {
                                    Text("$n", fontWeight = FontWeight.Bold, color = PrimaryLight)
                                }
                            }
                        }
                    }
                    AppOutlinedTextField(seatPrice, { seatPrice = it.filter(Char::isDigit).take(9) },
                        label = "Precio por cupo (COP)", placeholder = "Ej: 40000",
                        leadingIcon = Icons.Default.AttachMoney)
                }
            }

            error?.let {
                Text(it, fontSize = 12.sp, fontWeight = FontWeight.Bold, color = MaterialTheme.colorScheme.error)
            }

            PrimaryButton(
                text = "Publicar viaje",
                onClick = { publish() },
                leadingIcon = Icons.Default.Publish,
                trailingIcon = null,
                containerColor = SecondaryContainer
            )
            Spacer(Modifier.height(8.dp))
        }
    }
}

@Composable
private fun SwitchRow(label: String, checked: Boolean, onChange: (Boolean) -> Unit) {
    Row(Modifier.fillMaxWidth(), Arrangement.SpaceBetween, Alignment.CenterVertically) {
        Text(label, fontSize = 13.sp, fontWeight = FontWeight.Medium, color = PrimaryLight)
        Switch(
            checked = checked, onCheckedChange = onChange,
            colors = SwitchDefaults.colors(
                checkedThumbColor = CardBackground, checkedTrackColor = SecondaryContainer,
                uncheckedThumbColor = CardBackground, uncheckedTrackColor = Outline
            )
        )
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun DateField(label: String, value: String, onValue: (String) -> Unit) {
    var show by remember { mutableStateOf(false) }
    AppOutlinedTextField(
        value = value, onValueChange = onValue, label = label, placeholder = "dd/mm/aaaa",
        leadingIcon = Icons.Default.Event,
        trailingIcon = {
            IconButton(onClick = { show = true }) { Icon(Icons.Default.CalendarMonth, "Elegir fecha") }
        }
    )
    if (show) {
        val state = rememberDatePickerState()
        DatePickerDialog(
            onDismissRequest = { show = false },
            confirmButton = {
                TextButton(onClick = {
                    state.selectedDateMillis?.let { onValue(formatDate(it)) }
                    show = false
                }) { Text("Aceptar", color = Secondary) }
            },
            dismissButton = {
                TextButton(onClick = { show = false }) { Text("Cancelar", color = PrimaryLight) }
            }
        ) { DatePicker(state = state) }
    }
}

private fun formatDate(millis: Long): String =
    SimpleDateFormat("dd/MM/yyyy", Locale("es", "CO"))
        .apply { timeZone = TimeZone.getTimeZone("UTC") }
        .format(Date(millis))

private fun formatCop(digits: String): String {
    val value = digits.toLongOrNull() ?: return "$ 0"
    return "$ " + NumberFormat.getIntegerInstance(Locale("es", "CO")).format(value)
}

@Preview(showBackground = true)
@Composable
fun LoadsScreenPreview() {
    RutaConfiableTheme { LoadsScreen() }
}