package me.mauromax.rutaconfiable.ui.screens

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowForward
import androidx.compose.material.icons.filled.LocalShipping
import androidx.compose.material.icons.filled.Person
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
import me.mauromax.rutaconfiable.ui.components.*
import me.mauromax.rutaconfiable.ui.theme.*
import kotlinx.coroutines.launch

// Credenciales de demostración (reemplazar por validación contra el backend)
private val DEMO_USERS = listOf("3109876543", "usuario@ejemplo.com", "admin", "driver", "client")
private val DEMO_PASSWORD = "123"

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun LoginScreen(
    onBackClick: () -> Unit = {},
    onLoginSuccess: (role: String) -> Unit = {},
    onForgotPasswordClick: () -> Unit = {},
    onRegisterClick: () -> Unit = {}
) {
    var selectedRole by remember { mutableStateOf("Conductor") }
    var identifier by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }
    var rememberDevice by remember { mutableStateOf(true) }
    var loginError by remember { mutableStateOf(false) }

    Scaffold(
        topBar = {
            StandardAppTopBar(
                onBackClick = onBackClick,
                showHelpIcon = true,
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

            // Title Header
            Text(
                text = "Iniciar Sesión",
                fontSize = 26.sp,
                fontWeight = FontWeight.Bold,
                color = PrimaryLight,
                fontFamily = FontFamily.Serif
            )

            Text(
                text = "Ingresa a tu cuenta para gestionar tus viajes, fletes y rutas",
                fontSize = 13.sp,
                color = Color(0xFF44474D),
                lineHeight = 18.sp
            )

            // Segmented Role Selector
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
                        text = "Conductor / Aliado",
                        icon = Icons.Default.LocalShipping,
                        isSelected = selectedRole == "Conductor",
                        modifier = Modifier.weight(1f),
                        onClick = { selectedRole = "Conductor" }
                    )
                    RoleOptionButton(
                        text = "Cliente / Pasajero",
                        icon = Icons.Default.Person,
                        isSelected = selectedRole == "Cliente",
                        modifier = Modifier.weight(1f),
                        onClick = { selectedRole = "Cliente" }
                    )
                }
            }

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
                    // Identifier Field (Phone / Email)
                    Column {
                        Text(
                            text = "Correo electrónico o celular",
                            fontSize = 12.sp,
                            fontWeight = FontWeight.SemiBold,
                            color = PrimaryLight,
                            modifier = Modifier.padding(bottom = 4.dp)
                        )
                        PhoneWithCountrySelectorField(
                            phoneValue = identifier,
                            onPhoneChange = { identifier = it; loginError = false },
                            placeholderText = "Ej. 310 987 6543 o usuario@ejemplo.com"
                        )
                    }

                    // Password Field
                    Column {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text(
                                text = "Contraseña segura",
                                fontSize = 12.sp,
                                fontWeight = FontWeight.SemiBold,
                                color = PrimaryLight
                            )
                            Text(
                                text = "¿Olvidaste tu contraseña?",
                                fontSize = 12.sp,
                                fontWeight = FontWeight.Bold,
                                color = Secondary,
                                modifier = Modifier.clickable { onForgotPasswordClick() }
                            )
                        }
                        Spacer(modifier = Modifier.height(4.dp))
                        PasswordTextField(
                            value = password,
                            onValueChange = { password = it; loginError = false },
                            label = "Tu clave de acceso",
                            placeholder = "Ingresa tu contraseña"
                        )
                    }

                    // Remember Device Checkbox
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Checkbox(
                            checked = rememberDevice,
                            onCheckedChange = { rememberDevice = it },
                            colors = CheckboxDefaults.colors(checkedColor = PrimaryLight)
                        )
                        Text(
                            text = "Recordar mis datos en este dispositivo",
                            fontSize = 12.sp,
                            color = Color(0xFF44474D)
                        )
                    }

                    // Submit Action Button
                    if (loginError) {
                        Surface(
                            modifier = Modifier.fillMaxWidth(),
                            color = ErrorContainer,
                            shape = RoundedCornerShape(8.dp)
                        ) {
                            Text(
                                text = "Usuario o contraseña incorrecta",
                                modifier = Modifier.padding(12.dp),
                                fontSize = 13.sp,
                                fontWeight = FontWeight.SemiBold,
                                color = OnErrorContainer
                            )
                        }
                    }

                    PrimaryButton(
                        text = "Ingresar",
                        onClick = {
                            val valid = identifier.filter { !it.isWhitespace() }.lowercase() in DEMO_USERS &&
                                    password == DEMO_PASSWORD
                            if (valid) {
                                onLoginSuccess(selectedRole)
                            } else {
                                loginError = true
                            }
                        }
                    )
                }
            }


            Surface(
                modifier = Modifier
                    .fillMaxWidth()
                    .clickable { onRegisterClick() },
                color = Color(0xFFEFF4FF),
                shape = RoundedCornerShape(8.dp),
                border = BorderStroke(1.dp, Color(0xFFC4C6CE))
            ) {
                Row(
                    modifier = Modifier.padding(14.dp),
                    horizontalArrangement = Arrangement.Center,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "¿No tienes una cuenta todavía? ",
                        fontSize = 13.sp,
                        color = Color(0xFF44474D)
                    )
                    Text(
                        text = "Regístrate aquí",
                        fontSize = 13.sp,
                        fontWeight = FontWeight.Bold,
                        color = PrimaryLight
                    )
                }
            }

            Spacer(modifier = Modifier.height(8.dp))

            // Encryption Badge
            TlsEncryptionBadge(
                modifier = Modifier.align(Alignment.CenterHorizontally)
            )
        }
    }
}

@Preview(showBackground = true)
@Composable
fun LoginScreenPreview() {
    RutaConfiableTheme {
        LoginScreen()
    }
}
