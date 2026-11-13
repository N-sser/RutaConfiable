package me.mauromax.rutaconfiable.ui.components

import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.Shield
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import me.mauromax.rutaconfiable.ui.theme.PrimaryLight
import me.mauromax.rutaconfiable.ui.theme.Secondary
import me.mauromax.rutaconfiable.ui.theme.SurfaceSoft

@Composable
fun SecurityBadge(modifier: Modifier = Modifier, text: String = "Seguridad Vigente") {
    PillTag(modifier, color = SurfaceSoft, bordered = true) {
        Icon(Icons.Default.Shield, null, Modifier.size(14.dp), tint = Secondary)
        Spacer(Modifier.width(4.dp))
        Text(text, fontSize = 11.sp, fontWeight = FontWeight.Bold, color = PrimaryLight)
    }
}

@Composable
fun TlsEncryptionBadge(modifier: Modifier = Modifier, text: String = "CIFRADO TLS 256-BIT") {
    PillTag(
        modifier, color = SurfaceSoft, bordered = true,
        padding = PaddingValues(horizontal = 12.dp, vertical = 6.dp)
    ) {
        Icon(Icons.Default.Lock, null, Modifier.size(12.dp), tint = PrimaryLight)
        Spacer(Modifier.width(6.dp))
        Text(text, fontSize = 11.sp, fontWeight = FontWeight.Bold, color = PrimaryLight, letterSpacing = 0.5.sp)
    }
}

@Composable
fun StatusTag(text: String, bgColor: Color, textColor: Color, icon: ImageVector? = null) {
    PillTag(
        color = bgColor, shape = RoundedCornerShape(6.dp),
        padding = PaddingValues(horizontal = 8.dp, vertical = 4.dp)
    ) {
        if (icon != null) {
            Icon(icon, null, Modifier.size(12.dp), tint = textColor)
            Spacer(Modifier.width(4.dp))
        }
        Text(text, fontSize = 11.sp, fontWeight = FontWeight.Bold, color = textColor)
    }
}
