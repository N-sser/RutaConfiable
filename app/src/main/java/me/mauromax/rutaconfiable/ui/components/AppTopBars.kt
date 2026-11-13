package me.mauromax.rutaconfiable.ui.components

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.RowScope
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.HelpOutline
import androidx.compose.material.icons.filled.Language
import androidx.compose.material.icons.filled.LocalShipping
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.TextUnit
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import me.mauromax.rutaconfiable.ui.theme.PrimaryLight
import me.mauromax.rutaconfiable.ui.theme.SurfaceMuted

/** Barra superior: fondo + fila con el contenido a izquierda y derecha. */
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

/** Icono del camión + nombre de la app. */
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
                Spacer(Modifier.width(6.dp))
                IconButton(onClick = { }) {
                    Icon(Icons.Default.HelpOutline, "Ayuda", tint = PrimaryLight)
                }
            }
        }
    }
}
