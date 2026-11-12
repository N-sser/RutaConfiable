package me.mauromax.rutaconfiable.ui.screens

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.AccessTime
import androidx.compose.material.icons.filled.MarkEmailRead
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import me.mauromax.rutaconfiable.ui.components.*
import me.mauromax.rutaconfiable.ui.components.data.AuthRepository
import me.mauromax.rutaconfiable.ui.theme.*

private const val RESEND_COOLDOWN_SECONDS = 60

@Composable
fun ForgotPasswordStep2Screen(
    email: String = "",
    onBackClick: () -> Unit = {},
    onBackToLogin: () -> Unit = {}
) {
    var secondsLeft by remember { mutableIntStateOf(RESEND_COOLDOWN_SECONDS) }
    var isSending by remember { mutableStateOf(false) }
    var infoMessage by remember { mutableStateOf<String?>(null) }
    var errorMessage by remember { mutableStateOf<String?>(null) }
    val scope = rememberCoroutineScope()

    LaunchedEffect(secondsLeft) {
        if (secondsLeft > 0) {
            delay(1000)
            secondsLeft--
        }
    }

    fun resend() {
        if (secondsLeft > 0 || isSending || email.isBlank()) return
        isSending = true
        infoMessage = null
        errorMessage = null
        scope.launch {
            AuthRepository.sendPasswordReset(email)
                .onSuccess {
                    isSending = false
                    secondsLeft = RESEND_COOLDOWN_SECONDS
                    infoMessage = "Te enviamos el enlace de nuevo."
                }
                .onFailure { e ->
                    isSending = false
                    errorMessage = AuthRepository.friendlyError(e)
                }
        }
    }

    Scaffold(
        topBar = {
            StandardAppTopBar(onBackClick = onBackClick, stepTagText = "2/2", showSecurityBadge = false)
        },
        containerColor = Background
    ) { innerPadding ->
        ScreenColumn(innerPadding) {
            StepProgressBarCard(stepTitle = "Revisa tu correo")

            AppCard(Modifier.fillMaxWidth(), padding = 16.dp, spacing = 14.dp) {
                PillTag {
                    Icon(Icons.Default.MarkEmailRead, null, Modifier.size(14.dp), tint = Secondary)
                    Spacer(Modifier.width(4.dp))
                    Text("Enlace enviado", fontSize = 11.sp, fontWeight = FontWeight.Bold, color = PrimaryLight)
                }

                ScreenTitle("Revisa tu correo")
                BodyText(
                    if (email.isBlank()) {
                        "Si el correo está registrado, te enviamos un enlace para crear tu nueva contraseña."
                    } else {
                        "Si $email está registrado, te enviamos un enlace para crear tu nueva contraseña. Ábrelo, define tu clave y vuelve aquí para iniciar sesión."
                    }
                )

                SecurityAdviceBox(
                    message = "Si no lo ves, revisa la carpeta de spam o correo no deseado. El enlace tiene un tiempo limitado de validez."
                )

                Row(Modifier.fillMaxWidth(), Arrangement.SpaceBetween, Alignment.CenterVertically) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(Icons.Default.AccessTime, null, Modifier.size(14.dp), tint = Outline)
                        Spacer(Modifier.width(4.dp))
                        if (secondsLeft > 0) {
                            Hint("Reenviar enlace en ")
                            Text(
                                "00:%02d".format(secondsLeft), fontSize = 11.sp,
                                fontWeight = FontWeight.Bold, color = Secondary
                            )
                        } else {
                            Hint("¿No te llegó?")
                        }
                    }
                    if (secondsLeft == 0) {
                        Text(
                            if (isSending) "Enviando..." else "Reenviar ahora",
                            fontSize = 11.sp, fontWeight = FontWeight.Bold, color = Secondary,
                            modifier = Modifier.clickable(enabled = !isSending) { resend() }
                        )
                    }
                }

                infoMessage?.let { message ->
                    Text(message, fontSize = 12.sp, fontWeight = FontWeight.SemiBold, color = StatusAvailable)
                }
                errorMessage?.let { message ->
                    Surface(Modifier.fillMaxWidth(), color = ErrorContainer, shape = RoundedCornerShape(8.dp)) {
                        Text(
                            message, Modifier.padding(12.dp),
                            fontSize = 13.sp, fontWeight = FontWeight.SemiBold, color = OnErrorContainer
                        )
                    }
                }

                PrimaryButton("Volver a Iniciar Sesión", onBackToLogin)
                SecondaryOutlinedButton(
                    "Usar otro correo", onBackClick,
                    leadingIcon = Icons.AutoMirrored.Filled.ArrowBack
                )
            }

            TlsEncryptionBadge(Modifier.align(Alignment.CenterHorizontally))
        }
    }
}

@Preview(showBackground = true)
@Composable
fun ForgotPasswordStep2ScreenPreview() {
    RutaConfiableTheme { ForgotPasswordStep2Screen(email = "usuario@ejemplo.com") }
}