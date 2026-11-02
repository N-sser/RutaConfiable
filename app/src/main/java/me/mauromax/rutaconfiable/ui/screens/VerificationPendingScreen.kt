package me.mauromax.rutaconfiable.ui.screens

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.automirrored.filled.ArrowForward
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import me.mauromax.rutaconfiable.ui.theme.*

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun VerificationPendingScreen(
    role: RegistrationRole = RegistrationRole.DRIVER,
    onNavigateToProfile: () -> Unit = {},
    onResetRegistration: () -> Unit = {}
) {
    var isCheckingStatus by remember { mutableStateOf(false) }

    Scaffold(
        topBar = {
            Surface(
                color = MaterialTheme.colorScheme.surface,
                tonalElevation = 1.dp,
                modifier = Modifier.fillMaxWidth()
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .statusBarsPadding()
                        .padding(horizontal = 12.dp, vertical = 8.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        IconButton(onClick = onResetRegistration) {
                            Icon(
                                imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                                contentDescription = "Volver",
                                tint = MaterialTheme.colorScheme.onSurface
                            )
                        }
                        Icon(
                            imageVector = Icons.Default.LocalShipping,
                            contentDescription = null,
                            tint = SecondaryContainer,
                            modifier = Modifier.size(24.dp)
                        )
                        Spacer(modifier = Modifier.width(6.dp))
                        Text(
                            text = "Ruta Confiable",
                            fontSize = 18.sp,
                            fontWeight = FontWeight.Bold,
                            fontFamily = FontFamily.Serif,
                            color = PrimaryLight
                        )
                    }

                    Surface(
                        color = Color(0xFFE5EEFF),
                        shape = RoundedCornerShape(16.dp),
                        border = BorderStroke(1.dp, Color(0xFFC4C6CE))
                    ) {
                        Text(
                            text = "Paso 2 de 2",
                            modifier = Modifier.padding(horizontal = 10.dp, vertical = 4.dp),
                            fontSize = 11.sp,
                            fontWeight = FontWeight.Bold,
                            color = PrimaryLight
                        )
                    }
                }
            }
        },
        containerColor = Background
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .verticalScroll(rememberScrollState())
                .padding(horizontal = 16.dp, vertical = 16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {

            // Main Status Card
            Surface(
                modifier = Modifier.fillMaxWidth(),
                color = Color.White,
                shape = RoundedCornerShape(16.dp),
                border = BorderStroke(1.dp, Color(0xFFC4C6CE)),
                shadowElevation = 2.dp
            ) {
                Column(
                    modifier = Modifier.padding(20.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    // Highlighted Pending Icon
                    Surface(
                        modifier = Modifier.size(72.dp),
                        shape = CircleShape,
                        color = StatusPendingBg
                    ) {
                        Icon(
                            imageVector = Icons.Default.HourglassTop,
                            contentDescription = null,
                            tint = StatusPending,
                            modifier = Modifier
                                .padding(16.dp)
                                .fillMaxSize()
                        )
                    }

                    Spacer(modifier = Modifier.height(14.dp))

                    Surface(
                        color = StatusPendingBg,
                        shape = RoundedCornerShape(16.dp)
                    ) {
                        Row(
                            modifier = Modifier.padding(horizontal = 12.dp, vertical = 6.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Icon(
                                imageVector = Icons.Default.AccessTime,
                                contentDescription = null,
                                tint = StatusPending,
                                modifier = Modifier.size(14.dp)
                            )
                            Spacer(modifier = Modifier.width(6.dp))
                            Text(
                                text = "VERIFICACIÓN EN PROCESO",
                                fontSize = 11.sp,
                                fontWeight = FontWeight.Bold,
                                color = StatusPending,
                                letterSpacing = 0.5.sp
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(12.dp))

                    Text(
                        text = "Documentos en Revisión Legal",
                        fontSize = 22.sp,
                        fontWeight = FontWeight.Bold,
                        fontFamily = FontFamily.Serif,
                        color = PrimaryLight,
                        textAlign = TextAlign.Center
                    )

                    Spacer(modifier = Modifier.height(8.dp))

                    Text(
                        text = if (role == RegistrationRole.DRIVER) {
                            "Estamos cruzando tu documentación (Licencia, SOAT, RUNT y Selfie biométrica) con las entidades de control vehicular y seguridad logística nacional."
                        } else {
                            "Estamos validando tus datos de identidad, antecedentes y selfie biométrica con la Registraduría y sistemas de seguridad vial."
                        },
                        fontSize = 13.sp,
                        color = Color(0xFF44474D),
                        textAlign = TextAlign.Center,
                        lineHeight = 18.sp
                    )
                }
            }

            // Timeline Steps Card
            Surface(
                modifier = Modifier.fillMaxWidth(),
                color = Color.White,
                shape = RoundedCornerShape(12.dp),
                border = BorderStroke(1.dp, Color(0xFFC4C6CE)),
                shadowElevation = 1.dp
            ) {
                Column(
                    modifier = Modifier.padding(16.dp),
                    verticalArrangement = Arrangement.spacedBy(14.dp)
                ) {
                    Text(
                        text = "Estado de Verificación Paso a Paso",
                        fontSize = 14.sp,
                        fontWeight = FontWeight.Bold,
                        color = PrimaryLight
                    )

                    HorizontalDivider(color = Color(0xFFEFF4FF), thickness = 1.dp)

                    // Step 1 Completed
                    TimelineStepRow(
                        title = "1. Envío de formulario y adjuntos",
                        subtitle = "Documentos cargados correctamente",
                        isCompleted = true,
                        isInProgress = false
                    )

                    // Step 2 In Progress
                    TimelineStepRow(
                        title = if (role == RegistrationRole.DRIVER) "2. Validación RUNT, SOAT y Licencia" else "2. Validación de Identidad y Biometría",
                        subtitle = "Consulta directa en bases de datos oficiales (~15 a 30 min)",
                        isCompleted = false,
                        isInProgress = true
                    )

                    // Step 3 Pending
                    TimelineStepRow(
                        title = "3. Habilitación de perfil operativo",
                        subtitle = "Aprobación final para activar tu cuenta",
                        isCompleted = false,
                        isInProgress = false
                    )
                }
            }

            // Primary Actions: Simulate Approval & Refresh Status
            Column(
                modifier = Modifier.fillMaxWidth(),
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                Button(
                    onClick = onNavigateToProfile,
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(52.dp),
                    shape = RoundedCornerShape(8.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = PrimaryLight)
                ) {
                    Icon(
                        imageVector = Icons.Default.VerifiedUser,
                        contentDescription = null,
                        modifier = Modifier.size(18.dp)
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = "Simular Aprobación y Ver Perfil",
                        fontSize = 15.sp,
                        fontWeight = FontWeight.Bold
                    )
                    Spacer(modifier = Modifier.width(6.dp))
                    Icon(
                        imageVector = Icons.AutoMirrored.Filled.ArrowForward,
                        contentDescription = null,
                        modifier = Modifier.size(16.dp)
                    )
                }

                OutlinedButton(
                    onClick = { isCheckingStatus = !isCheckingStatus },
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(48.dp),
                    shape = RoundedCornerShape(8.dp),
                    border = BorderStroke(1.dp, Color(0xFFC4C6CE))
                ) {
                    Icon(
                        imageVector = Icons.Default.Refresh,
                        contentDescription = null,
                        tint = PrimaryLight,
                        modifier = Modifier.size(16.dp)
                    )
                    Spacer(modifier = Modifier.width(6.dp))
                    Text(
                        text = if (isCheckingStatus) "Consultando entidades..." else "Consultar Estado de Verificación",
                        fontSize = 13.sp,
                        fontWeight = FontWeight.Bold,
                        color = PrimaryLight
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
    isCompleted: Boolean,
    isInProgress: Boolean
) {
    Row(
        verticalAlignment = Alignment.CenterVertically,
        modifier = Modifier.fillMaxWidth()
    ) {
        Surface(
            modifier = Modifier.size(28.dp),
            shape = CircleShape,
            color = when {
                isCompleted -> StatusAvailableBg
                isInProgress -> StatusPendingBg
                else -> Color(0xFFE5EEFF)
            }
        ) {
            Icon(
                imageVector = when {
                    isCompleted -> Icons.Default.Check
                    isInProgress -> Icons.Default.Refresh
                    else -> Icons.Default.Lock
                },
                contentDescription = null,
                tint = when {
                    isCompleted -> StatusAvailable
                    isInProgress -> StatusPending
                    else -> Color(0xFF74777E)
                },
                modifier = Modifier.padding(6.dp)
            )
        }

        Spacer(modifier = Modifier.width(12.dp))

        Column {
            Text(
                text = title,
                fontSize = 13.sp,
                fontWeight = FontWeight.Bold,
                color = if (isCompleted || isInProgress) PrimaryLight else Color(0xFF74777E)
            )
            Text(
                text = subtitle,
                fontSize = 11.sp,
                color = Color(0xFF74777E)
            )
        }
    }
}

@Preview(showBackground = true)
@Composable
fun VerificationPendingScreenPreview() {
    RutaConfiableTheme {
        VerificationPendingScreen()
    }
}
