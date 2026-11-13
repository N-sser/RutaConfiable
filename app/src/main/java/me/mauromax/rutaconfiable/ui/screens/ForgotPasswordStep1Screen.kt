package me.mauromax.rutaconfiable.ui.screens

import android.util.Patterns
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Email
import androidx.compose.material.icons.filled.Restore
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import kotlinx.coroutines.launch
import me.mauromax.rutaconfiable.ui.components.*
import me.mauromax.rutaconfiable.ui.data.AuthRepository
import me.mauromax.rutaconfiable.ui.theme.*

@Composable
fun ForgotPasswordStep1Screen(
    onBackClick: () -> Unit = {},
    onLinkSent: (email: String) -> Unit = {}
) {
    var email by remember { mutableStateOf("") }
    var errorMessage by remember { mutableStateOf<String?>(null) }
    var isLoading by remember { mutableStateOf(false) }
    val scope = rememberCoroutineScope()

    fun submit() {
        val cleanEmail = email.trim()
        if (!Patterns.EMAIL_ADDRESS.matcher(cleanEmail).matches()) {
            errorMessage = "Ingresa un correo electrónico válido."
            return
        }
        errorMessage = null
        isLoading = true
        scope.launch {
            AuthRepository.sendPasswordReset(cleanEmail)
                .onSuccess {
                    isLoading = false
                    onLinkSent(cleanEmail)
                }
                .onFailure { e ->
                    isLoading = false
                    errorMessage = AuthRepository.friendlyError(e)
                }
        }
    }

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
                    "No te preocupes. Ingresa el correo con el que te registraste y te enviaremos un enlace para crear una nueva contraseña."
                )

                Column {
                    FieldLabel("Correo electrónico", Modifier.padding(bottom = 4.dp))
                    OutlinedTextField(
                        value = email,
                        onValueChange = { email = it; errorMessage = null },
                        placeholder = { Text("usuario@ejemplo.com", fontSize = 13.sp) },
                        leadingIcon = { Icon(Icons.Default.Email, null) },
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(8.dp),
                        colors = customTextFieldColors(),
                        singleLine = true,
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Email)
                    )
                    Hint(
                        "Recibirás un enlace por correo electrónico.",
                        Modifier.padding(top = 4.dp, start = 4.dp)
                    )
                }

                errorMessage?.let { message ->
                    Surface(Modifier.fillMaxWidth(), color = ErrorContainer, shape = RoundedCornerShape(8.dp)) {
                        Text(
                            message, Modifier.padding(12.dp),
                            fontSize = 13.sp, fontWeight = FontWeight.SemiBold, color = OnErrorContainer
                        )
                    }
                }

                SecurityAdviceBox(
                    message = "Revisa tu bandeja de entrada y la carpeta de spam. El enlace tiene un tiempo limitado de validez."
                )

                PrimaryButton(
                    text = if (isLoading) "Enviando..." else "Enviar enlace de recuperación",
                    onClick = { submit() },
                    enabled = !isLoading
                )
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