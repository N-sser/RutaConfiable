package me.mauromax.rutaconfiable.ui.screens

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.imePadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AttachMoney
import androidx.compose.material.icons.filled.Badge
import androidx.compose.material.icons.filled.CalendarMonth
import androidx.compose.material.icons.filled.DirectionsCar
import androidx.compose.material.icons.filled.Event
import androidx.compose.material.icons.filled.Groups
import androidx.compose.material.icons.filled.Inventory2
import androidx.compose.material.icons.filled.LocationOn
import androidx.compose.material.icons.filled.Publish
import androidx.compose.material.icons.filled.RadioButtonUnchecked
import androidx.compose.material.icons.filled.Route
import androidx.compose.material.icons.filled.Scale
import androidx.compose.material.icons.filled.Schedule
import androidx.compose.material3.DatePicker
import androidx.compose.material3.DatePickerDialog
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.rememberDatePickerState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import kotlinx.coroutines.launch
import me.mauromax.rutaconfiable.ui.components.AppBottomBar
import me.mauromax.rutaconfiable.ui.components.AppOutlinedTextField
import me.mauromax.rutaconfiable.ui.components.AppSwitch
import me.mauromax.rutaconfiable.ui.components.CardSectionContainer
import me.mauromax.rutaconfiable.ui.components.PrimaryButton
import me.mauromax.rutaconfiable.ui.components.StandardAppTopBar
import me.mauromax.rutaconfiable.ui.components.data.TripRepository
import me.mauromax.rutaconfiable.ui.theme.Background
import me.mauromax.rutaconfiable.ui.theme.OnSurfaceVariant
import me.mauromax.rutaconfiable.ui.theme.OutlineVariant
import me.mauromax.rutaconfiable.ui.theme.PrimaryLight
import me.mauromax.rutaconfiable.ui.theme.RutaConfiableTheme
import me.mauromax.rutaconfiable.ui.theme.Secondary
import me.mauromax.rutaconfiable.ui.theme.SecondaryContainer
import me.mauromax.rutaconfiable.ui.theme.SecondarySoft
import me.mauromax.rutaconfiable.ui.theme.SurfaceSoft
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
    onNavigateToHome: () -> Unit = {},
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
    var isPublishing by remember { mutableStateOf(false) }
    val scope = rememberCoroutineScope()

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
        isPublishing = true
        val kg = cargoKg.toIntOrNull() ?: 0
        val newTrip = AvailableTrip(
            id = "", // el código lo asigna TripRepository al guardar
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
        scope.launch {
            TripRepository.publish(newTrip)
                .onSuccess {
                    isPublishing = false
                    onPublished()
                }
                .onFailure { e ->
                    isPublishing = false
                    error = TripRepository.friendlyError(e)
                }
        }
    }

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
                text = if (isPublishing) "Publicando..." else "Publicar viaje",
                onClick = { publish() },
                enabled = !isPublishing,
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
        AppSwitch(checked, onChange)
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