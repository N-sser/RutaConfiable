package me.mauromax.rutaconfiable.ui.screens

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AccountBalanceWallet
import androidx.compose.material.icons.filled.AccountCircle
import androidx.compose.material.icons.filled.AltRoute
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Inventory2
import androidx.compose.material.icons.filled.PersonAdd
import androidx.compose.material.icons.filled.Shield
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import kotlinx.coroutines.launch
import me.mauromax.rutaconfiable.R
import me.mauromax.rutaconfiable.ui.components.*
import me.mauromax.rutaconfiable.ui.data.AuthRepository
import me.mauromax.rutaconfiable.ui.theme.*

private data class BenefitInfo(
    val title: String,
    val icon: ImageVector,
    val description: String,
    val points: List<String>
)

private val benefits = listOf(
    BenefitInfo(
        "Póliza y SOAT", Icons.Default.Shield,
        "Cada transportista debe tener sus documentos al día antes de recibir su primer viaje.",
        listOf(
            "Verificamos que el SOAT esté vigente y avisamos antes de su vencimiento.",
            "Revisamos licencia de conducción y datos del vehículo.",
            "Los conductores sin documentos vigentes no pueden aceptar viajes."
        )
    ),
    BenefitInfo(
        "Carga Segura", Icons.Default.Inventory2,
        "Sigue tu mercancía de principio a fin, con información clara en cada etapa.",
        listOf(
            "Seguimiento GPS del viaje en tiempo real.",
            "Inspección de la carga antes de salir, con registro en la hoja de ruta.",
            "Manifiesto digital con peso, origen y destino acordados."
        )
    ),
    BenefitInfo(
        "Pago Protegido", Icons.Default.AccountBalanceWallet,
        "Las tarifas se pactan antes del viaje, así nadie paga ni cobra de más.",
        listOf(
            "Tarifa acordada y visible para ambas partes desde el inicio.",
            "El pago se libera cuando el servicio se completa.",
            "Comprobante de cada transacción dentro de la app."
        )
    )
)

@Composable
fun WelcomeScreen(
    onNavigateToLogin: () -> Unit = {},
    onNavigateToRegister: () -> Unit = {},
    /** [verified] = false: la cuenta existe pero un administrador aún no la aprueba. */
    onGoogleSignedIn: (role: RegistrationRole, verified: Boolean) -> Unit = { _, _ -> }
) {
    var selectedBenefit by remember { mutableStateOf<BenefitInfo?>(null) }

    val context = LocalContext.current
    val scope = rememberCoroutineScope()
    var isGoogleLoading by remember { mutableStateOf(false) }
    var googleError by remember { mutableStateOf<String?>(null) }
    var askRole by remember { mutableStateOf(false) }

    // 1) Google entrega la cuenta, 2) Firebase inicia sesión, 3) si es una cuenta nueva
    // se pregunta si es cliente o conductor; si ya existía se usa el rol que tenía guardado.
    fun signInWithGoogle() {
        if (isGoogleLoading) return
        googleError = null
        isGoogleLoading = true
        scope.launch {
            try {
                val idToken = GoogleSignIn.getIdToken(context).getOrElse { e ->
                    googleError = GoogleSignIn.friendlyError(e)
                    return@launch
                }
                AuthRepository.signInWithGoogle(idToken).onFailure { e ->
                    googleError = AuthRepository.friendlyError(e)
                    return@launch
                }
                val exists = AuthRepository.hasProfile().getOrElse {
                    AuthRepository.logout()
                    googleError = "No se pudo leer tu perfil. Revisa tu conexión e intenta de nuevo."
                    return@launch
                }
                if (!exists) {
                    askRole = true
                    return@launch
                }
                val status = AuthRepository.getAccountStatus()
                when (status?.role) {
                    "client" -> onGoogleSignedIn(RegistrationRole.CLIENT, status.isVerified)
                    "driver" -> onGoogleSignedIn(RegistrationRole.DRIVER, status.isVerified)
                    else -> {
                        AuthRepository.logout()
                        googleError = "No se pudo leer tu perfil. Revisa tu conexión e intenta de nuevo."
                    }
                }
            } finally {
                isGoogleLoading = false
            }
        }
    }

    fun chooseRole(role: RegistrationRole) {
        askRole = false
        isGoogleLoading = true
        scope.launch {
            AuthRepository.createGoogleProfile(
                if (role == RegistrationRole.CLIENT) "client" else "driver"
            ).onSuccess {
                isGoogleLoading = false
                onGoogleSignedIn(role, false)
            }.onFailure { e ->
                // Sin perfil no hay cuenta usable: se cierra la sesión para poder reintentar.
                AuthRepository.logout()
                isGoogleLoading = false
                googleError = AuthRepository.friendlyError(e)
            }
        }
    }

    if (askRole) {
        AlertDialog(
            onDismissRequest = {
                askRole = false
                AuthRepository.logout()
            },
            title = {
                Text("¿Cómo vas a usar Ruta Confiable?", fontWeight = FontWeight.Bold, color = PrimaryLight)
            },
            text = {
                Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                    RoleOption(
                        "Soy cliente", "Envío carga o reservo cupos en viajes.",
                        onClick = { chooseRole(RegistrationRole.CLIENT) }
                    )
                    RoleOption(
                        "Soy conductor", "Ofrezco fletes y cupos en mis rutas.",
                        onClick = { chooseRole(RegistrationRole.DRIVER) }
                    )
                }
            },
            confirmButton = {},
            dismissButton = {
                TextButton(onClick = {
                    askRole = false
                    AuthRepository.logout()
                }) { Text("Cancelar") }
            }
        )
    }

    selectedBenefit?.let {
        BenefitInfoSheet(it, onDismiss = { selectedBenefit = null })
    }

    Scaffold(topBar = { WelcomeAppTopBar() }, containerColor = Background) { innerPadding ->
        ScreenColumn(innerPadding) {
            // Imagen principal con insignia de GPS
            Box(
                Modifier
                    .fillMaxWidth()
                    .height(200.dp)
                    .clip(RoundedCornerShape(16.dp))
                    .background(PrimaryDark)
            ) {
                Image(
                    painter = painterResource(id = R.drawable.ic_launcher_background),
                    contentDescription = null,
                    modifier = Modifier.fillMaxSize(),
                    contentScale = ContentScale.Crop
                )

                // Dark overlay tint for visual depth
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .background(Color(0x22000000))
                )
                Box(Modifier.fillMaxSize().background(Color(0x22000000))) // sombra suave

                PillTag(Modifier.padding(12.dp).align(Alignment.TopStart), color = CardBackground) {
                    Text("🟠", fontSize = 10.sp)
                    Spacer(Modifier.width(4.dp))
                    Text("Monitoreo GPS 24/7 en Ruta", fontSize = 11.sp, fontWeight = FontWeight.Bold, color = PrimaryLight)
                }
            }

            PillTag(shape = RoundedCornerShape(6.dp)) {
                Icon(Icons.Default.AltRoute, null, Modifier.size(14.dp), tint = PrimaryLight)
                Spacer(Modifier.width(6.dp))
                Text(
                    "TRANSPORTE INTERMUNICIPAL Y FLETES", fontSize = 11.sp,
                    fontWeight = FontWeight.Bold, color = PrimaryLight, letterSpacing = 0.5.sp
                )
            }

            ScreenTitle("Tu ruta segura para fletes y viajes compartidos", 26.sp, lineHeight = 32.sp)
            BodyText(
                "Conectamos cargadores y pasajeros con transportistas auditados. Disfruta de verificación estricta de antecedentes y SOAT, trazabilidad en vivo y tarifas justas pactadas."
            )

            Row(Modifier.fillMaxWidth(), Arrangement.spacedBy(8.dp)) {
                benefits.forEach { b ->
                    BenefitCard(b.title, b.icon, Modifier.weight(1f), onClick = { selectedBenefit = b })
                }
            }

            PrimaryButton("Iniciar Sesión", onNavigateToLogin)
            SecondaryOutlinedButton("Crear Cuenta Gratis", onNavigateToRegister, leadingIcon = Icons.Default.PersonAdd)
            SecondaryOutlinedButton(
                if (isGoogleLoading) "Conectando con Google..." else "Continuar con Google",
                { signInWithGoogle() },
                leadingIcon = Icons.Default.AccountCircle,
                borderColor = OutlineVariant
            )
            googleError?.let {
                Text(
                    it, Modifier.fillMaxWidth(), fontSize = 12.sp,
                    color = StatusBooked, textAlign = TextAlign.Center
                )
            }

            Text(
                "Al continuar, aceptas nuestros Términos de Servicio y Política de Privacidad.",
                Modifier.fillMaxWidth().padding(horizontal = 16.dp, vertical = 4.dp),
                fontSize = 11.sp, color = Outline, textAlign = TextAlign.Center
            )
        }
    }
}

@Composable
private fun RoleOption(title: String, subtitle: String, onClick: () -> Unit) {
    Surface(
        Modifier.fillMaxWidth().clickable { onClick() },
        shape = RoundedCornerShape(10.dp),
        color = SurfaceSoft
    ) {
        Column(Modifier.padding(14.dp)) {
            Text(title, fontSize = 15.sp, fontWeight = FontWeight.Bold, color = PrimaryLight)
            Text(subtitle, fontSize = 12.sp, color = OnSurfaceVariant)
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun BenefitInfoSheet(info: BenefitInfo, onDismiss: () -> Unit) {
    ModalBottomSheet(onDismissRequest = onDismiss, containerColor = Background) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 20.dp)
                .padding(bottom = 24.dp),
            verticalArrangement = Arrangement.spacedBy(14.dp)
        ) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Icon(info.icon, null, tint = Secondary, modifier = Modifier.size(28.dp))
                Spacer(Modifier.width(10.dp))
                Text(
                    text = info.title,
                    fontSize = 22.sp,
                    fontWeight = FontWeight.Bold,
                    color = PrimaryLight,
                    fontFamily = FontFamily.Serif
                )
            }
            Text(info.description, fontSize = 14.sp, color = Color(0xFF44474D), lineHeight = 20.sp)
            info.points.forEach { point ->
                Row(verticalAlignment = Alignment.Top) {
                    Icon(
                        Icons.Default.CheckCircle, null,
                        tint = StatusAvailable,
                        modifier = Modifier.size(18.dp)
                    )
                    Spacer(Modifier.width(8.dp))
                    Text(point, fontSize = 13.sp, color = PrimaryLight, lineHeight = 18.sp)
                }
            }
            PrimaryButton(text = "Entendido", onClick = onDismiss, trailingIcon = null)
        }
    }
}

@Preview(showBackground = true)
@Composable
fun WelcomeScreenPreview() {
    RutaConfiableTheme { WelcomeScreen() }
}