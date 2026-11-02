package me.mauromax.rutaconfiable.ui.screens

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowForward
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import me.mauromax.rutaconfiable.ui.components.*
import me.mauromax.rutaconfiable.ui.theme.*

enum class RegistrationRole {
    DRIVER,
    CLIENT
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun RegistrationScreen(
    initialRole: RegistrationRole = RegistrationRole.DRIVER,
    onRegistrationSubmitted: (role: RegistrationRole) -> Unit = {},
    onBackClick: () -> Unit = {},
    onLoginClick: () -> Unit = {}
) {
    var currentRole by remember { mutableStateOf(initialRole) }

    Scaffold(
        topBar = {
            StandardAppTopBar(
                onBackClick = onBackClick,
                showSecurityBadge = true
            )
        },
        containerColor = Background
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .verticalScroll(rememberScrollState())
                .padding(horizontal = 16.dp, vertical = 12.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {

            // Role Switcher (Conductor / Cliente)
            Surface(
                modifier = Modifier.fillMaxWidth(),
                color = Color(0xFFEFF4FF),
                shape = RoundedCornerShape(12.dp)
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(4.dp)
                ) {
                    RoleOptionButton(
                        text = "Conductor",
                        icon = Icons.Default.LocalShipping,
                        isSelected = currentRole == RegistrationRole.DRIVER,
                        modifier = Modifier.weight(1f),
                        onClick = { currentRole = RegistrationRole.DRIVER }
                    )
                    RoleOptionButton(
                        text = "Cliente",
                        icon = Icons.Default.Person,
                        isSelected = currentRole == RegistrationRole.CLIENT,
                        modifier = Modifier.weight(1f),
                        onClick = { currentRole = RegistrationRole.CLIENT }
                    )
                }
            }

            // Form content depending on selected role
            if (currentRole == RegistrationRole.DRIVER) {
                DriverRegistrationFormContent(
                    onSubmit = { onRegistrationSubmitted(RegistrationRole.DRIVER) }
                )
            } else {
                ClientRegistrationFormContent(
                    onSubmit = { onRegistrationSubmitted(RegistrationRole.CLIENT) }
                )
            }

            // Shared Footer Link
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 4.dp),
                horizontalArrangement = Arrangement.Center,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "¿Ya tienes una cuenta registrada? ",
                    fontSize = 13.sp,
                    color = Color(0xFF44474D)
                )
                Text(
                    text = "Iniciar sesión",
                    fontSize = 13.sp,
                    fontWeight = FontWeight.Bold,
                    color = Secondary,
                    modifier = Modifier.clickable { onLoginClick() }
                )
            }

            Spacer(modifier = Modifier.height(16.dp))
        }
    }
}

// CLIENT REGISTRATION FORM CONTENT
@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun ClientRegistrationFormContent(
    onSubmit: () -> Unit
) {
    var fullName by remember { mutableStateOf("") }
    var email by remember { mutableStateOf("") }
    var phone by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }
    var documentType by remember { mutableStateOf("C.C.") }
    var documentNumber by remember { mutableStateOf("") }
    var docDropdownExpanded by remember { mutableStateOf(false) }
    var termsAccepted by remember { mutableStateOf(false) }
    var whatsappAlertsAccepted by remember { mutableStateOf(false) }

    Column(verticalArrangement = Arrangement.spacedBy(16.dp)) {

        // Header Info
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Surface(
                color = Color(0xFFE5EEFF),
                shape = RoundedCornerShape(16.dp)
            ) {
                Row(
                    modifier = Modifier.padding(horizontal = 10.dp, vertical = 4.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Icon(
                        imageVector = Icons.Default.Person,
                        contentDescription = null,
                        tint = Secondary,
                        modifier = Modifier.size(14.dp)
                    )
                    Spacer(modifier = Modifier.width(4.dp))
                    Text(
                        text = "REGISTRO DE CLIENTE",
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Bold,
                        color = PrimaryLight
                    )
                }
            }

            Surface(
                color = Color(0xFFE5EEFF),
                shape = RoundedCornerShape(16.dp)
            ) {
                Text(
                    text = "Paso 1 de 2",
                    modifier = Modifier.padding(horizontal = 10.dp, vertical = 4.dp),
                    fontSize = 11.sp,
                    fontWeight = FontWeight.Bold,
                    color = PrimaryLight
                )
            }
        }

        Text(
            text = "Crea tu cuenta de Cliente",
            fontSize = 24.sp,
            fontWeight = FontWeight.Bold,
            color = PrimaryLight,
            fontFamily = FontFamily.Serif
        )

        Text(
            text = "Reserva cupos cómodos en viajes intermunicipales o despacha encomiendas con monitoreo satelital en tiempo real.",
            fontSize = 13.sp,
            color = Color(0xFF44474D),
            lineHeight = 18.sp
        )

        // Section 1: Datos Personales y Acceso
        CardSectionContainer(
            title = "1. Datos Personales y Acceso",
            icon = Icons.Default.Badge,
            subtitle = "Tus credenciales para gestionar reservas y despachos"
        ) {
            OutlinedTextField(
                value = fullName,
                onValueChange = { fullName = it },
                label = { Text("Nombre completo") },
                placeholder = { Text("Ej: Valentina Gómez Salazar") },
                leadingIcon = { Icon(Icons.Default.Person, contentDescription = null) },
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(8.dp),
                colors = customTextFieldColors()
            )

            OutlinedTextField(
                value = email,
                onValueChange = { email = it },
                label = { Text("Correo electrónico") },
                placeholder = { Text("ejemplo@correo.com") },
                leadingIcon = { Icon(Icons.Default.Email, contentDescription = null) },
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(8.dp),
                colors = customTextFieldColors()
            )

            // Phone field with WhatsApp country code selector
            Column {
                Text(
                    text = "Número de WhatsApp / Celular",
                    fontSize = 12.sp,
                    fontWeight = FontWeight.SemiBold,
                    color = PrimaryLight,
                    modifier = Modifier.padding(bottom = 4.dp)
                )
                PhoneWithCountrySelectorField(
                    phoneValue = phone,
                    onPhoneChange = { phone = it },
                    placeholderText = "310 456 7890"
                )
            }

            Column {
                PasswordTextField(
                    value = password,
                    onValueChange = { password = it },
                    label = "Contraseña segura",
                    placeholder = "Mínimo 8 caracteres"
                )
                Text(
                    text = "Incluye mayúsculas, números o caracteres especiales.",
                    fontSize = 11.sp,
                    color = Color(0xFF74777E),
                    modifier = Modifier.padding(top = 4.dp, start = 4.dp)
                )
            }
        }

        // Section 2: Validación de Seguridad
        CardSectionContainer(
            title = "2. Validación de Seguridad",
            icon = Icons.Default.VerifiedUser,
            subtitle = "Requerido para control de abordaje en terminal"
        ) {
            Text(
                text = "Documento de Identidad",
                fontSize = 12.sp,
                fontWeight = FontWeight.SemiBold,
                color = PrimaryLight
            )

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Box {
                    Surface(
                        modifier = Modifier
                            .height(56.dp)
                            .clickable { docDropdownExpanded = true },
                        shape = RoundedCornerShape(8.dp),
                        color = Color(0xFFEFF4FF),
                        border = BorderStroke(1.dp, Color(0xFFC4C6CE))
                    ) {
                        Row(
                            modifier = Modifier.padding(horizontal = 12.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text(documentType, fontSize = 13.sp, fontWeight = FontWeight.Bold, color = PrimaryLight)
                            Spacer(modifier = Modifier.width(4.dp))
                            Icon(Icons.Default.ArrowDropDown, contentDescription = null, tint = PrimaryLight)
                        }
                    }

                    DropdownMenu(
                        expanded = docDropdownExpanded,
                        onDismissRequest = { docDropdownExpanded = false }
                    ) {
                        DropdownMenuItem(
                            text = { Text("C.C.") },
                            onClick = { documentType = "C.C."; docDropdownExpanded = false }
                        )
                        DropdownMenuItem(
                            text = { Text("C.E.") },
                            onClick = { documentType = "C.E."; docDropdownExpanded = false }
                        )
                        DropdownMenuItem(
                            text = { Text("Pasaporte") },
                            onClick = { documentType = "Pasaporte"; docDropdownExpanded = false }
                        )
                    }
                }

                OutlinedTextField(
                    value = documentNumber,
                    onValueChange = { documentNumber = it },
                    placeholder = { Text("1.020.485.934") },
                    leadingIcon = { Icon(Icons.Default.Pin, contentDescription = null) },
                    modifier = Modifier.weight(1f),
                    shape = RoundedCornerShape(8.dp),
                    colors = customTextFieldColors()
                )
            }

            // Selfie de verificación biométrica
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "Selfie de verificación biométrica",
                    fontSize = 12.sp,
                    fontWeight = FontWeight.SemiBold,
                    color = PrimaryLight
                )
                Text(
                    text = "Obligatorio",
                    fontSize = 11.sp,
                    fontWeight = FontWeight.Bold,
                    color = Secondary
                )
            }

            Text(
                text = "Rostro descubierto y nítido para entrega segura de encomiendas y abordaje en ruta.",
                fontSize = 11.sp,
                color = Color(0xFF74777E)
            )

            Surface(
                modifier = Modifier.fillMaxWidth(),
                color = Color(0xFFEFF4FF),
                shape = RoundedCornerShape(8.dp)
            ) {
                Row(
                    modifier = Modifier.padding(12.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Surface(
                            modifier = Modifier.size(44.dp),
                            shape = CircleShape,
                            color = Color(0xFFD3E4FE)
                        ) {
                            Icon(
                                imageVector = Icons.Default.Face,
                                contentDescription = null,
                                tint = PrimaryLight,
                                modifier = Modifier.padding(10.dp)
                            )
                        }
                        Spacer(modifier = Modifier.width(10.dp))
                        Text(
                            text = "Sin captura adjunta",
                            fontSize = 12.sp,
                            fontWeight = FontWeight.Bold,
                            color = PrimaryLight
                        )
                    }

                    Row(horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                        Button(
                            onClick = { },
                            shape = RoundedCornerShape(6.dp),
                            colors = ButtonDefaults.buttonColors(containerColor = PrimaryLight),
                            contentPadding = PaddingValues(horizontal = 10.dp, vertical = 4.dp)
                        ) {
                            Icon(Icons.Default.PhotoCamera, contentDescription = null, modifier = Modifier.size(14.dp))
                            Spacer(modifier = Modifier.width(4.dp))
                            Text("Tomar foto", fontSize = 11.sp, fontWeight = FontWeight.Bold)
                        }

                        Button(
                            onClick = { },
                            shape = RoundedCornerShape(6.dp),
                            colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFD3E4FE)),
                            contentPadding = PaddingValues(horizontal = 10.dp, vertical = 4.dp)
                        ) {
                            Icon(Icons.Default.FileUpload, contentDescription = null, tint = PrimaryLight, modifier = Modifier.size(14.dp))
                            Spacer(modifier = Modifier.width(4.dp))
                            Text("Subir", fontSize = 11.sp, color = PrimaryLight, fontWeight = FontWeight.Bold)
                        }
                    }
                }
            }
        }

        // Section 3: Métodos de Pago
        Surface(
            modifier = Modifier.fillMaxWidth(),
            color = Color.White,
            shape = RoundedCornerShape(12.dp),
            border = BorderStroke(1.dp, Color(0xFFC4C6CE)),
            shadowElevation = 1.dp
        ) {
            Row(
                modifier = Modifier.padding(14.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Row(verticalAlignment = Alignment.CenterVertically, modifier = Modifier.weight(1f)) {
                    Surface(
                        modifier = Modifier.size(36.dp),
                        shape = RoundedCornerShape(6.dp),
                        color = Color(0xFFE5EEFF)
                    ) {
                        Icon(
                            imageVector = Icons.Default.CreditCard,
                            contentDescription = null,
                            tint = PrimaryLight,
                            modifier = Modifier.padding(8.dp)
                        )
                    }
                    Spacer(modifier = Modifier.width(10.dp))
                    Column {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Text(
                                text = "3. Métodos de Pago",
                                fontSize = 13.sp,
                                fontWeight = FontWeight.Bold,
                                color = PrimaryLight
                            )
                            Spacer(modifier = Modifier.width(6.dp))
                            Surface(
                                color = StatusPendingBg,
                                shape = RoundedCornerShape(4.dp)
                            ) {
                                Text(
                                    text = "Opcional",
                                    fontSize = 10.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = Secondary,
                                    modifier = Modifier.padding(horizontal = 4.dp, vertical = 2.dp)
                                )
                            }
                        }
                        Text(
                            text = "Tarjetas débito/crédito y PSE",
                            fontSize = 11.sp,
                            color = Color(0xFF74777E)
                        )
                    }
                }

                Button(
                    onClick = { },
                    shape = RoundedCornerShape(8.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = PrimaryLight),
                    contentPadding = PaddingValues(horizontal = 12.dp, vertical = 6.dp)
                ) {
                    Text("+ Configurar", fontSize = 12.sp, fontWeight = FontWeight.Bold)
                    Spacer(modifier = Modifier.width(4.dp))
                    Icon(Icons.AutoMirrored.Filled.ArrowForward, contentDescription = null, modifier = Modifier.size(14.dp))
                }
            }
        }

        // Insurance Info Banner
        Surface(
            modifier = Modifier.fillMaxWidth(),
            color = Color(0xFFEFF4FF),
            shape = RoundedCornerShape(12.dp)
        ) {
            Row(
                modifier = Modifier.padding(12.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Surface(
                    modifier = Modifier.size(40.dp),
                    shape = RoundedCornerShape(8.dp),
                    color = SecondaryContainer
                ) {
                    Icon(
                        imageVector = Icons.Default.DirectionsBus,
                        contentDescription = null,
                        tint = Color.White,
                        modifier = Modifier.padding(10.dp)
                    )
                }
                Spacer(modifier = Modifier.width(12.dp))
                Column {
                    Text(
                        text = "Póliza de Pasajero y Mercancía",
                        fontSize = 13.sp,
                        fontWeight = FontWeight.Bold,
                        color = PrimaryLight
                    )
                    Text(
                        text = "Cobertura activa desde el abordaje hasta la entrega final en destino.",
                        fontSize = 11.sp,
                        color = Color(0xFF44474D),
                        lineHeight = 15.sp
                    )
                }
            }
        }

        // Checkboxes
        Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
            Row(
                verticalAlignment = Alignment.Top,
                modifier = Modifier.fillMaxWidth()
            ) {
                Checkbox(
                    checked = termsAccepted,
                    onCheckedChange = { termsAccepted = it },
                    colors = CheckboxDefaults.colors(checkedColor = Secondary)
                )
                Text(
                    text = "Acepto los Términos y Condiciones del servicio de transporte y la Política de Privacidad de datos de Ruta Confiable.",
                    fontSize = 12.sp,
                    color = Color(0xFF44474D),
                    lineHeight = 16.sp,
                    modifier = Modifier.padding(top = 10.dp)
                )
            }

            Row(
                verticalAlignment = Alignment.Top,
                modifier = Modifier.fillMaxWidth()
            ) {
                Checkbox(
                    checked = whatsappAlertsAccepted,
                    onCheckedChange = { whatsappAlertsAccepted = it },
                    colors = CheckboxDefaults.colors(checkedColor = Secondary)
                )
                Text(
                    text = "Deseo recibir alertas de WhatsApp con el estado del bus y ubicación en tiempo real de mis encomiendas.",
                    fontSize = 12.sp,
                    color = Color(0xFF44474D),
                    lineHeight = 16.sp,
                    modifier = Modifier.padding(top = 10.dp)
                )
            }
        }

        // Primary Action Button
        PrimaryButton(
            text = "Crear cuenta de cliente",
            onClick = onSubmit
        )

        // Encrypted Security Note
        TlsEncryptionBadge(
            modifier = Modifier.align(Alignment.CenterHorizontally)
        )
    }
}

// DRIVER REGISTRATION FORM CONTENT
@Composable
private fun DriverRegistrationFormContent(
    onSubmit: () -> Unit
) {
    var fullName by remember { mutableStateOf("") }
    var email by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }
    var licenseNumber by remember { mutableStateOf("") }
    var selectedVehicleType by remember { mutableStateOf("Camión Ligero") }
    var vehiclePlate by remember { mutableStateOf("") }
    var soatNumber by remember { mutableStateOf("") }
    var soatExpiration by remember { mutableStateOf("") }
    var termsAccepted by remember { mutableStateOf(false) }
    var policiesAccepted by remember { mutableStateOf(false) }

    Column(verticalArrangement = Arrangement.spacedBy(16.dp)) {

        // Header Info
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Surface(
                color = Color(0xFFE5EEFF),
                shape = RoundedCornerShape(16.dp)
            ) {
                Row(
                    modifier = Modifier.padding(horizontal = 10.dp, vertical = 4.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Icon(
                        imageVector = Icons.Default.LocalShipping,
                        contentDescription = null,
                        tint = Secondary,
                        modifier = Modifier.size(14.dp)
                    )
                    Spacer(modifier = Modifier.width(4.dp))
                    Text(
                        text = "REGISTRO DE CONDUCTOR",
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Bold,
                        color = PrimaryLight
                    )
                }
            }

            Surface(
                color = Color(0xFFE5EEFF),
                shape = RoundedCornerShape(16.dp)
            ) {
                Text(
                    text = "Paso 1 de 2",
                    modifier = Modifier.padding(horizontal = 10.dp, vertical = 4.dp),
                    fontSize = 11.sp,
                    fontWeight = FontWeight.Bold,
                    color = PrimaryLight
                )
            }
        }

        Text(
            text = "Crea tu cuenta de Conductor",
            fontSize = 24.sp,
            fontWeight = FontWeight.Bold,
            color = PrimaryLight,
            fontFamily = FontFamily.Serif
        )

        Text(
            text = "Coordinación logística regional y flete seguro. Selecciona tu perfil operativo para comenzar.",
            fontSize = 13.sp,
            color = Color(0xFF44474D),
            lineHeight = 18.sp
        )

        // Section 1: Información Personal
        CardSectionContainer(
            title = "1. Información Personal",
            icon = Icons.Default.Badge,
            tagText = "Obligatorio",
            tagColor = Color(0xFF74777E)
        ) {
            OutlinedTextField(
                value = fullName,
                onValueChange = { fullName = it },
                label = { Text("Nombre completo") },
                placeholder = { Text("Ej. Carlos Mendoza Rivas") },
                leadingIcon = { Icon(Icons.Default.Person, contentDescription = null) },
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(8.dp),
                colors = customTextFieldColors()
            )

            OutlinedTextField(
                value = email,
                onValueChange = { email = it },
                label = { Text("Correo electrónico") },
                placeholder = { Text("operaciones@ejemplo.com") },
                leadingIcon = { Icon(Icons.Default.Email, contentDescription = null) },
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(8.dp),
                colors = customTextFieldColors()
            )

            PasswordTextField(
                value = password,
                onValueChange = { password = it },
                label = "Contraseña",
                placeholder = "Mínimo 8 caracteres"
            )

            // Selfie biométrica
            Text(
                text = "Fotografía facial (Selfie biométrica)",
                fontSize = 12.sp,
                fontWeight = FontWeight.SemiBold,
                color = PrimaryLight
            )

            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .dashedBorder(1.dp, Color(0xFFC4C6CE), 8.dp)
                    .background(Color(0xFFEFF4FF), RoundedCornerShape(8.dp))
                    .padding(12.dp)
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically, modifier = Modifier.weight(1f)) {
                        Box {
                            Surface(
                                modifier = Modifier.size(44.dp),
                                shape = CircleShape,
                                color = Color(0xFFD3E4FE)
                            ) {
                                Icon(
                                    imageVector = Icons.Default.Person,
                                    contentDescription = null,
                                    modifier = Modifier
                                        .padding(8.dp)
                                        .fillMaxSize(),
                                    tint = PrimaryLight
                                )
                            }
                            Surface(
                                modifier = Modifier
                                    .size(16.dp)
                                    .align(Alignment.BottomEnd),
                                shape = CircleShape,
                                color = Secondary
                            ) {
                                Icon(
                                    imageVector = Icons.Default.Add,
                                    contentDescription = null,
                                    tint = Color.White,
                                    modifier = Modifier.padding(2.dp)
                                )
                            }
                        }
                        Spacer(modifier = Modifier.width(10.dp))
                        Column {
                            Text(
                                text = "Rostro descubierto y nítido",
                                fontSize = 12.sp,
                                fontWeight = FontWeight.Bold,
                                color = PrimaryLight
                            )
                            Text(
                                text = "Sin gorras ni gafas oscuras",
                                fontSize = 11.sp,
                                color = Color(0xFF74777E)
                            )
                        }
                    }

                    OutlinedButton(
                        onClick = { },
                        shape = RoundedCornerShape(8.dp),
                        contentPadding = PaddingValues(horizontal = 12.dp, vertical = 4.dp),
                        border = BorderStroke(1.dp, Color(0xFFC4C6CE))
                    ) {
                        Text("Tomar foto", fontSize = 12.sp, color = PrimaryLight, fontWeight = FontWeight.Bold)
                    }
                }
            }

            // Status Tag
            StatusTag(
                text = "Validación facial obligatoria",
                bgColor = StatusAvailableBg,
                textColor = StatusAvailable,
                icon = Icons.Default.CheckCircle
            )
        }

        // Section 2: Licencia de Conducción
        CardSectionContainer(
            title = "2. Licencia de Conducción",
            icon = Icons.Default.AssignmentInd,
            tagText = "RUNT Vigente",
            tagColor = Secondary
        ) {
            OutlinedTextField(
                value = licenseNumber,
                onValueChange = { licenseNumber = it },
                label = { Text("Número de licencia (Pase)") },
                placeholder = { Text("Ej. 1020485934") },
                leadingIcon = { Icon(Icons.Default.Pin, contentDescription = null) },
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(8.dp),
                colors = customTextFieldColors()
            )

            Text(
                text = "Cargar documento de licencia",
                fontSize = 12.sp,
                fontWeight = FontWeight.SemiBold,
                color = PrimaryLight
            )

            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .dashedBorder(1.dp, Color(0xFFC4C6CE), 8.dp)
                    .background(Color(0xFFEFF4FF), RoundedCornerShape(8.dp))
                    .padding(16.dp)
            ) {
                Column(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Icon(
                        imageVector = Icons.Default.FileUpload,
                        contentDescription = null,
                        tint = PrimaryLight,
                        modifier = Modifier.size(32.dp)
                    )
                    Spacer(modifier = Modifier.height(4.dp))
                    Text(
                        text = "Toca para cargar foto o PDF legible",
                        fontSize = 12.sp,
                        fontWeight = FontWeight.Bold,
                        color = PrimaryLight
                    )
                    Text(
                        text = "Frente y reverso en un solo archivo (Máx 5MB)",
                        fontSize = 11.sp,
                        color = Color(0xFF74777E)
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                    Button(
                        onClick = { },
                        shape = RoundedCornerShape(8.dp),
                        colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFD3E4FE)),
                        contentPadding = PaddingValues(horizontal = 14.dp, vertical = 6.dp)
                    ) {
                        Icon(
                            imageVector = Icons.Default.FilePresent,
                            contentDescription = null,
                            tint = PrimaryLight,
                            modifier = Modifier.size(16.dp)
                        )
                        Spacer(modifier = Modifier.width(6.dp))
                        Text("Seleccionar archivo", fontSize = 12.sp, color = PrimaryLight, fontWeight = FontWeight.Bold)
                    }
                }
            }
        }

        // Section 3: Requisitos del Vehículo
        CardSectionContainer(
            title = "3. Requisitos del Vehículo",
            icon = Icons.Default.DirectionsCar,
            tagText = "Inspección Vial",
            tagColor = Color(0xFF74777E)
        ) {
            Text(
                text = "Tipo de vehículo habilitado",
                fontSize = 12.sp,
                fontWeight = FontWeight.SemiBold,
                color = PrimaryLight
            )

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                VehicleTypeChip(
                    title = "Camión Ligero",
                    icon = Icons.Default.LocalShipping,
                    isSelected = selectedVehicleType == "Camión Ligero",
                    modifier = Modifier.weight(1f),
                    onClick = { selectedVehicleType = "Camión Ligero" }
                )
                VehicleTypeChip(
                    title = "Camioneta",
                    icon = Icons.Default.AirportShuttle,
                    isSelected = selectedVehicleType == "Camioneta",
                    modifier = Modifier.weight(1f),
                    onClick = { selectedVehicleType = "Camioneta" }
                )
                VehicleTypeChip(
                    title = "Baúl Abierto",
                    icon = Icons.Default.DirectionsBus,
                    isSelected = selectedVehicleType == "Baúl Abierto",
                    modifier = Modifier.weight(1f),
                    onClick = { selectedVehicleType = "Baúl Abierto" }
                )
            }

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                OutlinedTextField(
                    value = vehiclePlate,
                    onValueChange = { vehiclePlate = it },
                    label = { Text("Placa del vehículo") },
                    placeholder = { Text("AAA-123") },
                    leadingIcon = { Icon(Icons.Default.Subtitles, contentDescription = null) },
                    modifier = Modifier.weight(1f),
                    shape = RoundedCornerShape(8.dp),
                    colors = customTextFieldColors()
                )

                OutlinedTextField(
                    value = soatNumber,
                    onValueChange = { soatNumber = it },
                    label = { Text("Número de SOAT") },
                    placeholder = { Text("10094827") },
                    leadingIcon = { Icon(Icons.Default.Verified, contentDescription = null) },
                    modifier = Modifier.weight(1f),
                    shape = RoundedCornerShape(8.dp),
                    colors = customTextFieldColors()
                )
            }

            Column {
                OutlinedTextField(
                    value = soatExpiration,
                    onValueChange = { soatExpiration = it },
                    label = { Text("Fecha de vencimiento del SOAT") },
                    placeholder = { Text("dd/mm/aaaa") },
                    leadingIcon = { Icon(Icons.Default.CalendarToday, contentDescription = null) },
                    trailingIcon = { Icon(Icons.Default.DateRange, contentDescription = null) },
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(8.dp),
                    colors = customTextFieldColors()
                )
                Text(
                    text = "El seguro obligatorio debe tener al menos 30 días de vigencia continua.",
                    fontSize = 11.sp,
                    color = Color(0xFF74777E),
                    modifier = Modifier.padding(top = 4.dp, start = 4.dp)
                )
            }

            Text(
                text = "Foto de Tarjeta de Propiedad o SOAT físico",
                fontSize = 12.sp,
                fontWeight = FontWeight.SemiBold,
                color = PrimaryLight
            )

            Surface(
                modifier = Modifier.fillMaxWidth(),
                color = Color(0xFFEFF4FF),
                shape = RoundedCornerShape(8.dp),
                border = BorderStroke(1.dp, Color(0xFFC4C6CE))
            ) {
                Row(
                    modifier = Modifier.padding(12.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically, modifier = Modifier.weight(1f)) {
                        Surface(
                            modifier = Modifier.size(36.dp),
                            shape = RoundedCornerShape(6.dp),
                            color = Color(0xFFD3E4FE)
                        ) {
                            Icon(
                                imageVector = Icons.Default.Description,
                                contentDescription = null,
                                tint = PrimaryLight,
                                modifier = Modifier.padding(8.dp)
                            )
                        }
                        Spacer(modifier = Modifier.width(10.dp))
                        Column {
                            Text(
                                text = "Documento de control vehicular",
                                fontSize = 12.sp,
                                fontWeight = FontWeight.Bold,
                                color = PrimaryLight
                            )
                            Text(
                                text = "Formato JPG, PNG o PDF",
                                fontSize = 11.sp,
                                color = Color(0xFF74777E)
                            )
                        }
                    }

                    Button(
                        onClick = { },
                        shape = RoundedCornerShape(8.dp),
                        colors = ButtonDefaults.buttonColors(containerColor = PrimaryLight),
                        contentPadding = PaddingValues(horizontal = 14.dp, vertical = 4.dp)
                    ) {
                        Text("Subir", fontSize = 12.sp, fontWeight = FontWeight.Bold)
                    }
                }
            }

            // Legal Requirement Statuses
            Text(
                text = "Estado de Requerimientos Legales:",
                fontSize = 11.sp,
                color = Color(0xFF74777E)
            )

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(6.dp)
            ) {
                StatusTag(
                    text = "✓ RUNT Conductor",
                    bgColor = StatusAvailableBg,
                    textColor = StatusAvailable
                )
                StatusTag(
                    text = "⏱ Revisión SOAT",
                    bgColor = StatusPendingBg,
                    textColor = StatusPending
                )
                StatusTag(
                    text = "✓ Carga Asegurada",
                    bgColor = Color(0xFFE5EEFF),
                    textColor = PrimaryLight
                )
            }
        }

        // Checkboxes
        Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
            Row(
                verticalAlignment = Alignment.Top,
                modifier = Modifier.fillMaxWidth()
            ) {
                Checkbox(
                    checked = termsAccepted,
                    onCheckedChange = { termsAccepted = it },
                    colors = CheckboxDefaults.colors(checkedColor = PrimaryLight)
                )
                Text(
                    text = "Acepto los Términos y Condiciones de Ruta Confiable y autorizo el tratamiento de mis datos personales para verificación y seguridad logística.",
                    fontSize = 12.sp,
                    color = Color(0xFF44474D),
                    lineHeight = 16.sp,
                    modifier = Modifier.padding(top = 10.dp)
                )
            }

            Row(
                verticalAlignment = Alignment.Top,
                modifier = Modifier.fillMaxWidth()
            ) {
                Checkbox(
                    checked = policiesAccepted,
                    onCheckedChange = { policiesAccepted = it },
                    colors = CheckboxDefaults.colors(checkedColor = PrimaryLight)
                )
                Text(
                    text = "Acepto las políticas de validación física e inspección de carga en puestos de control de ruta.",
                    fontSize = 12.sp,
                    color = Color(0xFF44474D),
                    lineHeight = 16.sp,
                    modifier = Modifier.padding(top = 10.dp)
                )
            }
        }

        // Primary Action Button
        PrimaryButton(
            text = "Crear cuenta de conductor",
            onClick = onSubmit
        )
    }
}

@Preview(showBackground = true, heightDp = 1600)
@Composable
fun ClientRegistrationScreenPreview() {
    RutaConfiableTheme {
        RegistrationScreen(initialRole = RegistrationRole.CLIENT)
    }
}

@Preview(showBackground = true, heightDp = 1800)
@Composable
fun DriverRegistrationScreenPreview() {
    RutaConfiableTheme {
        RegistrationScreen(initialRole = RegistrationRole.DRIVER)
    }
}
