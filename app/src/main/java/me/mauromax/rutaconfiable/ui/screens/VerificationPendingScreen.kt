package me.mauromax.rutaconfiable.ui.screens

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.Logout
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import me.mauromax.rutaconfiable.ui.components.*
import me.mauromax.rutaconfiable.ui.data.AccountStatus
import me.mauromax.rutaconfiable.ui.data.AuthRepository
import me.mauromax.rutaconfiable.ui.data.VerificationStatus
import me.mauromax.rutaconfiable.ui.theme.*

/**
 * Espera la aprobación de la cuenta por parte de un administrador.
 * Escucha users/{uid} en tiempo real y llama a [onNavigateToProfile] en cuanto la cuenta queda
 * verificada. "Aceptar estado de verificación" deja seguir al perfil sin esperar la aprobación.
 * Si la cuenta es rechazada se muestra el motivo y solo se puede cerrar sesión.
 */
@Composable
fun VerificationPendingScreen(
    role: RegistrationRole = RegistrationRole.DRIVER,
    onNavigateToProfile: () -> Unit = {},
    onResetRegistration: () -> Unit = {}
) {
    var rejection by remember { mutableStateOf<AccountStatus?>(null) }
    val isDriver = role == RegistrationRole.DRIVER
    val currentOnApproved by rememberUpdatedState(onNavigateToProfile)

    // Avanza solo cuando el administrador aprueba (o muestra el rechazo).
    LaunchedEffect(Unit) {
        AuthRepository.observeAccountStatus().collect { status ->
            when (status.verification) {
                VerificationStatus.APPROVED -> currentOnApproved()
                VerificationStatus.REJECTED -> rejection = status
                VerificationStatus.PENDING -> rejection = null
            }
        }
    }

    Scaffold(
        topBar = { StandardAppTopBar(onBackClick = onResetRegistration, stepTagText = "Paso 2 de 2") },
        containerColor = Background
    ) { innerPadding ->
        ScreenColumn(innerPadding, Alignment.CenterHorizontally) {
            val rejected = rejection

            // Estado principal
            AppCard(
                Modifier.fillMaxWidth(), radius = 16.dp, padding = 20.dp,
                spacing = 12.dp, alignment = Alignment.CenterHorizontally
            ) {
                Surface(
                    Modifier.size(72.dp), shape = CircleShape,
                    color = if (rejected != null) StatusBookedBg else StatusPendingBg
                ) {
                    Icon(
                        if (rejected != null) Icons.Default.Block else Icons.Default.HourglassTop, null,
                        Modifier.padding(16.dp).fillMaxSize(),
                        tint = if (rejected != null) StatusBooked else StatusPending
                    )
                }

                PillTag(
                    color = if (rejected != null) StatusBookedBg else StatusPendingBg,
                    padding = PaddingValues(horizontal = 12.dp, vertical = 6.dp)
                ) {
                    val tint = if (rejected != null) StatusBooked else StatusPending
                    Icon(
                        if (rejected != null) Icons.Default.ErrorOutline else Icons.Default.AccessTime,
                        null, Modifier.size(14.dp), tint = tint
                    )
                    Spacer(Modifier.width(6.dp))
                    Text(
                        if (rejected != null) "VERIFICACIÓN RECHAZADA" else "VERIFICACIÓN EN PROCESO",
                        fontSize = 11.sp, fontWeight = FontWeight.Bold,
                        color = tint, letterSpacing = 0.5.sp
                    )
                }

                if (rejected != null) {
                    ScreenTitle("No pudimos verificar tu identidad", 22.sp, textAlign = TextAlign.Center)
                    BodyText(
                        rejected.rejectionReason
                            ?: "Revisamos tu documentación y no pudimos aprobarla. Comunícate con soporte para corregir tus datos.",
                        textAlign = TextAlign.Center
                    )
                } else {
                    ScreenTitle("Documentos en Revisión Legal", 22.sp, textAlign = TextAlign.Center)
                    BodyText(
                        if (isDriver) {
                            "Estamos cruzando tu documentación (Licencia, SOAT, RUNT y Selfie biométrica) con las entidades de control vehicular y seguridad logística nacional."
                        } else {
                            "Estamos validando tus datos de identidad, antecedentes y selfie biométrica con la Registraduría y sistemas de seguridad vial."
                        },
                        textAlign = TextAlign.Center
                    )
                }
            }

            // Pasos de la verificación
            if (rejected == null) {
                AppCard(Modifier.fillMaxWidth(), padding = 16.dp, spacing = 14.dp) {
                    Text(
                        "Estado de Verificación Paso a Paso", fontSize = 14.sp,
                        fontWeight = FontWeight.Bold, color = PrimaryLight
                    )
                    HorizontalDivider(color = SurfaceSoft)
                    TimelineStepRow(
                        "1. Envío de formulario y adjuntos",
                        "Datos de la cuenta enviados correctamente",
                        isCompleted = true
                    )
                    TimelineStepRow(
                        if (isDriver) "2. Validación RUNT, SOAT y Licencia" else "2. Validación de Identidad y Biometría",
                        "Un administrador revisa tu información. Esta pantalla avanza sola al aprobarla.",
                        isInProgress = true
                    )
                    TimelineStepRow(
                        "3. Habilitación de perfil operativo",
                        "Aprobación final para activar tu cuenta"
                    )
                }
            }

            Column(Modifier.fillMaxWidth(), verticalArrangement = Arrangement.spacedBy(8.dp)) {
                if (rejected == null) {
                    PrimaryButton(
                        "Aceptar estado de verificación", onNavigateToProfile,
                        leadingIcon = Icons.Default.VerifiedUser
                    )
                }
                if (rejected != null) {
                    PrimaryButton(
                        "Cerrar sesión", onResetRegistration,
                        leadingIcon = Icons.AutoMirrored.Filled.Logout,
                        trailingIcon = null
                    )
                }
            }
        }
    }
}

@Composable
private fun TimelineStepRow(
    title: String,
    subtitle: String,
    isCompleted: Boolean = false,
    isInProgress: Boolean = false
) {
    val background = when {
        isCompleted -> StatusAvailableBg
        isInProgress -> StatusPendingBg
        else -> SurfaceMuted
    }
    val color = when {
        isCompleted -> StatusAvailable
        isInProgress -> StatusPending
        else -> Outline
    }
    val icon = when {
        isCompleted -> Icons.Default.Check
        isInProgress -> Icons.Default.Refresh
        else -> Icons.Default.Lock
    }

    Row(verticalAlignment = Alignment.CenterVertically) {
        Surface(Modifier.size(28.dp), shape = CircleShape, color = background) {
            Icon(icon, null, Modifier.padding(6.dp), tint = color)
        }
        Spacer(Modifier.width(12.dp))
        Column {
            Text(
                title, fontSize = 13.sp, fontWeight = FontWeight.Bold,
                color = if (isCompleted || isInProgress) PrimaryLight else Outline
            )
            Hint(subtitle)
        }
    }
}

@Preview(showBackground = true)
@Composable
fun VerificationPendingScreenPreview() {
    RutaConfiableTheme { VerificationPendingScreen() }
}