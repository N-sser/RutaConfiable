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
import kotlinx.coroutines.launch
import me.mauromax.rutaconfiable.ui.components.*
import me.mauromax.rutaconfiable.ui.components.data.AuthRepository
import me.mauromax.rutaconfiable.ui.theme.*

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
    var errorMessage by remember { mutableStateOf<String?>(null) }
    var isLoading by remember { mutableStateOf(false) }
    val scope = rememberCoroutineScope()

    fun submit() {
        val email = identifier.trim()
        if (email.isBlank() || password.isBlank()) {
            errorMessage = "Ingresa tu correo y contraseña."
            return
        }
        if ("@" !in email) {
            errorMessage = "Por ahora debes iniciar sesión con tu correo electrónico."
            return
        }
        val selectedRole = if (isClient) RegistrationRole.CLIENT else RegistrationRole.DRIVER
        val selectedKey = if (isClient) "client" else "driver"

        errorMessage = null
        isLoading = true
        scope.launch {
            AuthRepository.login(email, password)
                .onSuccess {
                    val actualRole = AuthRepository.getRole()
                    isLoading = false
                    if (actualRole == selectedKey) {
                        onLoginSuccess(selectedRole)
                    } else {
                        AuthRepository.logout()
                        errorMessage = if (actualRole == "client") {
                            "Esta cuenta es de Cliente. Cambia el selector de rol."
                        } else {
                            "Esta cuenta es de Conductor. Cambia el selector de rol."
                        }
                    }
                }
                .onFailure { e ->
                    isLoading = false
                    errorMessage = AuthRepository.friendlyError(e)
                }
        }
    }

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
                        identifier, { identifier = it; errorMessage = null },
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
                        password, { password = it; errorMessage = null },
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

                errorMessage?.let { message ->
                    Surface(Modifier.fillMaxWidth(), color = ErrorContainer, shape = RoundedCornerShape(8.dp)) {
                        Text(
                            message, Modifier.padding(12.dp),
                            fontSize = 13.sp, fontWeight = FontWeight.SemiBold, color = OnErrorContainer
                        )
                    }
                }

                PrimaryButton(
                    text = if (isLoading) "Ingresando..." else "Ingresar",
                    onClick = { submit() },
                    enabled = !isLoading
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