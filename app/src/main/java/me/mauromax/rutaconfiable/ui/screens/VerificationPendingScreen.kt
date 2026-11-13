package me.mauromax.rutaconfiable.ui.screens

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AccessTime
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.HourglassTop
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material.icons.filled.VerifiedUser
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import me.mauromax.rutaconfiable.ui.components.AppCard
import me.mauromax.rutaconfiable.ui.components.BodyText
import me.mauromax.rutaconfiable.ui.components.Hint
import me.mauromax.rutaconfiable.ui.components.PillTag
import me.mauromax.rutaconfiable.ui.components.PrimaryButton
import me.mauromax.rutaconfiable.ui.components.ScreenColumn
import me.mauromax.rutaconfiable.ui.components.ScreenTitle
import me.mauromax.rutaconfiable.ui.components.SecondaryOutlinedButton
import me.mauromax.rutaconfiable.ui.components.StandardAppTopBar
import me.mauromax.rutaconfiable.ui.theme.Background
import me.mauromax.rutaconfiable.ui.theme.Outline
import me.mauromax.rutaconfiable.ui.theme.OutlineVariant
import me.mauromax.rutaconfiable.ui.theme.PrimaryLight
import me.mauromax.rutaconfiable.ui.theme.RutaConfiableTheme
import me.mauromax.rutaconfiable.ui.theme.StatusAvailable
import me.mauromax.rutaconfiable.ui.theme.StatusAvailableBg
import me.mauromax.rutaconfiable.ui.theme.StatusPending
import me.mauromax.rutaconfiable.ui.theme.StatusPendingBg
import me.mauromax.rutaconfiable.ui.theme.SurfaceMuted
import me.mauromax.rutaconfiable.ui.theme.SurfaceSoft

@Composable
fun VerificationPendingScreen(
    role: RegistrationRole = RegistrationRole.DRIVER,
    onNavigateToProfile: () -> Unit = {},
    onResetRegistration: () -> Unit = {}
) {
    var isCheckingStatus by remember { mutableStateOf(false) }
    val isDriver = role == RegistrationRole.DRIVER

    Scaffold(
        topBar = { StandardAppTopBar(onBackClick = onResetRegistration, stepTagText = "Paso 2 de 2") },
        containerColor = Background
    ) { innerPadding ->
        ScreenColumn(innerPadding, Alignment.CenterHorizontally) {
            // Estado principal
            AppCard(
                Modifier.fillMaxWidth(), radius = 16.dp, padding = 20.dp,
                spacing = 12.dp, alignment = Alignment.CenterHorizontally
            ) {
                Surface(Modifier.size(72.dp), shape = CircleShape, color = StatusPendingBg) {
                    Icon(
                        Icons.Default.HourglassTop, null,
                        Modifier.padding(16.dp).fillMaxSize(), tint = StatusPending
                    )
                }

                PillTag(
                    color = StatusPendingBg,
                    padding = PaddingValues(horizontal = 12.dp, vertical = 6.dp)
                ) {
                    Icon(Icons.Default.AccessTime, null, Modifier.size(14.dp), tint = StatusPending)
                    Spacer(Modifier.width(6.dp))
                    Text(
                        "VERIFICACIÓN EN PROCESO", fontSize = 11.sp, fontWeight = FontWeight.Bold,
                        color = StatusPending, letterSpacing = 0.5.sp
                    )
                }

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

            // Pasos de la verificación
            AppCard(Modifier.fillMaxWidth(), padding = 16.dp, spacing = 14.dp) {
                Text(
                    "Estado de Verificación Paso a Paso", fontSize = 14.sp,
                    fontWeight = FontWeight.Bold, color = PrimaryLight
                )
                HorizontalDivider(color = SurfaceSoft)
                TimelineStepRow(
                    "1. Envío de formulario y adjuntos",
                    "Documentos cargados correctamente",
                    isCompleted = true
                )
                TimelineStepRow(
                    if (isDriver) "2. Validación RUNT, SOAT y Licencia" else "2. Validación de Identidad y Biometría",
                    "Consulta directa en bases de datos oficiales (~15 a 30 min)",
                    isInProgress = true
                )
                TimelineStepRow(
                    "3. Habilitación de perfil operativo",
                    "Aprobación final para activar tu cuenta"
                )
            }

            Column(Modifier.fillMaxWidth(), verticalArrangement = Arrangement.spacedBy(8.dp)) {
                PrimaryButton(
                    "Simular Aprobación y Ver Perfil", onNavigateToProfile,
                    leadingIcon = Icons.Default.VerifiedUser
                )
                SecondaryOutlinedButton(
                    if (isCheckingStatus) "Consultando entidades..." else "Consultar Estado de Verificación",
                    { isCheckingStatus = !isCheckingStatus },
                    leadingIcon = Icons.Default.Refresh,
                    height = 48.dp,
                    borderColor = OutlineVariant
                )
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
