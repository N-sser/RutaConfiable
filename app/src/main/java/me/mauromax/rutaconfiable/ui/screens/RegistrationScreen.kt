package me.mauromax.rutaconfiable.ui.screens

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
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import me.mauromax.rutaconfiable.ui.components.*
import me.mauromax.rutaconfiable.ui.theme.*

enum class RegistrationRole {
    DRIVER, CLIENT
}

private val Body = Color(0xFF44474D)

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
                DriverForm { onRegistrationSubmitted(RegistrationRole.DRIVER) }
            } else {
                ClientForm(
                    onAddPaymentClick = onAddPaymentClick,
                    onSubmit = { onRegistrationSubmitted(RegistrationRole.CLIENT) }
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

// ---------------------------------------------------------------- CLIENTE

@Composable
private fun ClientForm(onAddPaymentClick: () -> Unit, onSubmit: () -> Unit) {
    var fullName by rememberSaveable { mutableStateOf("") }
    var email by rememberSaveable { mutableStateOf("") }
    var phone by rememberSaveable { mutableStateOf("") }
    var password by remember { mutableStateOf("") }
    var documentType by rememberSaveable { mutableStateOf("C.C.") }
    var documentNumber by rememberSaveable { mutableStateOf("") }
    var docMenu by remember { mutableStateOf(false) }
    var terms by rememberSaveable { mutableStateOf(false) }
    var whatsapp by rememberSaveable { mutableStateOf(false) }

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
            Column {
                FieldLabel("Número de WhatsApp / Celular")
                Spacer(Modifier.height(4.dp))
                PhoneWithCountrySelectorField(phone, { phone = it }, placeholderText = "310 456 7890")
            }
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
            FieldLabel("Documento de Identidad")
            Row(Modifier.fillMaxWidth(), Arrangement.spacedBy(8.dp), Alignment.CenterVertically) {
                Box {
                    Surface(
                        modifier = Modifier.height(56.dp).clickable { docMenu = true },
                        shape = RoundedCornerShape(8.dp),
                        color = SurfaceSoft,
                        border = BorderStroke(1.dp, OutlineVariant)
                    ) {
                        Row(Modifier.padding(horizontal = 12.dp), verticalAlignment = Alignment.CenterVertically) {
                            Text(documentType, fontSize = 13.sp, fontWeight = FontWeight.Bold, color = PrimaryLight)
                            Icon(Icons.Default.ArrowDropDown, null, tint = PrimaryLight)
                        }
                    }
                    DropdownMenu(expanded = docMenu, onDismissRequest = { docMenu = false }) {
                        listOf("C.C.", "C.E.", "Pasaporte").forEach { type ->
                            DropdownMenuItem(
                                text = { Text(type) },
                                onClick = { documentType = type; docMenu = false }
                            )
                        }
                    }
                }
                AppOutlinedTextField(
                    documentNumber, { documentNumber = it }, null, "1.020.485.934",
                    Icons.Default.Pin, modifier = Modifier.weight(1f)
                )
            }

            Row(Modifier.fillMaxWidth(), Arrangement.SpaceBetween, Alignment.CenterVertically) {
                FieldLabel("Selfie de verificación biométrica")
                Text("Obligatorio", fontSize = 11.sp, fontWeight = FontWeight.Bold, color = Secondary)
            }
            Hint("Rostro descubierto y nítido para entrega segura de encomiendas y abordaje en ruta.")
            UploadRow(Icons.Default.Face, "Sin captura adjunta") {
                SmallButton("Tomar foto", Icons.Default.PhotoCamera)
                SmallButton("Subir", Icons.Default.FileUpload, dark = false)
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
            CheckRow(terms, { terms = it }, Secondary,
                "Acepto los Términos y Condiciones del servicio de transporte y la Política de Privacidad de datos de Ruta Confiable.")
            CheckRow(whatsapp, { whatsapp = it }, Secondary,
                "Deseo recibir alertas de WhatsApp con el estado del bus y ubicación en tiempo real de mis encomiendas.")
        }

        if (!(terms && whatsapp)) Hint("Acepta ambas casillas para poder continuar.")
        PrimaryButton(text = "Crear cuenta de cliente", onClick = onSubmit, enabled = terms && whatsapp)
        TlsEncryptionBadge(Modifier.align(Alignment.CenterHorizontally))
    }
}

// -------------------------------------------------------------- CONDUCTOR

@Composable
private fun DriverForm(onSubmit: () -> Unit) {
    var fullName by rememberSaveable { mutableStateOf("") }
    var email by rememberSaveable { mutableStateOf("") }
    var password by remember { mutableStateOf("") }
    var license by rememberSaveable { mutableStateOf("") }
    var vehicleType by rememberSaveable { mutableStateOf("Camión Ligero") }
    var plate by rememberSaveable { mutableStateOf("") }
    var soat by rememberSaveable { mutableStateOf("") }
    var soatDate by rememberSaveable { mutableStateOf("") }
    var terms by rememberSaveable { mutableStateOf(false) }
    var policies by rememberSaveable { mutableStateOf(false) }

    Column(verticalArrangement = Arrangement.spacedBy(16.dp)) {
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
            PasswordTextField(password, { password = it }, "Contraseña", "Mínimo 8 caracteres")
            FieldLabel("Fotografía facial (Selfie biométrica)")
            UploadRow(Icons.Default.Person, "Rostro descubierto y nítido", "Sin gorras ni gafas oscuras", dashed = true) {
                SmallButton("Tomar foto", dark = false)
            }
            StatusTag("Validación facial obligatoria", StatusAvailableBg, StatusAvailable, Icons.Default.CheckCircle)
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
                Icons.Default.FilePresent, "Toca para cargar foto o PDF legible",
                "Frente y reverso en un solo archivo (Máx 5MB)", dashed = true
            ) { SmallButton("Subir", Icons.Default.FileUpload, dark = false) }
        }

        CardSectionContainer(
            title = "3. Requisitos del Vehículo",
            icon = Icons.Default.DirectionsCar,
            tagText = "Inspección Vial",
            tagColor = Outline
        ) {
            FieldLabel("Tipo de vehículo habilitado")
            Row(Modifier.fillMaxWidth(), Arrangement.spacedBy(8.dp)) {
                listOf(
                    "Camión Ligero" to Icons.Default.LocalShipping,
                    "Camioneta" to Icons.Default.AirportShuttle,
                    "Baúl Abierto" to Icons.Default.DirectionsBus
                ).forEach { (name, icon) ->
                    VehicleTypeChip(name, icon, vehicleType == name, Modifier.weight(1f)) { vehicleType = name }
                }
            }

            Row(Modifier.fillMaxWidth(), Arrangement.spacedBy(8.dp)) {
                AppOutlinedTextField(plate, { plate = it }, "Placa del vehículo", "AAA-123", Icons.Default.Subtitles, modifier = Modifier.weight(1f))
                AppOutlinedTextField(soat, { soat = it }, "Número de SOAT", "10094827", Icons.Default.Verified, modifier = Modifier.weight(1f))
            }

            Column {
                AppOutlinedTextField(
                    soatDate, { soatDate = it }, "Fecha de vencimiento del SOAT", "dd/mm/aaaa",
                    Icons.Default.CalendarToday,
                    trailingIcon = { Icon(Icons.Default.DateRange, null) }
                )
                Hint("El seguro obligatorio debe tener al menos 30 días de vigencia continua.", Modifier.padding(top = 4.dp, start = 4.dp))
            }

            FieldLabel("Foto de Tarjeta de Propiedad o SOAT físico")
            UploadRow(Icons.Default.Description, "Documento de control vehicular", "Formato JPG, PNG o PDF") {
                SmallButton("Subir")
            }

            Hint("Estado de Requerimientos Legales:")
            Row(Modifier.fillMaxWidth(), Arrangement.spacedBy(6.dp)) {
                StatusTag("✓ RUNT Conductor", StatusAvailableBg, StatusAvailable)
                StatusTag("⏱ Revisión SOAT", StatusPendingBg, StatusPending)
                StatusTag("✓ Carga Asegurada", SurfaceMuted, PrimaryLight)
            }
        }

        Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
            CheckRow(terms, { terms = it }, PrimaryLight,
                "Acepto los Términos y Condiciones de Ruta Confiable y autorizo el tratamiento de mis datos personales para verificación y seguridad logística.")
            CheckRow(policies, { policies = it }, PrimaryLight,
                "Acepto las políticas de validación física e inspección de carga en puestos de control de ruta.")
        }

        if (!(terms && policies)) Hint("Acepta ambas casillas para poder continuar.")
        PrimaryButton(text = "Crear cuenta de conductor", onClick = onSubmit, enabled = terms && policies)
    }
}

// ------------------------------------------------------ PIEZAS REUTILIZABLES

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
private fun CheckRow(checked: Boolean, onChange: (Boolean) -> Unit, color: Color, text: String) {
    Row(Modifier.fillMaxWidth(), verticalAlignment = Alignment.Top) {
        Checkbox(checked, onChange, colors = CheckboxDefaults.colors(checkedColor = color))
        Text(text, fontSize = 12.sp, color = OnSurfaceVariant, lineHeight = 16.sp, modifier = Modifier.padding(top = 10.dp))
    }
}

@Composable
private fun SmallButton(text: String, icon: ImageVector? = null, dark: Boolean = true) {
    Button(
        onClick = { },
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
    actions: @Composable RowScope.() -> Unit
) {
    val shape = RoundedCornerShape(8.dp)
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .then(
                if (dashed) Modifier.dashedBorder(1.dp, OutlineVariant, 8.dp)
                else Modifier.border(1.dp, OutlineVariant, shape)
            )
            .background(SurfaceSoft, shape)
            .padding(12.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Row(Modifier.weight(1f), verticalAlignment = Alignment.CenterVertically) {
            Surface(Modifier.size(40.dp), shape = CircleShape, color = SurfaceVariant) {
                Icon(icon, null, Modifier.padding(9.dp), tint = PrimaryLight)
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

@Preview(showBackground = true, heightDp = 1600)
@Composable
fun ClientRegistrationScreenPreview() {
    RutaConfiableTheme { RegistrationScreen(initialRole = RegistrationRole.CLIENT) }
}

@Preview(showBackground = true, heightDp = 1800)
@Composable
fun DriverRegistrationScreenPreview() {
    RutaConfiableTheme { RegistrationScreen(initialRole = RegistrationRole.DRIVER) }
}
