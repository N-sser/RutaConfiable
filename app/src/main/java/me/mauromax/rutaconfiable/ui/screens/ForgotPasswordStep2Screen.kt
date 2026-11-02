package me.mauromax.rutaconfiable.ui.screens

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.AccessTime
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material.icons.filled.Shield
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

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ForgotPasswordStep2Screen(
    onBackClick: () -> Unit = {},
    onResetPasswordSuccess: () -> Unit = {}
) {
    var otpCode by remember { mutableStateOf("482") }
    var newPassword by remember { mutableStateOf("") }
    var confirmPassword by remember { mutableStateOf("") }

    Scaffold(
        topBar = {
            StandardAppTopBar(
                onBackClick = onBackClick,
                stepTagText = "2/2",
                showSecurityBadge = false
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

            // Step Progress Bar Card
            StepProgressBarCard(
                currentStep = 2,
                totalSteps = 2,
                stepTitle = "Verificación final",
                progress = 1.0f
            )

            // Form Card
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

                    // Security Tag
                    Surface(
                        color = Color(0xFFE5EEFF),
                        shape = RoundedCornerShape(16.dp)
                    ) {
                        Row(
                            modifier = Modifier.padding(horizontal = 10.dp, vertical = 4.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Icon(
                                imageVector = Icons.Default.Shield,
                                contentDescription = null,
                                tint = Secondary,
                                modifier = Modifier.size(14.dp)
                            )
                            Spacer(modifier = Modifier.width(4.dp))
                            Text(
                                text = "Acceso Seguro de Transporte",
                                fontSize = 11.sp,
                                fontWeight = FontWeight.Bold,
                                color = PrimaryLight
                            )
                        }
                    }

                    // Title Header
                    Text(
                        text = "Verificación y Nueva Clave",
                        fontSize = 24.sp,
                        fontWeight = FontWeight.Bold,
                        color = PrimaryLight,
                        fontFamily = FontFamily.Serif
                    )

                    Text(
                        text = "Ingresa el código de 6 dígitos que enviamos a tu correo/teléfono y define tu nueva contraseña segura.",
                        fontSize = 13.sp,
                        color = Color(0xFF44474D),
                        lineHeight = 18.sp
                    )

                    // 6-Digit Code Input
                    Column {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text(
                                text = "Código de Verificación",
                                fontSize = 12.sp,
                                fontWeight = FontWeight.SemiBold,
                                color = PrimaryLight
                            )
                            Text(
                                text = "6 dígitos",
                                fontSize = 11.sp,
                                color = Color(0xFF74777E)
                            )
                        }

                        Spacer(modifier = Modifier.height(6.dp))

                        SixDigitCodeInput(
                            codeValue = otpCode,
                            onCodeChange = { otpCode = it }
                        )

                        Spacer(modifier = Modifier.height(6.dp))

                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Icon(
                                    imageVector = Icons.Default.AccessTime,
                                    contentDescription = null,
                                    tint = Color(0xFF74777E),
                                    modifier = Modifier.size(14.dp)
                                )
                                Spacer(modifier = Modifier.width(4.dp))
                                Text(
                                    text = "Reenviar código en ",
                                    fontSize = 11.sp,
                                    color = Color(0xFF74777E)
                                )
                                Text(
                                    text = "00:45",
                                    fontSize = 11.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = Secondary
                                )
                            }

                            Text(
                                text = "Reenviar ahora",
                                fontSize = 11.sp,
                                fontWeight = FontWeight.Bold,
                                color = Secondary,
                                modifier = Modifier.clickable { }
                            )
                        }
                    }

                    // New Password
                    PasswordTextField(
                        value = newPassword,
                        onValueChange = { newPassword = it },
                        label = "Nueva Contraseña",
                        placeholder = "••••••••••••"
                    )

                    // Confirm Password
                    Column {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text(
                                text = "Confirmar Contraseña",
                                fontSize = 12.sp,
                                fontWeight = FontWeight.SemiBold,
                                color = PrimaryLight
                            )
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Icon(
                                    imageVector = Icons.Default.CheckCircle,
                                    contentDescription = null,
                                    tint = StatusAvailable,
                                    modifier = Modifier.size(14.dp)
                                )
                                Spacer(modifier = Modifier.width(4.dp))
                                Text(
                                    text = "Coinciden",
                                    fontSize = 11.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = StatusAvailable
                                )
                            }
                        }
                        Spacer(modifier = Modifier.height(4.dp))
                        PasswordTextField(
                            value = confirmPassword,
                            onValueChange = { confirmPassword = it },
                            label = "",
                            placeholder = "••••••••••••"
                        )
                    }

                    // Security Requirements Box
                    Surface(
                        modifier = Modifier.fillMaxWidth(),
                        color = Color(0xFFEFF4FF),
                        shape = RoundedCornerShape(8.dp)
                    ) {
                        Column(
                            modifier = Modifier.padding(12.dp),
                            verticalArrangement = Arrangement.spacedBy(6.dp)
                        ) {
                            Text(
                                text = "REQUISITOS DE SEGURIDAD",
                                fontSize = 11.sp,
                                fontWeight = FontWeight.Bold,
                                color = PrimaryLight,
                                letterSpacing = 0.5.sp
                            )
                            RequirementItem("Mínimo 8 caracteres")
                            RequirementItem("Al menos un número o símbolo (!@#$)")
                            RequirementItem("Al menos una letra mayúscula")
                        }
                    }

                    // Primary Action Button
                    PrimaryButton(
                        text = "Restablecer contraseña y acceder",
                        onClick = onResetPasswordSuccess
                    )

                    // Secondary Action
                    SecondaryOutlinedButton(
                        text = "Volver a Iniciar Sesión",
                        onClick = onBackClick,
                        leadingIcon = Icons.AutoMirrored.Filled.ArrowBack
                    )
                }
            }

            Spacer(modifier = Modifier.height(4.dp))

            // Encryption Badge
            TlsEncryptionBadge(
                modifier = Modifier.align(Alignment.CenterHorizontally)
            )
        }
    }
}

@Composable
private fun RequirementItem(text: String) {
    Row(verticalAlignment = Alignment.CenterVertically) {
        Icon(
            imageVector = Icons.Default.CheckCircle,
            contentDescription = null,
            tint = StatusAvailable,
            modifier = Modifier.size(14.dp)
        )
        Spacer(modifier = Modifier.width(6.dp))
        Text(
            text = text,
            fontSize = 11.sp,
            color = Color(0xFF44474D)
        )
    }
}

@Preview(showBackground = true)
@Composable
fun ForgotPasswordStep2ScreenPreview() {
    RutaConfiableTheme {
        ForgotPasswordStep2Screen()
    }
}
