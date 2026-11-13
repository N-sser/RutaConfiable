package me.mauromax.rutaconfiable.ui.components

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.HelpOutline
import androidx.compose.material.icons.filled.Language
import androidx.compose.material.icons.filled.LocalShipping
import androidx.compose.material3.*
import android.content.Intent
import android.net.Uri
import androidx.compose.runtime.*
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.TextUnit
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import me.mauromax.rutaconfiable.ui.theme.*

private const val SUPPORT_EMAIL = "soporte@rutaconfiable.com"

@Composable
private fun HelpDialog(onDismiss: () -> Unit) {
    val context = LocalContext.current
    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("Ayuda", fontWeight = FontWeight.Bold, color = PrimaryLight) },
        text = {
            Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                Text(
                    "Verificación: un administrador revisa tu cuenta antes de activarla. " +
                            "La pantalla de verificación avanza sola cuando te aprueban.",
                    fontSize = 13.sp
                )
                Text(
                    "Reservas: elige un viaje, pulsa \"Reservar cupo o solicitar flete\" " +
                            "e indica cuántos cupos o kilos necesitas.",
                    fontSize = 13.sp
                )
                Text(
                    "Contraseña: si no puedes entrar, usa \"¿Olvidaste tu contraseña?\" en el inicio de sesión.",
                    fontSize = 13.sp
                )
                Text("¿Algo más? Escríbenos a $SUPPORT_EMAIL", fontSize = 13.sp, fontWeight = FontWeight.SemiBold)
            }
        },
        confirmButton = {
            TextButton(onClick = {
                runCatching {
                    context.startActivity(
                        Intent(Intent.ACTION_SENDTO, Uri.parse("mailto:$SUPPORT_EMAIL"))
                            .putExtra(Intent.EXTRA_SUBJECT, "Ayuda con Ruta Confiable")
                    )
                }
                onDismiss()
            }) { Text("Escribir a soporte") }
        },
        dismissButton = { TextButton(onClick = onDismiss) { Text("Cerrar") } }
    )
}

@Composable
private fun TopBarRow(horizontal: Dp, vertical: Dp, content: @Composable RowScope.() -> Unit) {
    Surface(
        color = MaterialTheme.colorScheme.surface,
        tonalElevation = 1.dp,
        modifier = Modifier.fillMaxWidth()
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .statusBarsPadding()
                .padding(horizontal = horizontal, vertical = vertical),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween,
            content = content
        )
    }
}

@Composable
private fun Logo(size: Dp, textSize: TextUnit) {
    Surface(Modifier.size(size), shape = RoundedCornerShape(8.dp), color = PrimaryLight) {
        Icon(Icons.Default.LocalShipping, null, Modifier.padding(size / 6), tint = Color.White)
    }
    Spacer(Modifier.width(size / 4))
    Text(
        "Ruta Confiable", fontSize = textSize, fontWeight = FontWeight.Bold,
        fontFamily = FontFamily.Serif, color = PrimaryLight
    )
}

@Composable
fun WelcomeAppTopBar() {
    TopBarRow(horizontal = 16.dp, vertical = 10.dp) {
        Row(verticalAlignment = Alignment.CenterVertically) { Logo(36.dp, 20.sp) }
        PillTag(
            color = SurfaceMuted, bordered = true,
            padding = PaddingValues(horizontal = 12.dp, vertical = 6.dp)
        ) {
            Icon(Icons.Default.Language, null, Modifier.size(14.dp), tint = PrimaryLight)
            Spacer(Modifier.width(6.dp))
            Text("ES", fontSize = 12.sp, fontWeight = FontWeight.Bold, color = PrimaryLight)
        }
    }
}

@Composable
fun StandardAppTopBar(
    onBackClick: (() -> Unit)? = null,
    stepTagText: String? = null,
    showHelpIcon: Boolean = false,
    showSecurityBadge: Boolean = true
) {
    TopBarRow(horizontal = 12.dp, vertical = 8.dp) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            if (onBackClick != null) {
                IconButton(onClick = onBackClick) {
                    Icon(
                        Icons.AutoMirrored.Filled.ArrowBack, "Volver",
                        tint = MaterialTheme.colorScheme.onSurface
                    )
                }
            }
            Logo(32.dp, 18.sp)
        }

        Row(verticalAlignment = Alignment.CenterVertically) {
            if (stepTagText != null) {
                PillTag(color = SurfaceMuted, bordered = true) {
                    Text(stepTagText, fontSize = 11.sp, fontWeight = FontWeight.Bold, color = PrimaryLight)
                }
            } else if (showSecurityBadge) {
                SecurityBadge()
            }

            if (showHelpIcon) {
                var showHelp by remember { mutableStateOf(false) }
                if (showHelp) HelpDialog { showHelp = false }
                Spacer(Modifier.width(6.dp))
                IconButton(onClick = { showHelp = true }) {
                    Icon(Icons.Default.HelpOutline, "Ayuda", tint = PrimaryLight)
                }
            }
        }
    }
}