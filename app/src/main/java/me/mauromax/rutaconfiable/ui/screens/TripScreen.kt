package me.mauromax.rutaconfiable.ui.screens

import android.Manifest
import android.content.Context
import android.content.pm.PackageManager
import android.location.Location
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.content.ContextCompat
import androidx.compose.ui.platform.LocalContext
import com.google.android.gms.location.LocationServices
import com.google.android.gms.location.LocationCallback
import com.google.android.gms.location.LocationRequest
import com.google.android.gms.location.LocationResult
import com.google.android.gms.location.Priority
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.Chat
import androidx.compose.material.icons.automirrored.filled.Sort
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import me.mauromax.rutaconfiable.ui.components.data.TripRepository
import me.mauromax.rutaconfiable.ui.components.AppBottomBar
import me.mauromax.rutaconfiable.ui.components.AppCard
import me.mauromax.rutaconfiable.ui.components.AppSwitch
import me.mauromax.rutaconfiable.ui.components.AppOutlinedTextField
import me.mauromax.rutaconfiable.ui.components.PrimaryButton
import me.mauromax.rutaconfiable.ui.components.StandardAppTopBar
import me.mauromax.rutaconfiable.ui.components.StatusTag
import me.mauromax.rutaconfiable.ui.theme.*

data class AvailableTrip(
    val id: String,
    val departure: String,
    val driver: String,
    val rating: Double,
    val verifiedTrips: Int,
    val vehicle: String,
    val vehicleDetail: String,
    val plate: String,
    val cargoFreeKg: Int,
    val cargoTotalKg: Int,
    val cargoNote: String,
    val seatsFree: Int,
    val seatsTotal: Int,
    val seatsNote: String,
    val cargoPrice: String,
    val seatPrice: String,
    val distance: String,
    val duration: String,
    val road: String,
    val origin: String = "",
    val destination: String = "",
    val date: String = "",
    val hasCargo: Boolean = true,
    val hasSeats: Boolean = true
)

@Composable
fun TripScreen(
    role: RegistrationRole = RegistrationRole.DRIVER,
    trips: List<AvailableTrip> = TripRepository.trips,
    onNavigateToLoads: () -> Unit = {},
    onNavigateToProfile: () -> Unit = {}
) {
    var origin by remember { mutableStateOf("") }
    var destination by remember { mutableStateOf("") }
    var date by remember { mutableStateOf("") }
    var time by remember { mutableStateOf("") }
    var vehicle by remember { mutableStateOf<String?>(null) }
    var withCargo by remember { mutableStateOf(true) }
    var withPassengers by remember { mutableStateOf(true) }
    var selectedId by remember { mutableStateOf<String?>(null) }

    val context = LocalContext.current
    var isGettingLocation by remember { mutableStateOf(false) }
    var liveLocation by remember { mutableStateOf<String?>(null) }
    var isTrackingLive by remember { mutableStateOf(false) }

    DisposableEffect(isTrackingLive) {
        if (!isTrackingLive) return@DisposableEffect onDispose {}

        val hasFine = ContextCompat.checkSelfPermission(context, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED
        val hasCoarse = ContextCompat.checkSelfPermission(context, Manifest.permission.ACCESS_COARSE_LOCATION) == PackageManager.PERMISSION_GRANTED

        if (!hasFine && !hasCoarse) {
            isTrackingLive = false
            return@DisposableEffect onDispose {}
        }

        val fusedClient = LocationServices.getFusedLocationProviderClient(context)
        val request = LocationRequest.Builder(Priority.PRIORITY_HIGH_ACCURACY, 10000L)
            .setMinUpdateIntervalMillis(5000L)
            .build()

        val callback = object : LocationCallback() {
            override fun onLocationResult(result: LocationResult) {
                result.lastLocation?.let { location ->
                    liveLocation = "Lat: %.4f, Lng: %.4f".format(location.latitude, location.longitude)
                }
            }
        }

        try {
            fusedClient.requestLocationUpdates(request, callback, android.os.Looper.getMainLooper())
        } catch (e: SecurityException) {
            // Ignored
        }

        onDispose {
            try {
                fusedClient.removeLocationUpdates(callback)
            } catch (e: Exception) {}
        }
    }

    val permissionLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.RequestMultiplePermissions()
    ) { permissions ->
        val granted = permissions[Manifest.permission.ACCESS_FINE_LOCATION] == true ||
                permissions[Manifest.permission.ACCESS_COARSE_LOCATION] == true
        if (granted) {
            isGettingLocation = true
            fetchCurrentLocation(context) { locStr ->
                origin = locStr
                isGettingLocation = false
            }
        }
    }

    val results = trips.filter { t ->
        val matchesOrigin = origin.isBlank() || t.origin.contains(origin.trim(), ignoreCase = true)
        val matchesDestination = destination.isBlank() || t.destination.contains(destination.trim(), ignoreCase = true)
        val matchesType = when {
            !withCargo && !withPassengers -> true
            else -> (withCargo && t.hasCargo) || (withPassengers && t.hasSeats)
        }
        matchesOrigin && matchesDestination && matchesType
    }
    val selected = results.find { it.id == selectedId } ?: results.firstOrNull()
    val others = results.filter { it.id != selected?.id }

    Scaffold(
        containerColor = Background,
        topBar = { StandardAppTopBar(showSecurityBadge = false) },
        bottomBar = {
            AppBottomBar(selectedIndex = 2) {
                when (it) {
                    1 -> onNavigateToLoads()
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
            // Live GPS Tracking Banner
            Surface(
                modifier = Modifier.fillMaxWidth(),
                color = SurfaceSoft,
                shape = RoundedCornerShape(12.dp),
                border = BorderStroke(1.dp, OutlineVariant)
            ) {
                Row(
                    modifier = Modifier.padding(12.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically, modifier = Modifier.weight(1f)) {
                        Icon(
                            imageVector = Icons.Default.MyLocation,
                            contentDescription = null,
                            tint = if (isTrackingLive) StatusAvailable else Outline,
                            modifier = Modifier.size(24.dp)
                        )
                        Spacer(modifier = Modifier.width(10.dp))
                        Column {
                            Text(
                                text = if (isTrackingLive) "GPS en Tiempo Real Activo" else "Monitoreo GPS en Vivo",
                                fontSize = 13.sp,
                                fontWeight = FontWeight.Bold,
                                color = PrimaryLight
                            )
                            Text(
                                text = liveLocation ?: (if (isTrackingLive) "Obteniendo posición..." else "Pulsa para iniciar seguimiento"),
                                fontSize = 11.sp,
                                color = OnSurfaceVariant
                            )
                        }
                    }
                    Switch(
                        checked = isTrackingLive,
                        onCheckedChange = { checked ->
                            val hasFine = ContextCompat.checkSelfPermission(context, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED
                            val hasCoarse = ContextCompat.checkSelfPermission(context, Manifest.permission.ACCESS_COARSE_LOCATION) == PackageManager.PERMISSION_GRANTED

                            if (checked && !hasFine && !hasCoarse) {
                                permissionLauncher.launch(
                                    arrayOf(
                                        Manifest.permission.ACCESS_FINE_LOCATION,
                                        Manifest.permission.ACCESS_COARSE_LOCATION
                                    )
                                )
                            } else {
                                isTrackingLive = checked
                            }
                        },
                        colors = SwitchDefaults.colors(
                            checkedThumbColor = CardBackground,
                            checkedTrackColor = StatusAvailable,
                            uncheckedThumbColor = CardBackground,
                            uncheckedTrackColor = Outline
                        )
                    )
                }
            }

            // Búsqueda
            TripBox {
                AppOutlinedTextField(
                    value = origin,
                    onValueChange = { origin = it },
                    label = "En origen",
                    leadingIcon = Icons.Default.LocationOn,
                    trailingIcon = {
                        IconButton(
                            onClick = {
                                val hasFine = ContextCompat.checkSelfPermission(
                                    context, Manifest.permission.ACCESS_FINE_LOCATION
                                ) == PackageManager.PERMISSION_GRANTED
                                val hasCoarse = ContextCompat.checkSelfPermission(
                                    context, Manifest.permission.ACCESS_COARSE_LOCATION
                                ) == PackageManager.PERMISSION_GRANTED

                                if (hasFine || hasCoarse) {
                                    isGettingLocation = true
                                    fetchCurrentLocation(context) { locStr ->
                                        origin = locStr
                                        isGettingLocation = false
                                    }
                                } else {
                                    permissionLauncher.launch(
                                        arrayOf(
                                            Manifest.permission.ACCESS_FINE_LOCATION,
                                            Manifest.permission.ACCESS_COARSE_LOCATION
                                        )
                                    )
                                }
                            }
                        ) {
                            if (isGettingLocation) {
                                CircularProgressIndicator(
                                    modifier = Modifier.size(18.dp),
                                    strokeWidth = 2.dp,
                                    color = Secondary
                                )
                            } else {
                                Icon(
                                    imageVector = Icons.Default.GpsFixed,
                                    contentDescription = "Obtener ubicación GPS",
                                    tint = Secondary
                                )
                            }
                        }
                    }
                )
                AppOutlinedTextField(destination, { destination = it }, label = "Para (destino)",
                    leadingIcon = Icons.Default.Place,
                    trailingIcon = { Icon(Icons.Default.SwapVert, null, tint = Outline) })
                AppOutlinedTextField(date, { date = it }, label = "Fecha de salida",
                    leadingIcon = Icons.Default.CalendarMonth)
                AppOutlinedTextField(time, { time = it }, label = "Horario",
                    leadingIcon = Icons.Default.Schedule)
            }

            // Filtros
            AppCard(Modifier.fillMaxWidth(), spacing = 10.dp) {
                Row(Modifier.fillMaxWidth(), Arrangement.SpaceBetween, Alignment.CenterVertically) {
                    Text("FILTROS DE CAPACIDAD Y TRAYECTO", fontSize = 10.sp,
                        fontWeight = FontWeight.Bold, color = OnSurfaceVariant)
                    Text("Restablecer", fontSize = 11.sp, fontWeight = FontWeight.Bold, color = Secondary,
                        modifier = Modifier.clickable {
                            vehicle = null; withCargo = true; withPassengers = true
                        })
                }
                Row(Modifier.horizontalScroll(rememberScrollState()), Arrangement.spacedBy(8.dp)) {
                    listOf("Liviano", "Camión", "Bi-pasajeros", "Van").forEach { v ->
                        FilterChip(
                            selected = vehicle == v,
                            onClick = { vehicle = if (vehicle == v) null else v },
                            label = { Text(v, fontSize = 12.sp) },
                            colors = FilterChipDefaults.filterChipColors(
                                containerColor = CardBackground,
                                labelColor = OnSurfaceVariant,
                                selectedContainerColor = SecondarySoft,
                                selectedLabelColor = PrimaryLight
                            )
                        )
                    }
                }
                Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    ToggleBox(Icons.Default.Inventory2, "Carga", withCargo, { withCargo = it }, Modifier.weight(1f))
                    ToggleBox(Icons.Default.Groups, "Pasajeros", withPassengers, { withPassengers = it }, Modifier.weight(1f))
                }
            }

            // Encabezado de resultados
            Row(Modifier.fillMaxWidth(), Arrangement.SpaceBetween, Alignment.CenterVertically) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Text("Viajes Disponibles", fontSize = 17.sp, fontWeight = FontWeight.Bold,
                        color = PrimaryLight, fontFamily = FontFamily.Serif)
                    Spacer(Modifier.width(8.dp))
                    StatusTag("${results.size} DISPONIBLES", SurfaceMuted, PrimaryLight)
                }
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(Icons.AutoMirrored.Filled.Sort, null, Modifier.size(14.dp), tint = OnSurfaceVariant)
                    Spacer(Modifier.width(4.dp))
                    Text("Ordenar", fontSize = 11.sp, color = OnSurfaceVariant)
                }
            }

            if (selected != null) {
                TripDetailCard(selected)
            } else {
                AppCard(Modifier.fillMaxWidth(), spacing = 10.dp) {
                    Icon(Icons.Default.Map, null, Modifier.size(32.dp).align(Alignment.CenterHorizontally), tint = Outline)
                    Text(
                        if (trips.isEmpty()) "Aún no hay viajes publicados."
                        else "No hay viajes que coincidan con tu búsqueda.",
                        fontSize = 12.sp, color = OnSurfaceVariant, textAlign = TextAlign.Center,
                        modifier = Modifier.fillMaxWidth()
                    )
                }
            }

            // Otras salidas
            if (others.isNotEmpty()) {
                Row(Modifier.fillMaxWidth(), Arrangement.SpaceBetween, Alignment.CenterVertically) {
                    Text("Otras Salidas", fontSize = 15.sp, fontWeight = FontWeight.Bold,
                        color = PrimaryLight, fontFamily = FontFamily.Serif)
                    Text("${others.size} alternativas", fontSize = 11.sp, color = OnSurfaceVariant)
                }
                others.forEach { trip -> OtherDepartureRow(trip) { selectedId = trip.id } }
            }
        }
    }
}

@Composable
private fun TripDetailCard(t: AvailableTrip) {
    Surface(
        shape = RoundedCornerShape(12.dp),
        color = CardBackground,
        border = BorderStroke(1.dp, OutlineVariant),
        shadowElevation = 1.dp
    ) {
        Column {
            // Cabecera oscura
            Row(
                Modifier.fillMaxWidth().background(PrimaryLight).padding(horizontal = 14.dp, vertical = 10.dp),
                Arrangement.SpaceBetween, Alignment.CenterVertically
            ) {
                Text("VIAJE SELECCIONADO • EN DETALLE", color = OnPrimary, fontSize = 10.sp,
                    fontWeight = FontWeight.Bold)
                StatusTag(t.id, SurfaceMuted, PrimaryLight)
            }

            Column(Modifier.padding(14.dp), verticalArrangement = Arrangement.spacedBy(12.dp)) {
                RouteMapPreview(t.distance, t.duration)

                if (t.origin.isNotBlank()) {
                    Text(
                        "${t.origin} → ${t.destination} · ${t.date} · ${t.departure}",
                        fontSize = 12.sp, fontWeight = FontWeight.Bold, color = PrimaryLight
                    )
                }

                // Conductor
                Surface(color = SurfaceSoft, shape = RoundedCornerShape(10.dp)) {
                    Row(Modifier.fillMaxWidth().padding(12.dp), verticalAlignment = Alignment.CenterVertically) {
                        Icon(Icons.Default.AccountCircle, null, Modifier.size(44.dp), tint = PrimaryLight)
                        Spacer(Modifier.width(10.dp))
                        Column(Modifier.weight(1f)) {
                            Text(t.driver, fontSize = 14.sp, fontWeight = FontWeight.Bold, color = PrimaryLight)
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Icon(Icons.Default.Star, null, Modifier.size(13.dp), tint = RatingStar)
                                Spacer(Modifier.width(2.dp))
                                Text("${t.rating} · ${t.verifiedTrips} viajes verificados",
                                    fontSize = 11.sp, color = OnSurfaceVariant)
                            }
                            StatusTag("Conductor Verificado", StatusAvailableBg, StatusAvailable, Icons.Default.Verified)
                        }
                        Column {
                            IconButton(onClick = {}, modifier = Modifier.size(36.dp)) {
                                Icon(Icons.Default.Call, "Llamar", tint = PrimaryLight)
                            }
                            IconButton(onClick = {}, modifier = Modifier.size(36.dp)) {
                                Icon(Icons.AutoMirrored.Filled.Chat, "Mensaje", tint = PrimaryLight)
                            }
                        }
                    }
                }

                InfoRow(Icons.Default.DirectionsCar, "VEHÍCULO DE CARGA Y PASAJE", t.vehicle, t.vehicleDetail)

                // Placa
                Surface(
                    shape = RoundedCornerShape(10.dp), color = CardBackground,
                    border = BorderStroke(1.dp, OutlineVariant)
                ) {
                    Row(Modifier.fillMaxWidth().padding(12.dp), verticalAlignment = Alignment.CenterVertically) {
                        Icon(Icons.Default.Badge, null, tint = PrimaryLight)
                        Spacer(Modifier.width(10.dp))
                        Column {
                            Text("PLACA Y REGISTRO", fontSize = 10.sp, color = OnSurfaceVariant)
                            StatusTag(t.plate, PlateYellow, OnPlateYellow)
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Icon(Icons.Default.VerifiedUser, null, Modifier.size(12.dp), tint = StatusAvailable)
                                Spacer(Modifier.width(4.dp))
                                Text("SOAT vigente comprobado", fontSize = 11.sp,
                                    fontWeight = FontWeight.Bold, color = StatusAvailable)
                            }
                        }
                    }
                }

                // Capacidad
                Surface(color = SurfaceSoft, shape = RoundedCornerShape(10.dp)) {
                    Column(Modifier.padding(12.dp), verticalArrangement = Arrangement.spacedBy(10.dp)) {
                        Row(Modifier.fillMaxWidth(), Arrangement.SpaceBetween, Alignment.CenterVertically) {
                            Text("CAPACIDAD DISPONIBLE EN RUTA", fontSize = 10.sp,
                                fontWeight = FontWeight.Bold, color = OnSurfaceVariant)
                            StatusTag("Espacio Confirmado", StatusAvailableBg, StatusAvailable)
                        }
                        if (t.hasCargo) {
                            CapacityRow(Icons.Default.Inventory2, "Carga Flete", "${t.cargoFreeKg} kg libres", t.cargoNote) {
                                LinearProgressIndicator(
                                    progress = { (t.cargoFreeKg.toFloat() / t.cargoTotalKg).coerceIn(0f, 1f) },
                                    modifier = Modifier.fillMaxWidth().height(5.dp),
                                    color = SecondaryContainer, trackColor = SurfaceVariant
                                )
                            }
                        }
                        if (t.hasSeats) {
                            CapacityRow(Icons.Default.EventSeat, "Pasajeros", "${t.seatsFree} cupos libres", t.seatsNote) {
                                Row(horizontalArrangement = Arrangement.spacedBy(4.dp)) {
                                    repeat(t.seatsTotal) { i ->
                                        Box(
                                            Modifier.size(10.dp).clip(CircleShape)
                                                .background(if (i < t.seatsFree) PrimaryLight else SurfaceVariant)
                                        )
                                    }
                                }
                            }
                        }
                    }
                }

                // Tarifas
                Row(Modifier.fillMaxWidth(), Arrangement.SpaceBetween) {
                    if (t.hasCargo) {
                        Column {
                            Text("CARGA", fontSize = 10.sp, color = OnSurfaceVariant)
                            Text("${t.cargoPrice} COP", fontSize = 16.sp, fontWeight = FontWeight.Bold, color = PrimaryLight)
                        }
                    } else Spacer(Modifier.width(1.dp))
                    if (t.hasSeats) {
                        Column(horizontalAlignment = Alignment.End) {
                            Text("CUPO A BORDO", fontSize = 10.sp, color = OnSurfaceVariant)
                            Text("${t.seatPrice} COP", fontSize = 16.sp, fontWeight = FontWeight.Bold, color = Secondary)
                        }
                    }
                }

                PrimaryButton(
                    text = "Reservar cupo o solicitar flete",
                    onClick = {},
                    leadingIcon = Icons.Default.LocalShipping,
                    trailingIcon = null,
                    containerColor = SecondaryContainer
                )
                Text(
                    "Transacción protegida por Garantía de Entrega Ruta Confiable",
                    fontSize = 10.sp, color = OnSurfaceVariant, textAlign = TextAlign.Center,
                    modifier = Modifier.fillMaxWidth()
                )
            }
        }
    }
}

@Composable
private fun RouteMapPreview(distance: String, duration: String) {
    Box(
        Modifier.fillMaxWidth().height(140.dp).clip(RoundedCornerShape(10.dp)).background(MapBackground)
    ) {
        Canvas(Modifier.fillMaxSize()) {
            val start = Offset(size.width * 0.10f, size.height * 0.78f)
            val end = Offset(size.width * 0.88f, size.height * 0.25f)
            val path = Path().apply {
                moveTo(start.x, start.y)
                cubicTo(size.width * 0.35f, size.height * 0.95f, size.width * 0.50f, size.height * 0.15f, end.x, end.y)
            }
            drawPath(path, MapRoadLine, style = Stroke(width = 14f, cap = StrokeCap.Round))
            drawPath(path, SecondaryContainer, style = Stroke(width = 7f, cap = StrokeCap.Round))
            drawCircle(StatusAvailable, 16f, start)
            drawCircle(StatusBooked, 16f, end)
        }
        Row(Modifier.align(Alignment.TopStart).padding(8.dp), horizontalArrangement = Arrangement.spacedBy(6.dp)) {
            StatusTag(distance, PrimaryLight, OnPrimary, Icons.Default.Route)
            StatusTag(duration, PrimaryLight, OnPrimary, Icons.Default.Schedule)
        }
    }
}

@Composable
private fun CapacityRow(
    icon: ImageVector, title: String, free: String, note: String,
    indicator: @Composable () -> Unit
) {
    Surface(color = CardBackground, shape = RoundedCornerShape(8.dp)) {
        Row(Modifier.fillMaxWidth().padding(10.dp), verticalAlignment = Alignment.CenterVertically) {
            Surface(color = SecondarySoft, shape = RoundedCornerShape(8.dp)) {
                Icon(icon, null, Modifier.padding(8.dp).size(20.dp), tint = Secondary)
            }
            Spacer(Modifier.width(10.dp))
            Column(Modifier.weight(1f), verticalArrangement = Arrangement.spacedBy(4.dp)) {
                Row(Modifier.fillMaxWidth(), Arrangement.SpaceBetween) {
                    Text(title, fontSize = 12.sp, fontWeight = FontWeight.Bold, color = PrimaryLight)
                    Text(free, fontSize = 11.sp, fontWeight = FontWeight.Bold, color = Secondary)
                }
                indicator()
                Text(note, fontSize = 10.sp, color = OnSurfaceVariant)
            }
        }
    }
}

@Composable
private fun InfoRow(icon: ImageVector, label: String, title: String, subtitle: String) {
    Surface(shape = RoundedCornerShape(10.dp), color = CardBackground, border = BorderStroke(1.dp, OutlineVariant)) {
        Row(Modifier.fillMaxWidth().padding(12.dp), verticalAlignment = Alignment.CenterVertically) {
            Icon(icon, null, tint = PrimaryLight)
            Spacer(Modifier.width(10.dp))
            Column {
                Text(label, fontSize = 10.sp, color = OnSurfaceVariant)
                Text(title, fontSize = 13.sp, fontWeight = FontWeight.Bold, color = PrimaryLight)
                Text(subtitle, fontSize = 11.sp, color = OnSurfaceVariant)
            }
        }
    }
}

@Composable
private fun OtherDepartureRow(t: AvailableTrip, onClick: () -> Unit) {
    Surface(
        modifier = Modifier.fillMaxWidth().clickable { onClick() },
        shape = RoundedCornerShape(10.dp), color = CardBackground,
        border = BorderStroke(1.dp, OutlineVariant)
    ) {
        Row(Modifier.padding(12.dp), verticalAlignment = Alignment.CenterVertically) {
            Icon(Icons.Default.LocalShipping, null, tint = PrimaryLight)
            Spacer(Modifier.width(10.dp))
            Column(Modifier.weight(1f)) {
                Text("${t.departure} · ${t.vehicle}", fontSize = 12.sp, fontWeight = FontWeight.Bold, color = PrimaryLight)
                Text("${t.origin} → ${t.destination}", fontSize = 10.sp, color = OnSurfaceVariant)
            }
            Column(horizontalAlignment = Alignment.End) {
                if (t.hasSeats) StatusTag("${t.seatsFree} cupo", StatusAvailableBg, StatusAvailable)
                Text(if (t.hasCargo) t.cargoPrice else t.seatPrice,
                    fontSize = 14.sp, fontWeight = FontWeight.Bold, color = Secondary)
            }
        }
    }
}

@Composable
private fun ToggleBox(
    icon: ImageVector, label: String, checked: Boolean,
    onChange: (Boolean) -> Unit, modifier: Modifier = Modifier
) {
    Surface(modifier, color = SurfaceSoft, shape = RoundedCornerShape(8.dp), border = BorderStroke(1.dp, OutlineVariant)) {
        Row(Modifier.padding(horizontal = 10.dp, vertical = 6.dp), Arrangement.SpaceBetween, Alignment.CenterVertically) {
            Icon(icon, null, Modifier.size(18.dp), tint = Secondary)
            Text(label, fontSize = 12.sp, fontWeight = FontWeight.Bold, color = PrimaryLight)
            AppSwitch(checked, onChange)
        }
    }
}

@Composable
private fun TripBox(content: @Composable ColumnScope.() -> Unit) {
    Surface(
        modifier = Modifier.fillMaxWidth(),
        color = CardBackground, shape = RoundedCornerShape(12.dp),
        border = BorderStroke(1.dp, OutlineVariant), shadowElevation = 1.dp
    ) { Column(Modifier.padding(14.dp), verticalArrangement = Arrangement.spacedBy(10.dp), content = content) }
}

private fun fetchCurrentLocation(context: Context, onLocationResult: (String) -> Unit) {
    val hasFine = ContextCompat.checkSelfPermission(
        context, Manifest.permission.ACCESS_FINE_LOCATION
    ) == PackageManager.PERMISSION_GRANTED
    val hasCoarse = ContextCompat.checkSelfPermission(
        context, Manifest.permission.ACCESS_COARSE_LOCATION
    ) == PackageManager.PERMISSION_GRANTED

    if (!hasFine && !hasCoarse) {
        onLocationResult("Permiso de ubicación no concedido")
        return
    }

    try {
        val fusedLocationClient = LocationServices.getFusedLocationProviderClient(context)
        fusedLocationClient.lastLocation.addOnSuccessListener { location: Location? ->
            if (location != null) {
                val latLngStr = "Lat: %.4f, Lng: %.4f".format(location.latitude, location.longitude)
                try {
                    val geocoder = android.location.Geocoder(context, java.util.Locale.getDefault())
                    val addresses = geocoder.getFromLocation(location.latitude, location.longitude, 1)
                    if (!addresses.isNullOrEmpty()) {
                        val address = addresses[0]
                        val cityName = address.locality ?: address.subAdminArea ?: address.adminArea ?: latLngStr
                        onLocationResult(cityName)
                    } else {
                        onLocationResult(latLngStr)
                    }
                } catch (e: Exception) {
                    onLocationResult(latLngStr)
                }
            } else {
                onLocationResult("Ubicación actual no disponible")
            }
        }.addOnFailureListener {
            onLocationResult("Error al obtener GPS")
        }
    } catch (e: Exception) {
        onLocationResult("GPS no disponible")
    }
}

@Preview(showBackground = true)
@Composable
fun TripScreenPreview() {
    RutaConfiableTheme {
        TripScreen(
            trips = listOf(
                AvailableTrip(
                    "VJ-0001", "08:00 AM", "Carlos Mario Restrepo", 4.8, 142,
                    "Toyota Hilux 4x4", "Carga y flete", "CAL482",
                    450, 800, "Espacio de carga", 2, 4, "Cupo a bordo",
                    "$ 55.000", "$ 40.000", "405 km", "8h 30m", "Bogotá → Medellín",
                    origin = "Bogotá", destination = "Medellín", date = "24/10/2026"
                )
            )
        )
    }
}
