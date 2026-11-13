package me.mauromax.rutaconfiable.ui.screens

import android.net.Uri
import android.widget.Toast
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import kotlinx.coroutines.launch
import me.mauromax.rutaconfiable.ui.components.*
import me.mauromax.rutaconfiable.ui.data.AuthRepository
import me.mauromax.rutaconfiable.ui.theme.*
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale
import java.util.TimeZone

enum class RegistrationRole {
    DRIVER, CLIENT
}

private val DocumentTypes = listOf("C.C.", "C.E.", "Pasaporte")

private val PassengerVehicles = listOf(
    "Bus" to Icons.Default.DirectionsBus,
    "Microbús" to Icons.Default.AirportShuttle,
    "Van" to Icons.Default.Commute
)
private val CargoVehicles = listOf(
    "Camión Ligero" to Icons.Default.LocalShipping,
    "Camioneta" to Icons.Default.DirectionsCar,
    "Baúl Abierto" to Icons.Default.Inventory2
)

@Composable
fun RegistrationScreen(
    initialRole: RegistrationRole = RegistrationRole.DRIVER,
    onRegistrationSubmitted: (role: RegistrationRole) -> Unit = {},
    onBackClick: () -> Unit = {},
    onLoginClick: () -> Unit = {},
    onAddPaymentClick: () -> Unit = {}
) {
    var currentRole by rememberSaveable { mutableStateOf(initialRole) }

    Scaffold(
        topBar = { StandardAppTopBar(onBackClick = onBackClick, showSecurityBadge = true) },
        containerColor = Background
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .imePadding()
                .verticalScroll(rememberScrollState())
                .padding(horizontal = 16.dp, vertical = 12.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            SegmentedSelector(
                listOf("Conductor" to Icons.Default.LocalShipping, "Cliente" to Icons.Default.Person),
                currentRole.ordinal
            ) { currentRole = RegistrationRole.entries[it] }

            if (currentRole == RegistrationRole.DRIVER) {
                DriverForm(onRegistered = { onRegistrationSubmitted(RegistrationRole.DRIVER) })
            } else {
                ClientForm(
                    onAddPaymentClick = onAddPaymentClick,
                    onRegistered = { onRegistrationSubmitted(RegistrationRole.CLIENT) }
                )
            }

            Row(
                Modifier.fillMaxWidth().padding(vertical = 4.dp),
                Arrangement.Center,
                Alignment.CenterVertically
            ) {
                Text("¿Ya tienes una cuenta registrada? ", fontSize = 13.sp, color = OnSurfaceVariant)
                Text(
                    "Iniciar sesión", fontSize = 13.sp, fontWeight = FontWeight.Bold,
                    color = Secondary, modifier = Modifier.clickable { onLoginClick() }
                )
            }
            Spacer(Modifier.height(16.dp))
        }
    }
}

// ---------------------------------------------------------------- VALIDACIONES

private fun isValidName(v: String) =
    v.trim().split(Regex("\\s+")).count { it.length >= 2 } >= 2

private fun isValidEmail(v: String): Boolean {
    val emailRegex = "^[A-Za-z0-9._%+\\-]+@[A-Za-z0-9.\\-]+\\.[A-Za-z]{2,}$".toRegex()
    return v.trim().matches(emailRegex)
}

private fun isValidPhone(v: String) = v.count { it.isDigit() } in 7..15

private fun isValidPassword(v: String) =
    v.length >= 8 && v.any { it.isUpperCase() || !it.isLetter() }

private fun isValidDocument(type: String, number: String): Boolean {
    val clean = number.filter { it.isLetterOrDigit() }
    return if (type == "Pasaporte") clean.length in 6..12
    else clean.length in 5..12 && clean.all { it.isDigit() }
}

private fun isValidLicense(v: String) = v.filter { it.isLetterOrDigit() }.length >= 6

private fun isValidPlate(v: String) = Regex("^[A-Z]{3}-?\\d{3}$").matches(v.trim().uppercase())

private fun isValidSoatNumber(v: String) = v.trim().length >= 6 && v.trim().all { it.isDigit() }

/** Formato dd/mm/aaaa y al menos 30 días de vigencia desde hoy. */
private fun isValidSoatDate(v: String): Boolean {
    val text = v.trim()
    if (text.length != 10) return false
    val format = SimpleDateFormat("dd/MM/yyyy", Locale.US).apply { isLenient = false }
    val date = runCatching { format.parse(text) }.getOrNull() ?: return false
    return date.time - System.currentTimeMillis() >= 30L * 24 * 60 * 60 * 1000
}

// ---------------------------------------------------------------- CLIENTE

@Composable
private fun ClientForm(onAddPaymentClick: () -> Unit, onRegistered: () -> Unit) {
    var fullName by rememberSaveable { mutableStateOf("") }
    var email by rememberSaveable { mutableStateOf("") }
    var phone by rememberSaveable { mutableStateOf("") }
    var password by remember { mutableStateOf("") }
    var documentType by rememberSaveable { mutableStateOf(DocumentTypes.first()) }
    var documentNumber by rememberSaveable { mutableStateOf("") }
    var selfieOk by rememberSaveable { mutableStateOf(false) }
    var terms by rememberSaveable { mutableStateOf(false) }
    var whatsapp by rememberSaveable { mutableStateOf(false) }
    var isLoading by remember { mutableStateOf(false) }
    var errorMessage by remember { mutableStateOf<String?>(null) }
    val scope = rememberCoroutineScope()

    fun submit() {
        errorMessage = null
        isLoading = true
        scope.launch {
            AuthRepository.register(
                email = email.trim().lowercase(),
                password = password,
                name = fullName.trim(),
                role = "client",
                profile = mapOf(
                    "phone" to phone.filter { it.isDigit() },
                    "phoneCountryCode" to "+57",
                    "documentType" to documentType,
                    "documentNumber" to documentNumber.filter { it.isLetterOrDigit() },
                    "termsAccepted" to terms,
                    "whatsappAlertsAccepted" to whatsapp
                )
            ).onSuccess {
                isLoading = false
                onRegistered()
            }.onFailure { e ->
                isLoading = false
                errorMessage = AuthRepository.friendlyError(e)
            }
        }
    }

    val context = LocalContext.current
    val validateSelfie = {
        Toast.makeText(context, "Ya fue validado", Toast.LENGTH_SHORT).show()
        selfieOk = true
    }
    val pickSelfie = rememberDocumentPicker(arrayOf("image/*")) { selfieOk = true }

    val missing = buildList {
        if (!isValidName(fullName)) add("nombre y apellido")
        if (!isValidEmail(email)) add("correo válido")
        if (!isValidPhone(phone)) add("celular")
        if (!isValidPassword(password)) add("contraseña (8+ caracteres con mayúscula, número o símbolo)")
        if (!isValidDocument(documentType, documentNumber)) add("documento de identidad")
        if (!selfieOk) add("selfie")
        if (!terms) add("aceptar términos")
        if (!whatsapp) add("aceptar alertas de WhatsApp")
    }

    Column(verticalArrangement = Arrangement.spacedBy(16.dp)) {
        FormHeader(
            Icons.Default.Person, "REGISTRO DE CLIENTE", "Crea tu cuenta de Cliente",
            "Reserva cupos cómodos en viajes intermunicipales o despacha encomiendas con monitoreo satelital en tiempo real."
        )

        CardSectionContainer(
            title = "1. Datos Personales y Acceso",
            icon = Icons.Default.Badge,
            subtitle = "Tus credenciales para gestionar reservas y despachos"
        ) {
            AppOutlinedTextField(fullName, { fullName = it }, "Nombre completo", "Ej: Valentina Gómez Salazar", Icons.Default.Person)
            AppOutlinedTextField(email, { email = it }, "Correo electrónico", "ejemplo@correo.com", Icons.Default.Email)
            PhoneField(phone) { phone = it }
            Column {
                PasswordTextField(password, { password = it }, "Contraseña segura", "Mínimo 8 caracteres")
                Hint("Incluye mayúsculas, números o caracteres especiales.", Modifier.padding(top = 4.dp, start = 4.dp))
            }
        }

        CardSectionContainer(
            title = "2. Validación de Seguridad",
            icon = Icons.Default.VerifiedUser,
            subtitle = "Requerido para control de abordaje en terminal"
        ) {
            DocumentField(documentType, { documentType = it }, documentNumber, { documentNumber = it })

            Row(Modifier.fillMaxWidth(), Arrangement.SpaceBetween, Alignment.CenterVertically) {
                FieldLabel("Selfie de verificación biométrica")
                Text("Obligatorio", fontSize = 11.sp, fontWeight = FontWeight.Bold, color = Secondary)
            }
            Hint("Rostro descubierto y nítido para entrega segura de encomiendas y abordaje en ruta.")
            UploadRow(
                icon = Icons.Default.Face,
                title = if (selfieOk) "Selfie validada" else "Sin captura adjunta",
                attached = selfieOk
            ) {
                SmallButton("Tomar foto", Icons.Default.PhotoCamera, onClick = validateSelfie)
                SmallButton("Subir", Icons.Default.FileUpload, dark = false, onClick = pickSelfie)
            }
        }

        SecondaryOutlinedButton(
            text = "Agregar método de pago",
            onClick = onAddPaymentClick,
            leadingIcon = Icons.Default.Add
        )

        Surface(Modifier.fillMaxWidth(), color = SurfaceSoft, shape = RoundedCornerShape(12.dp)) {
            Row(Modifier.padding(12.dp), verticalAlignment = Alignment.CenterVertically) {
                Surface(Modifier.size(40.dp), shape = RoundedCornerShape(8.dp), color = SecondaryContainer) {
                    Icon(Icons.Default.DirectionsBus, null, Modifier.padding(10.dp), tint = Color.White)
                }
                Spacer(Modifier.width(12.dp))
                Column {
                    Text("Póliza de Pasajero y Mercancía", fontSize = 13.sp, fontWeight = FontWeight.Bold, color = PrimaryLight)
                    Text(
                        "Cobertura activa desde el abordaje hasta la entrega final en destino.",
                        fontSize = 11.sp, color = OnSurfaceVariant, lineHeight = 15.sp
                    )
                }
            }
        }

        Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
            CheckRow(terms, { terms = it },
                "Acepto los Términos y Condiciones del servicio de transporte y la Política de Privacidad de datos de Ruta Confiable.")
            CheckRow(whatsapp, { whatsapp = it },
                "Deseo recibir alertas de WhatsApp con el estado del bus y ubicación en tiempo real de mis encomiendas.")
        }

        if (missing.isNotEmpty()) Hint("Falta: " + missing.joinToString(", ") + ".")
        errorMessage?.let { ErrorBanner(it) }
        PrimaryButton(
            text = if (isLoading) "Creando cuenta..." else "Crear cuenta de cliente",
            onClick = { submit() },
            enabled = missing.isEmpty() && !isLoading
        )
        TlsEncryptionBadge(Modifier.align(Alignment.CenterHorizontally))
    }
}

// -------------------------------------------------------------- CONDUCTOR

@Composable
private fun DriverForm(onRegistered: () -> Unit) {
    var fullName by rememberSaveable { mutableStateOf("") }
    var email by rememberSaveable { mutableStateOf("") }
    var phone by rememberSaveable { mutableStateOf("") }
    var password by remember { mutableStateOf("") }
    var documentType by rememberSaveable { mutableStateOf(DocumentTypes.first()) }
    var documentNumber by rememberSaveable { mutableStateOf("") }
    var selfieOk by rememberSaveable { mutableStateOf(false) }
    var license by rememberSaveable { mutableStateOf("") }
    var licenseFile by rememberSaveable { mutableStateOf<Uri?>(null) }
    var vehicleType by rememberSaveable { mutableStateOf("") }
    var plate by rememberSaveable { mutableStateOf("") }
    var soat by rememberSaveable { mutableStateOf("") }
    var soatDate by rememberSaveable { mutableStateOf("") }
    var vehicleDoc by rememberSaveable { mutableStateOf<Uri?>(null) }
    var showDatePicker by remember { mutableStateOf(false) }
    var terms by rememberSaveable { mutableStateOf(false) }
    var policies by rememberSaveable { mutableStateOf(false) }
    var isLoading by remember { mutableStateOf(false) }
    var errorMessage by remember { mutableStateOf<String?>(null) }
    val scope = rememberCoroutineScope()

    fun submit() {
        errorMessage = null
        isLoading = true
        scope.launch {
            AuthRepository.register(
                email = email.trim().lowercase(),
                password = password,
                name = fullName.trim(),
                role = "driver",
                profile = mapOf(
                    "phone" to phone.filter { it.isDigit() },
                    "phoneCountryCode" to "+57",
                    "documentType" to documentType,
                    "documentNumber" to documentNumber.filter { it.isLetterOrDigit() },
                    "licenseNumber" to license.trim(),
                    "vehicleType" to vehicleType,
                    "vehiclePlate" to plate.trim().uppercase().replace("-", ""),
                    "soatNumber" to soat.trim(),
                    "soatExpiration" to soatDate.trim(),
                    "termsAccepted" to terms,
                    "policiesAccepted" to policies
                )
            ).onSuccess {
                isLoading = false
                onRegistered()
            }.onFailure { e ->
                isLoading = false
                errorMessage = AuthRepository.friendlyError(e)
            }
        }
    }

    val docMimeTypes = arrayOf("image/*", "application/pdf")
    val context = LocalContext.current
    val validateSelfie = {
        Toast.makeText(context, "Ya fue validado", Toast.LENGTH_SHORT).show()
        selfieOk = true
    }
    val pickLicense = rememberDocumentPicker(docMimeTypes) { licenseFile = it }
    val pickVehicleDoc = rememberDocumentPicker(docMimeTypes) { vehicleDoc = it }

    val missing = buildList {
        if (!isValidName(fullName)) add("nombre y apellido")
        if (!isValidEmail(email)) add("correo válido")
        if (!isValidPhone(phone)) add("celular")
        if (!isValidPassword(password)) add("contraseña (8+ caracteres con mayúscula, número o símbolo)")
        if (!isValidDocument(documentType, documentNumber)) add("documento de identidad")
        if (!selfieOk) add("selfie")
        if (!isValidLicense(license)) add("número de licencia")
        if (licenseFile == null) add("documento de licencia")
        if (vehicleType.isEmpty()) add("tipo de vehículo")
        if (!isValidPlate(plate)) add("placa (formato AAA-123)")
        if (!isValidSoatNumber(soat)) add("número de SOAT")
        if (!isValidSoatDate(soatDate)) add("vencimiento del SOAT (dd/mm/aaaa, 30+ días de vigencia)")
        if (vehicleDoc == null) add("documento del vehículo")
        if (!terms) add("aceptar términos")
        if (!policies) add("aceptar políticas de inspección")
    }

    Column(verticalArrangement = Arrangement.spacedBy(16.dp)) {
        if (showDatePicker) {
            SoatDatePickerDialog(
                onDismiss = { showDatePicker = false },
                onDateSelected = { soatDate = it }
            )
        }

        FormHeader(
            Icons.Default.LocalShipping, "REGISTRO DE CONDUCTOR", "Crea tu cuenta de Conductor",
            "Coordinación logística regional y flete seguro. Selecciona tu perfil operativo para comenzar."
        )

        CardSectionContainer(
            title = "1. Información Personal",
            icon = Icons.Default.Badge,
            tagText = "Obligatorio",
            tagColor = Outline
        ) {
            AppOutlinedTextField(fullName, { fullName = it }, "Nombre completo", "Ej. Carlos Mendoza Rivas", Icons.Default.Person)
            AppOutlinedTextField(email, { email = it }, "Correo electrónico", "operaciones@ejemplo.com", Icons.Default.Email)
            PhoneField(phone) { phone = it }
            PasswordTextField(password, { password = it }, "Contraseña", "Mínimo 8 caracteres")
            DocumentField(documentType, { documentType = it }, documentNumber, { documentNumber = it })
            FieldLabel("Fotografía facial (Selfie biométrica)")
            UploadRow(
                icon = Icons.Default.Person,
                title = if (selfieOk) "Selfie validada" else "Rostro descubierto y nítido",
                subtitle = if (selfieOk) null else "Sin gorras ni gafas oscuras",
                dashed = true,
                attached = selfieOk
            ) {
                SmallButton("Tomar foto", dark = false, onClick = validateSelfie)
            }
            if (selfieOk) {
                StatusTag("Selfie validada", StatusAvailableBg, StatusAvailable, Icons.Default.CheckCircle)
            } else {
                StatusTag("Validación facial obligatoria", StatusPendingBg, StatusPending)
            }
        }

        CardSectionContainer(
            title = "2. Licencia de Conducción",
            icon = Icons.Default.AssignmentInd,
            tagText = "RUNT Vigente",
            tagColor = Secondary
        ) {
            AppOutlinedTextField(license, { license = it }, "Número de licencia (Pase)", "Ej. 1020485934", Icons.Default.Pin)
            FieldLabel("Cargar documento de licencia")
            UploadRow(
                icon = Icons.Default.FilePresent,
                title = if (licenseFile != null) "Licencia adjunta" else "Toca para cargar foto o PDF legible",
                subtitle = "Frente y reverso en un solo archivo (Máx 5MB)",
                dashed = true,
                attached = licenseFile != null
            ) { SmallButton("Subir", Icons.Default.FileUpload, dark = false, onClick = pickLicense) }
        }

        CardSectionContainer(
            title = "3. Requisitos del Vehículo",
            icon = Icons.Default.DirectionsCar,
            tagText = "Inspección Vial",
            tagColor = Outline
        ) {
            FieldLabel("Tipo de vehículo habilitado")
            VehicleGroup("Transporte de pasajeros", PassengerVehicles, vehicleType) { vehicleType = it }
            VehicleGroup("Carga y encomiendas", CargoVehicles, vehicleType) { vehicleType = it }

            Row(Modifier.fillMaxWidth(), Arrangement.spacedBy(8.dp)) {
                AppOutlinedTextField(plate, { plate = it.uppercase() }, "Placa del vehículo", "AAA-123", Icons.Default.Subtitles, modifier = Modifier.weight(1f))
                AppOutlinedTextField(soat, { soat = it }, "Número de SOAT", "10094827", Icons.Default.Verified, modifier = Modifier.weight(1f))
            }

            Column {
                AppOutlinedTextField(
                    soatDate, { soatDate = it }, "Fecha de vencimiento del SOAT", "dd/mm/aaaa",
                    Icons.Default.CalendarToday,
                    trailingIcon = {
                        IconButton(onClick = { showDatePicker = true }) {
                            Icon(Icons.Default.DateRange, contentDescription = "Abrir calendario")
                        }
                    }
                )
                Hint("El seguro obligatorio debe tener al menos 30 días de vigencia continua.", Modifier.padding(top = 4.dp, start = 4.dp))
            }

            FieldLabel("Foto de Tarjeta de Propiedad o SOAT físico")
            UploadRow(
                icon = Icons.Default.Description,
                title = if (vehicleDoc != null) "Documento adjunto" else "Documento de control vehicular",
                subtitle = "Formato JPG, PNG o PDF",
                attached = vehicleDoc != null
            ) {
                SmallButton("Subir", onClick = pickVehicleDoc)
            }

            Hint("Estado de Requerimientos Legales:")
            Row(Modifier.fillMaxWidth(), Arrangement.spacedBy(6.dp)) {
                StatusTag("✓ RUNT Conductor", StatusAvailableBg, StatusAvailable)
                StatusTag("⏱ Revisión SOAT", StatusPendingBg, StatusPending)
                StatusTag("✓ Carga Asegurada", SurfaceMuted, PrimaryLight)
            }
        }

        Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
            CheckRow(terms, { terms = it },
                "Acepto los Términos y Condiciones de Ruta Confiable y autorizo el tratamiento de mis datos personales para verificación y seguridad logística.")
            CheckRow(policies, { policies = it },
                "Acepto las políticas de validación física e inspección de carga en puestos de control de ruta.")
        }

        if (missing.isNotEmpty()) Hint("Falta: " + missing.joinToString(", ") + ".")
        errorMessage?.let { ErrorBanner(it) }
        PrimaryButton(
            text = if (isLoading) "Creando cuenta..." else "Crear cuenta de conductor",
            onClick = { submit() },
            enabled = missing.isEmpty() && !isLoading
        )
    }
}

// ------------------------------------------------------ PIEZAS REUTILIZABLES

@Composable
private fun ErrorBanner(message: String) {
    Surface(Modifier.fillMaxWidth(), color = ErrorContainer, shape = RoundedCornerShape(8.dp)) {
        Text(
            message, Modifier.padding(12.dp),
            fontSize = 13.sp, fontWeight = FontWeight.SemiBold, color = OnErrorContainer
        )
    }
}

@Composable
private fun PhoneField(phone: String, onChange: (String) -> Unit) {
    Column {
        FieldLabel("Número de WhatsApp / Celular")
        Spacer(Modifier.height(4.dp))
        PhoneWithCountrySelectorField(phone, onChange, placeholderText = "310 456 7890")
    }
}

@Composable
private fun DocumentField(
    type: String,
    onTypeChange: (String) -> Unit,
    number: String,
    onNumberChange: (String) -> Unit
) {
    var menu by remember { mutableStateOf(false) }

    FieldLabel("Documento de Identidad")
    Row(Modifier.fillMaxWidth(), Arrangement.spacedBy(8.dp), Alignment.CenterVertically) {
        Box {
            Surface(
                modifier = Modifier.height(56.dp).clickable { menu = true },
                shape = RoundedCornerShape(8.dp),
                color = SurfaceSoft,
                border = BorderStroke(1.dp, OutlineVariant)
            ) {
                Row(Modifier.padding(horizontal = 12.dp), verticalAlignment = Alignment.CenterVertically) {
                    Text(type, fontSize = 13.sp, fontWeight = FontWeight.Bold, color = PrimaryLight)
                    Icon(Icons.Default.ArrowDropDown, null, tint = PrimaryLight)
                }
            }
            DropdownMenu(expanded = menu, onDismissRequest = { menu = false }) {
                DocumentTypes.forEach { option ->
                    DropdownMenuItem(
                        text = { Text(option) },
                        onClick = { onTypeChange(option); menu = false }
                    )
                }
            }
        }
        AppOutlinedTextField(
            number, onNumberChange, null, "1.020.485.934",
            Icons.Default.Pin, modifier = Modifier.weight(1f)
        )
    }
}

@Composable
private fun VehicleGroup(
    title: String,
    options: List<Pair<String, ImageVector>>,
    selected: String,
    onSelect: (String) -> Unit
) {
    Column(verticalArrangement = Arrangement.spacedBy(6.dp)) {
        Hint(title)
        Row(Modifier.fillMaxWidth(), Arrangement.spacedBy(8.dp)) {
            options.forEach { (name, icon) ->
                VehicleTypeChip(name, icon, selected == name, Modifier.weight(1f)) { onSelect(name) }
            }
        }
    }
}

@Composable
private fun FormHeader(icon: ImageVector, tag: String, title: String, description: String) {
    Column(verticalArrangement = Arrangement.spacedBy(16.dp)) {
        Row(Modifier.fillMaxWidth(), Arrangement.SpaceBetween, Alignment.CenterVertically) {
            PillTag {
                Icon(icon, null, Modifier.size(14.dp), tint = Secondary)
                Spacer(Modifier.width(4.dp))
                Text(tag, fontSize = 11.sp, fontWeight = FontWeight.Bold, color = PrimaryLight)
            }
            PillTag { Text("Paso 1 de 2", fontSize = 11.sp, fontWeight = FontWeight.Bold, color = PrimaryLight) }
        }
        ScreenTitle(title)
        BodyText(description)
    }
}

@Composable
private fun CheckRow(checked: Boolean, onChange: (Boolean) -> Unit, text: String) {
    Row(Modifier.fillMaxWidth(), verticalAlignment = Alignment.Top) {
        Checkbox(checked, onChange, colors = CheckboxDefaults.colors(checkedColor = Secondary))
        Text(text, fontSize = 12.sp, color = OnSurfaceVariant, lineHeight = 16.sp, modifier = Modifier.padding(top = 10.dp))
    }
}

@Composable
private fun SmallButton(
    text: String,
    icon: ImageVector? = null,
    dark: Boolean = true,
    onClick: () -> Unit
) {
    Button(
        onClick = onClick,
        shape = RoundedCornerShape(8.dp),
        colors = ButtonDefaults.buttonColors(
            containerColor = if (dark) PrimaryLight else SurfaceVariant,
            contentColor = if (dark) Color.White else PrimaryLight
        ),
        contentPadding = PaddingValues(horizontal = 10.dp, vertical = 4.dp)
    ) {
        icon?.let {
            Icon(it, null, Modifier.size(14.dp))
            Spacer(Modifier.width(4.dp))
        }
        Text(text, fontSize = 11.sp, fontWeight = FontWeight.Bold)
    }
}

@Composable
private fun UploadRow(
    icon: ImageVector,
    title: String,
    subtitle: String? = null,
    dashed: Boolean = false,
    attached: Boolean = false,
    actions: @Composable RowScope.() -> Unit
) {
    val shape = RoundedCornerShape(8.dp)
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .then(
                if (dashed && !attached) Modifier.dashedBorder(1.dp, OutlineVariant, 8.dp)
                else Modifier.border(1.dp, if (attached) StatusAvailable else OutlineVariant, shape)
            )
            .background(SurfaceSoft, shape)
            .padding(12.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Row(Modifier.weight(1f), verticalAlignment = Alignment.CenterVertically) {
            Surface(Modifier.size(40.dp), shape = CircleShape, color = if (attached) StatusAvailableBg else SurfaceVariant) {
                Icon(
                    if (attached) Icons.Default.CheckCircle else icon,
                    null,
                    Modifier.padding(9.dp),
                    tint = if (attached) StatusAvailable else PrimaryLight
                )
            }
            Spacer(Modifier.width(10.dp))
            Column {
                Text(title, fontSize = 12.sp, fontWeight = FontWeight.Bold, color = PrimaryLight)
                subtitle?.let { Hint(it) }
            }
        }
        Spacer(Modifier.width(8.dp))
        Row(horizontalArrangement = Arrangement.spacedBy(6.dp), content = actions)
    }
}

// ------------------------------------------------------ SELECCIÓN DE ARCHIVOS

/** Abre el selector de documentos del sistema y devuelve el Uri elegido. */
@Composable
private fun rememberDocumentPicker(mimeTypes: Array<String>, onPicked: (Uri) -> Unit): () -> Unit {
    val launcher = rememberLauncherForActivityResult(ActivityResultContracts.OpenDocument()) { uri ->
        uri?.let(onPicked)
    }
    return { launcher.launch(mimeTypes) }
}

/** Calendario para elegir el vencimiento del SOAT (mínimo 30 días desde hoy). */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun SoatDatePickerDialog(onDismiss: () -> Unit, onDateSelected: (String) -> Unit) {
    val minDate = remember { System.currentTimeMillis() + 30L * 24 * 60 * 60 * 1000 }
    val state = rememberDatePickerState(
        initialDisplayedMonthMillis = minDate,
        selectableDates = object : SelectableDates {
            override fun isSelectableDate(utcTimeMillis: Long) = utcTimeMillis >= minDate
        }
    )
    DatePickerDialog(
        onDismissRequest = onDismiss,
        confirmButton = {
            TextButton(
                enabled = state.selectedDateMillis != null,
                onClick = {
                    state.selectedDateMillis?.let { onDateSelected(formatPickedDate(it)) }
                    onDismiss()
                }
            ) { Text("Aceptar") }
        },
        dismissButton = { TextButton(onClick = onDismiss) { Text("Cancelar") } }
    ) {
        DatePicker(state = state)
    }
}

private fun formatPickedDate(utcMillis: Long): String =
    SimpleDateFormat("dd/MM/yyyy", Locale.US)
        .apply { timeZone = TimeZone.getTimeZone("UTC") }
        .format(Date(utcMillis))

@Preview(showBackground = true, heightDp = 1600)
@Composable
fun ClientRegistrationScreenPreview() {
    RutaConfiableTheme { RegistrationScreen(initialRole = RegistrationRole.CLIENT) }
}

@Preview(showBackground = true, heightDp = 2200)
@Composable
fun DriverRegistrationScreenPreview() {
    RutaConfiableTheme { RegistrationScreen(initialRole = RegistrationRole.DRIVER) }
}