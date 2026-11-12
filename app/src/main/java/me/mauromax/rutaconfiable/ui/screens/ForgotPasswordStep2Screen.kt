package me.mauromax.rutaconfiable.ui.screens

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.AccessTime
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Shield
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import me.mauromax.rutaconfiable.ui.components.*
import me.mauromax.rutaconfiable.ui.theme.*

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
            StandardAppTopBar(onBackClick = onBackClick, stepTagText = "2/2", showSecurityBadge = false)
        },
        containerColor = Background
    ) { innerPadding ->
        ScreenColumn(innerPadding) {
            StepProgressBarCard() // paso 2 de 2 (valores por defecto)

            AppCard(Modifier.fillMaxWidth(), padding = 16.dp, spacing = 14.dp) {
                PillTag {
                    Icon(Icons.Default.Shield, null, Modifier.size(14.dp), tint = Secondary)
                    Spacer(Modifier.width(4.dp))
                    Text("Acceso Seguro de Transporte", fontSize = 11.sp, fontWeight = FontWeight.Bold, color = PrimaryLight)
                }

                ScreenTitle("Verificación y Nueva Clave")
                BodyText("Ingresa el código de 6 dígitos que enviamos a tu correo/teléfono y define tu nueva contraseña segura.")

                // Código de 6 dígitos
                Column {
                    Row(Modifier.fillMaxWidth(), Arrangement.SpaceBetween, Alignment.CenterVertically) {
                        FieldLabel("Código de Verificación")
                        Hint("6 dígitos")
                    }
                    Spacer(Modifier.height(6.dp))
                    SixDigitCodeInput(otpCode, { otpCode = it })
                    Spacer(Modifier.height(6.dp))
                    Row(Modifier.fillMaxWidth(), Arrangement.SpaceBetween, Alignment.CenterVertically) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(Icons.Default.AccessTime, null, Modifier.size(14.dp), tint = Outline)
                            Spacer(Modifier.width(4.dp))
                            Hint("Reenviar código en ")
                            Text("00:45", fontSize = 11.sp, fontWeight = FontWeight.Bold, color = Secondary)
                        }
                        Text(
                            "Reenviar ahora", fontSize = 11.sp, fontWeight = FontWeight.Bold,
                            color = Secondary, modifier = Modifier.clickable { }
                        )
                    }
                }

                PasswordTextField(newPassword, { newPassword = it }, "Nueva Contraseña", "••••••••••••")

                Column {
                    Row(Modifier.fillMaxWidth(), Arrangement.SpaceBetween, Alignment.CenterVertically) {
                        FieldLabel("Confirmar Contraseña")
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(Icons.Default.CheckCircle, null, Modifier.size(14.dp), tint = StatusAvailable)
                            Spacer(Modifier.width(4.dp))
                            Text("Coinciden", fontSize = 11.sp, fontWeight = FontWeight.Bold, color = StatusAvailable)
                        }
                    }
                    Spacer(Modifier.height(4.dp))
                    PasswordTextField(confirmPassword, { confirmPassword = it }, "", "••••••••••••")
                }

                // Requisitos
                Surface(Modifier.fillMaxWidth(), color = SurfaceSoft, shape = RoundedCornerShape(8.dp)) {
                    Column(Modifier.padding(12.dp), verticalArrangement = Arrangement.spacedBy(6.dp)) {
                        Text(
                            "REQUISITOS DE SEGURIDAD", fontSize = 11.sp, fontWeight = FontWeight.Bold,
                            color = PrimaryLight, letterSpacing = 0.5.sp
                        )
                        listOf(
                            "Mínimo 8 caracteres",
                            "Al menos un número o símbolo (!@#$)",
                            "Al menos una letra mayúscula"
                        ).forEach { RequirementItem(it) }
                    }
                }

                PrimaryButton("Restablecer contraseña y acceder", onResetPasswordSuccess)
                SecondaryOutlinedButton(
                    "Volver a Iniciar Sesión", onBackClick,
                    leadingIcon = Icons.AutoMirrored.Filled.ArrowBack
                )
            }

            TlsEncryptionBadge(Modifier.align(Alignment.CenterHorizontally))
        }
    }
}

@Composable
private fun RequirementItem(text: String) {
    Row(verticalAlignment = Alignment.CenterVertically) {
        Icon(Icons.Default.CheckCircle, null, Modifier.size(14.dp), tint = StatusAvailable)
        Spacer(Modifier.width(6.dp))
        Text(text, fontSize = 11.sp, color = OnSurfaceVariant)
    }
}

@Preview(showBackground = true)
@Composable
fun ForgotPasswordStep2ScreenPreview() {
    RutaConfiableTheme { ForgotPasswordStep2Screen() }
}
