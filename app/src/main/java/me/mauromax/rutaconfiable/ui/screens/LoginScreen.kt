
package me.mauromax.rutaconfiable.ui.screens

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.LocalShipping
import androidx.compose.material.icons.filled.Person
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import kotlinx.coroutines.launch
import me.mauromax.rutaconfiable.data.AuthRepository
import me.mauromax.rutaconfiable.ui.components.*
import me.mauromax.rutaconfiable.ui.theme.*

@Composable
fun LoginScreen(
    onBackClick: () -> Unit = {},
    onLoginSuccess: (RegistrationRole) -> Unit = {},
    onForgotPasswordClick: () -> Unit = {},
    onRegisterClick: (RegistrationRole) -> Unit = {}
) {
    var selectedRole by remember {
        mutableStateOf(RegistrationRole.DRIVER)
    }

    var identifier by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }
    var rememberDevice by remember { mutableStateOf(true) }

    var errorMessage by remember {
        mutableStateOf<String?>(null)
    }

    var isLoading by remember {
        mutableStateOf(false)
    }

    val scope = rememberCoroutineScope()

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
                        isSelected = selectedRole == RegistrationRole.DRIVER,
                        modifier = Modifier.weight(1f),
                        onClick = {
                            selectedRole = RegistrationRole.DRIVER
                        }
                    )

                    RoleOptionButton(
                        text = "Cliente / Pasajero",
                        icon = Icons.Default.Person,
                        isSelected = selectedRole == RegistrationRole.CLIENT,
                        modifier = Modifier.weight(1f),
                        onClick = {
                            selectedRole = RegistrationRole.CLIENT
                        }
                    )
                }
            }

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

                    Column {
                        Text(
                            text = "Correo electrónico",
                            fontSize = 12.sp,
                            fontWeight = FontWeight.SemiBold,
                            color = PrimaryLight,
                            modifier = Modifier.padding(bottom = 4.dp)
                        )

                        PhoneWithCountrySelectorField(
                            phoneValue = identifier,
                            onPhoneChange = {
                                identifier = it
                                errorMessage = null
                            },
                            placeholderText = "usuario@ejemplo.com"
                        )
                    }

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
                                modifier = Modifier.clickable {
                                    onForgotPasswordClick()
                                }
                            )
                        }

                        Spacer(modifier = Modifier.height(4.dp))

                        PasswordTextField(
                            value = password,
                            onValueChange = {
                                password = it
                                errorMessage = null
                            },
                            label = "Tu clave de acceso",
                            placeholder = "Ingresa tu contraseña"
                        )
                    }

                    if (errorMessage != null) {
                        Text(
                            text = errorMessage!!,
                            color = MaterialTheme.colorScheme.error,
                            fontSize = 12.sp
                        )
                    }

                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Checkbox(
                            checked = rememberDevice,
                            onCheckedChange = {
                                rememberDevice = it
                            },
                            colors = CheckboxDefaults.colors(
                                checkedColor = PrimaryLight
                            )
                        )

                        Text(
                            text = "Recordar mis datos en este dispositivo",
                            fontSize = 12.sp,
                            color = Color(0xFF44474D)
                        )
                    }

                    PrimaryButton(
                        text = if (isLoading) {
                            "Ingresando..."
                        } else {
                            "Ingresar"
                        },
                        onClick = {
                            if (isLoading) return@PrimaryButton

                            if (identifier.isBlank() || password.isBlank()) {
                                errorMessage =
                                    "Por favor completa el correo y la contraseña"
                            } else {
                                errorMessage = null
                                isLoading = true

                                scope.launch {
                                    AuthRepository.login(
                                        identifier.trim(),
                                        password
                                    ).onSuccess {

                                        val roleStr =
                                            AuthRepository.getRole()

                                        val roleEnum =
                                            if (roleStr == "client") {
                                                RegistrationRole.CLIENT
                                            } else {
                                                RegistrationRole.DRIVER
                                            }

                                        isLoading = false
                                        onLoginSuccess(roleEnum)

                                    }.onFailure { error ->

                                        isLoading = false

                                        errorMessage =
                                            AuthRepository.friendlyError(error)
                                    }
                                }
                            }
                        }
                    )
                }
            }

            Surface(
                modifier = Modifier
                    .fillMaxWidth()
                    .clickable {
                        onRegisterClick(selectedRole)
                    },
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

            TlsEncryptionBadge(
                modifier = Modifier.align(Alignment.CenterHorizontally)
            )
        }
    }
}
