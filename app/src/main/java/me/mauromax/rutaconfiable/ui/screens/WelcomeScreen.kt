package me.mauromax.rutaconfiable.ui.screens

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AccountBalanceWallet
import androidx.compose.material.icons.filled.AltRoute
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Inventory2
import androidx.compose.material.icons.filled.PersonAdd
import androidx.compose.material.icons.filled.Shield
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
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
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import me.mauromax.rutaconfiable.R
import me.mauromax.rutaconfiable.ui.components.*
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

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun WelcomeScreen(
    onNavigateToLogin: () -> Unit = {},
    onNavigateToRegister: () -> Unit = {}
) {
    var selectedBenefit by remember { mutableStateOf<BenefitInfo?>(null) }

    selectedBenefit?.let {
        BenefitInfoSheet(it, onDismiss = { selectedBenefit = null })
    }

    Scaffold(
        topBar = {
            WelcomeAppTopBar()
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

            // Hero Image Card Container with GPS Badge
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(200.dp)
                    .clip(RoundedCornerShape(16.dp))
                    .background(Color(0xFFCBD5E1))
            ) {
                // Background Image
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

                // Top Left Live GPS Badge
                Surface(
                    modifier = Modifier
                        .padding(12.dp)
                        .align(Alignment.TopStart),
                    color = Color.White,
                    shape = RoundedCornerShape(16.dp)
                ) {
                    Row(
                        modifier = Modifier.padding(horizontal = 10.dp, vertical = 4.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text("🟠", fontSize = 10.sp)
                        Spacer(modifier = Modifier.width(4.dp))
                        Text(
                            text = "Monitoreo GPS 24/7 en Ruta",
                            fontSize = 11.sp,
                            fontWeight = FontWeight.Bold,
                            color = PrimaryLight
                        )
                    }
                }
            }

            // Sub-tag
            Surface(
                color = Color(0xFFE5EEFF),
                shape = RoundedCornerShape(6.dp)
            ) {
                Row(
                    modifier = Modifier.padding(horizontal = 10.dp, vertical = 4.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Icon(
                        imageVector = Icons.Default.AltRoute,
                        contentDescription = null,
                        tint = PrimaryLight,
                        modifier = Modifier.size(14.dp)
                    )
                    Spacer(modifier = Modifier.width(6.dp))
                    Text(
                        text = "TRANSPORTE INTERMUNICIPAL Y FLETES",
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Bold,
                        color = PrimaryLight,
                        letterSpacing = 0.5.sp
                    )
                }
            }

            // Headline
            Text(
                text = "Tu ruta segura para fletes y viajes compartidos",
                fontSize = 26.sp,
                fontWeight = FontWeight.Bold,
                color = PrimaryLight,
                fontFamily = FontFamily.Serif,
                lineHeight = 32.sp
            )

            // Description
            Text(
                text = "Conectamos cargadores y pasajeros con transportistas auditados. Disfruta de verificación estricta de antecedentes y SOAT, trazabilidad en vivo y tarifas justas pactadas.",
                fontSize = 13.sp,
                color = Color(0xFF44474D),
                lineHeight = 18.sp
            )

            // 3 Benefit Cards
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                benefits.forEach { b ->
                    BenefitCard(
                        title = b.title,
                        icon = b.icon,
                        modifier = Modifier.weight(1f),
                        onClick = { selectedBenefit = b }
                    )
                }
            }

            Spacer(modifier = Modifier.height(4.dp))

            // Primary Action Buttons
            PrimaryButton(
                text = "Iniciar Sesión",
                onClick = onNavigateToLogin
            )

            SecondaryOutlinedButton(
                text = "Crear Cuenta Gratis",
                onClick = onNavigateToRegister,
                leadingIcon = Icons.Default.PersonAdd
            )

            // Terms Footer
            Text(
                text = "Al continuar, aceptas nuestros Términos de Servicio y Política de Privacidad.",
                fontSize = 11.sp,
                color = Color(0xFF74777E),
                textAlign = TextAlign.Center,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp, vertical = 4.dp)
            )
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
    RutaConfiableTheme {
        WelcomeScreen()
    }
}