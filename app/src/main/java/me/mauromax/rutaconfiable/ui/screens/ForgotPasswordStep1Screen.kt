package me.mauromax.rutaconfiable.ui.screens

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Email
import androidx.compose.material.icons.filled.Message
import androidx.compose.material.icons.filled.Restore
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import me.mauromax.rutaconfiable.ui.components.*
import me.mauromax.rutaconfiable.ui.theme.*
import me.mauromax.rutaconfiable.data.AuthRepository
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ForgotPasswordStep1Screen(
    onBackClick: () -> Unit = {},
    onSendCodeSuccess: () -> Unit = {}
) {
    var recoveryMethod by remember { mutableStateOf("SMS") }
    var phoneNumber by remember { mutableStateOf("") }
    var isLoading by remember { mutableStateOf(false) }
    var errorMessage by remember { mutableStateOf<String?>(null) }
    var successMessage by remember { mutableStateOf<String?>(null) }
    val scope = rememberCoroutineScope()

    Scaffold(
        topBar = {
            StandardAppTopBar(
                onBackClick = onBackClick,
                stepTagText = "PASO 1 DE 2",
                showHelpIcon = true
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

                    // Lock / Recovery Avatar Icon
                    Box {
                        Surface(
                            modifier = Modifier.size(52.dp),
                            shape = RoundedCornerShape(12.dp),
                            color = Color(0xFFE5EEFF)
                        ) {
                            Icon(
                                imageVector = Icons.Default.Restore,
                                contentDescription = null,
                                tint = PrimaryLight,
                                modifier = Modifier.padding(12.dp)
                            )
                        }
                        Surface(
                            modifier = Modifier
                                .size(14.dp)
                                .align(Alignment.TopEnd),
                            shape = CircleShape,
                            color = Secondary
                        ) {}
                    }

                    // Title Header
                    Text(
                        text = "Recuperar Contraseña",
                        fontSize = 24.sp,
                        fontWeight = FontWeight.Bold,
                        color = PrimaryLight,
                        fontFamily = FontFamily.Serif
                    )

                    Text(
                        text = "No te preocupes. Ingresa tu correo registrado o número de teléfono y te enviaremos un código de seguridad para restablecerla.",
                        fontSize = 13.sp,
                        color = Color(0xFF44474D),
                        lineHeight = 18.sp
                    )

                    // Verification Method Selector
                    Text(
                        text = "Selecciona el método de verificación",
                        fontSize = 12.sp,
                        fontWeight = FontWeight.SemiBold,
                        color = PrimaryLight
                    )

                    // Phone / Email Input
                    Column {
                        Text(
                            text = "Número de Celular Móvil o Correo electronico",
                            fontSize = 12.sp,
                            fontWeight = FontWeight.SemiBold,
                            color = PrimaryLight,
                            modifier = Modifier.padding(bottom = 4.dp)
                        )
                        PhoneWithCountrySelectorField(
                            phoneValue = phoneNumber,
                            onPhoneChange = { phoneNumber = it },
                            placeholderText = "310 123 4567"
                        )
                        Text(
                            text = "Te enviaremos un PIN temporal por SMS o mensaje verificado.",
                            fontSize = 11.sp,
                            color = Color(0xFF74777E),
                            modifier = Modifier.padding(top = 4.dp, start = 4.dp)
                        )
                    }

                    // Security Advice Box
                    SecurityAdviceBox()

                    if (errorMessage != null) {
                        Text(text = errorMessage!!, color = MaterialTheme.colorScheme.error, fontSize = 12.sp)
                    }
                    if (successMessage != null) {
                        Text(text = successMessage!!, color = Color(0xFF006837), fontSize = 12.sp)
                    }

                    // Action Buttons
                    PrimaryButton(
                        text = if (isLoading) "Enviando..." else "Enviar correo de recuperación",
                        onClick = {
                            if (phoneNumber.isBlank()) {
                                errorMessage = "Por favor ingresa tu correo electrónico"
                            } else {
                                errorMessage = null
                                successMessage = null
                                isLoading = true
                                scope.launch {
                                    AuthRepository.sendPasswordReset(phoneNumber.trim())
                                        .onSuccess {
                                            isLoading = false
                                            successMessage = "Correo de restablecimiento enviado con éxito."
                                            onSendCodeSuccess()
                                        }
                                        .onFailure { e ->
                                            isLoading = false
                                            errorMessage = AuthRepository.friendlyError(e)
                                        }
                                }
                            }
                        }
                    )

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
private fun RecoveryMethodOptionButton(
    text: String,
    icon: ImageVector,
    isSelected: Boolean,
    modifier: Modifier = Modifier,
    onClick: () -> Unit
) {
    Surface(
        modifier = modifier
            .padding(2.dp)
            .clickable { onClick() },
        shape = RoundedCornerShape(8.dp),
        color = if (isSelected) Color.White else Color.Transparent,
        shadowElevation = if (isSelected) 2.dp else 0.dp
    ) {
        Row(
            modifier = Modifier.padding(vertical = 10.dp),
            horizontalArrangement = Arrangement.Center,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Icon(
                imageVector = icon,
                contentDescription = null,
                tint = if (isSelected) PrimaryLight else Color(0xFF74777E),
                modifier = Modifier.size(18.dp)
            )
            Spacer(modifier = Modifier.width(6.dp))
            Text(
                text = text,
                fontSize = 12.sp,
                fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Medium,
                color = if (isSelected) PrimaryLight else Color(0xFF74777E)
            )
        }
    }
}

@Preview(showBackground = true)
@Composable
fun ForgotPasswordStep1ScreenPreview() {
    RutaConfiableTheme {
        ForgotPasswordStep1Screen()
    }
}
