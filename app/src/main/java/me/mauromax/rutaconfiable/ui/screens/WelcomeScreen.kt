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

@Composable
fun WelcomeScreen(
    onNavigateToLogin: () -> Unit = {},
    onNavigateToRegister: () -> Unit = {}
) {
    var selectedBenefit by remember { mutableStateOf<BenefitInfo?>(null) }

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
                    .background(Color(0xFFCBD5E1))
            ) {
                Image(
                    painterResource(R.drawable.ic_launcher_background), null,
                    Modifier.fillMaxSize(), contentScale = ContentScale.Crop
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

            Text(
                "Al continuar, aceptas nuestros Términos de Servicio y Política de Privacidad.",
                Modifier.fillMaxWidth().padding(horizontal = 16.dp, vertical = 4.dp),
                fontSize = 11.sp, color = Outline, textAlign = TextAlign.Center
            )
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun BenefitInfoSheet(info: BenefitInfo, onDismiss: () -> Unit) {
    ModalBottomSheet(onDismissRequest = onDismiss, containerColor = Background) {
        Column(
            modifier = Modifier.fillMaxWidth().padding(horizontal = 20.dp).padding(bottom = 24.dp),
            verticalArrangement = Arrangement.spacedBy(14.dp)
        ) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Icon(info.icon, null, Modifier.size(28.dp), tint = Secondary)
                Spacer(Modifier.width(10.dp))
                ScreenTitle(info.title, 22.sp)
            }
            Text(info.description, fontSize = 14.sp, color = OnSurfaceVariant, lineHeight = 20.sp)
            info.points.forEach { point ->
                Row(verticalAlignment = Alignment.Top) {
                    Icon(Icons.Default.CheckCircle, null, Modifier.size(18.dp), tint = StatusAvailable)
                    Spacer(Modifier.width(8.dp))
                    Text(point, fontSize = 13.sp, color = PrimaryLight, lineHeight = 18.sp)
                }
            }
            PrimaryButton("Entendido", onDismiss, trailingIcon = null)
        }
    }
}

@Preview(showBackground = true)
@Composable
fun WelcomeScreenPreview() {
    RutaConfiableTheme { WelcomeScreen() }
}
