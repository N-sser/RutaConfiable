package me.mauromax.rutaconfiable.ui.screens

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.LocalShipping
import androidx.compose.material.icons.filled.Person
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

// Credenciales de demostración (reemplazar por validación contra el backend)
private val DEMO_USERS = listOf("3109876543", "usuario@ejemplo.com", "admin", "driver", "client")
private const val DEMO_PASSWORD = "123"

@Composable
fun LoginScreen(
    onBackClick: () -> Unit = {},
    onLoginSuccess: (role: RegistrationRole) -> Unit = {},
    onForgotPasswordClick: () -> Unit = {},
    onRegisterClick: (role: RegistrationRole) -> Unit = {}
) {
    var isClient by remember { mutableStateOf(false) }
    var identifier by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }
    var rememberDevice by remember { mutableStateOf(true) }
    var loginError by remember { mutableStateOf(false) }

    Scaffold(
        topBar = {
            StandardAppTopBar(onBackClick = onBackClick, showHelpIcon = true, showSecurityBadge = false)
        },
        containerColor = Background
    ) { innerPadding ->
        ScreenColumn(innerPadding) {
            ScreenTitle("Iniciar Sesión", 26.sp)
            BodyText("Ingresa a tu cuenta para gestionar tus viajes, fletes y rutas")

            SegmentedSelector(
                listOf(
                    "Conductor / Aliado" to Icons.Default.LocalShipping,
                    "Cliente / Pasajero" to Icons.Default.Person
                ),
                selectedIndex = if (isClient) 1 else 0
            ) { isClient = it == 1 }

            AppCard(Modifier.fillMaxWidth(), padding = 16.dp, spacing = 14.dp) {
                Column {
                    FieldLabel("Correo electrónico o celular", Modifier.padding(bottom = 4.dp))
                    PhoneWithCountrySelectorField(
                        identifier, { identifier = it; loginError = false },
                        placeholderText = "Ej. 310 987 6543 o usuario@ejemplo.com"
                    )
                }

                Column {
                    Row(Modifier.fillMaxWidth(), Arrangement.SpaceBetween, Alignment.CenterVertically) {
                        FieldLabel("Contraseña segura")
                        Text(
                            "¿Olvidaste tu contraseña?", fontSize = 12.sp, fontWeight = FontWeight.Bold,
                            color = Secondary, modifier = Modifier.clickable { onForgotPasswordClick() }
                        )
                    }
                    Spacer(Modifier.height(4.dp))
                    PasswordTextField(
                        password, { password = it; loginError = false },
                        label = "Tu clave de acceso", placeholder = "Ingresa tu contraseña"
                    )
                }

                Row(verticalAlignment = Alignment.CenterVertically) {
                    Checkbox(
                        rememberDevice, { rememberDevice = it },
                        colors = CheckboxDefaults.colors(checkedColor = PrimaryLight)
                    )
                    Text("Recordar mis datos en este dispositivo", fontSize = 12.sp, color = OnSurfaceVariant)
                }

                if (loginError) {
                    Surface(Modifier.fillMaxWidth(), color = ErrorContainer, shape = RoundedCornerShape(8.dp)) {
                        Text(
                            "Usuario o contraseña incorrecta", Modifier.padding(12.dp),
                            fontSize = 13.sp, fontWeight = FontWeight.SemiBold, color = OnErrorContainer
                        )
                    }
                }

                PrimaryButton(
                    text = "Ingresar",
                    onClick = {
                        val user = identifier.filter { !it.isWhitespace() }.lowercase()
                        if (user in DEMO_USERS && password == DEMO_PASSWORD) {
                            onLoginSuccess(if (isClient) RegistrationRole.CLIENT else RegistrationRole.DRIVER)
                        } else {
                            loginError = true
                        }
                    }
                )
            }

            Surface(
                Modifier.fillMaxWidth().clickable {
                    onRegisterClick(if (isClient) RegistrationRole.CLIENT else RegistrationRole.DRIVER)
                },
                color = SurfaceSoft,
                shape = RoundedCornerShape(8.dp),
                border = BorderStroke(1.dp, OutlineVariant)
            ) {
                Row(Modifier.padding(14.dp), Arrangement.Center, Alignment.CenterVertically) {
                    Text("¿No tienes una cuenta todavía? ", fontSize = 13.sp, color = OnSurfaceVariant)
                    Text("Regístrate aquí", fontSize = 13.sp, fontWeight = FontWeight.Bold, color = PrimaryLight)
                }
            }

            TlsEncryptionBadge(Modifier.align(Alignment.CenterHorizontally))
        }
    }
}

@Preview(showBackground = true)
@Composable
fun LoginScreenPreview() {
    RutaConfiableTheme { LoginScreen() }
}
