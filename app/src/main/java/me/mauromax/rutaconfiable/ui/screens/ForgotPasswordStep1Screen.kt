package me.mauromax.rutaconfiable.ui.screens

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Restore
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import me.mauromax.rutaconfiable.ui.components.*
import me.mauromax.rutaconfiable.ui.theme.*

@Composable
fun ForgotPasswordStep1Screen(
    onBackClick: () -> Unit = {},
    onSendCodeSuccess: () -> Unit = {}
) {
    var phoneNumber by remember { mutableStateOf("") }

    Scaffold(
        topBar = {
            StandardAppTopBar(onBackClick = onBackClick, stepTagText = "PASO 1 DE 2", showHelpIcon = true)
        },
        containerColor = Background
    ) { innerPadding ->
        ScreenColumn(innerPadding) {
            AppCard(Modifier.fillMaxWidth(), padding = 16.dp, spacing = 14.dp) {
                // Icono con puntito naranja
                Box {
                    Surface(Modifier.size(52.dp), shape = RoundedCornerShape(12.dp), color = SurfaceMuted) {
                        Icon(Icons.Default.Restore, null, Modifier.padding(12.dp), tint = PrimaryLight)
                    }
                    Surface(
                        Modifier.size(14.dp).align(Alignment.TopEnd),
                        shape = CircleShape, color = Secondary
                    ) {}
                }

                ScreenTitle("Recuperar Contraseña")
                BodyText(
                    "No te preocupes. Ingresa tu correo registrado o número de teléfono y te enviaremos un código de seguridad para restablecerla."
                )
                FieldLabel("Selecciona el método de verificación")

                Column {
                    FieldLabel("Número de Celular Móvil o Correo electronico", Modifier.padding(bottom = 4.dp))
                    PhoneWithCountrySelectorField(
                        phoneNumber, { phoneNumber = it },
                        placeholderText = "310 123 4567"
                    )
                    Hint(
                        "Te enviaremos un PIN temporal por SMS o mensaje verificado.",
                        Modifier.padding(top = 4.dp, start = 4.dp)
                    )
                }

                SecurityAdviceBox()

                PrimaryButton("Enviar código de recuperación", onSendCodeSuccess)
                SecondaryOutlinedButton(
                    "Volver a Iniciar Sesión", onBackClick,
                    leadingIcon = Icons.AutoMirrored.Filled.ArrowBack
                )
            }

            TlsEncryptionBadge(Modifier.align(Alignment.CenterHorizontally))
        }
    }
}

@Preview(showBackground = true)
@Composable
fun ForgotPasswordStep1ScreenPreview() {
    RutaConfiableTheme { ForgotPasswordStep1Screen() }
}
