package me.mauromax.rutaconfiable.ui.components

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Surface
import androidx.compose.material3.Switch
import androidx.compose.material3.SwitchDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Shape
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.TextUnit
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import me.mauromax.rutaconfiable.ui.theme.*

// Piezas pequeñas que se repetían en casi todas las pantallas.


/** Tarjeta blanca con borde. */
@Composable
fun AppCard(
    modifier: Modifier = Modifier,
    radius: Dp = 12.dp,
    padding: Dp = 14.dp,
    spacing: Dp = 0.dp,
    alignment: Alignment.Horizontal = Alignment.Start,
    content: @Composable ColumnScope.() -> Unit
) {
    Surface(
        modifier = modifier,
        color = CardBackground,
        shape = RoundedCornerShape(radius),
        border = BorderStroke(1.dp, OutlineVariant),
        shadowElevation = 1.dp
    ) {
        Column(
            modifier = Modifier.padding(padding),
            verticalArrangement = Arrangement.spacedBy(spacing),
            horizontalAlignment = alignment,
            content = content
        )
    }
}

/** Etiqueta redonda (el contenido lo pones tú: icono, texto...). */
@Composable
fun PillTag(
    modifier: Modifier = Modifier,
    color: Color = SurfaceMuted,
    shape: Shape = RoundedCornerShape(16.dp),
    bordered: Boolean = false,
    padding: PaddingValues = PaddingValues(horizontal = 10.dp, vertical = 4.dp),
    content: @Composable RowScope.() -> Unit
) {
    Surface(
        modifier = modifier,
        shape = shape,
        color = color,
        border = if (bordered) BorderStroke(1.dp, OutlineVariant) else null
    ) {
        Row(Modifier.padding(padding), verticalAlignment = Alignment.CenterVertically, content = content)
    }
}

/** Columna con scroll que usan las pantallas de formulario. */
@Composable
fun ScreenColumn(
    padding: PaddingValues,
    horizontalAlignment: Alignment.Horizontal = Alignment.Start,
    content: @Composable ColumnScope.() -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(padding)
            .verticalScroll(rememberScrollState())
            .padding(horizontal = 16.dp, vertical = 12.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp),
        horizontalAlignment = horizontalAlignment,
        content = content
    )
}

@Composable
fun ScreenTitle(
    text: String,
    size: TextUnit = 24.sp,
    modifier: Modifier = Modifier,
    textAlign: TextAlign? = null,
    lineHeight: TextUnit = TextUnit.Unspecified
) = Text(
    text, modifier, fontSize = size, fontWeight = FontWeight.Bold, fontFamily = FontFamily.Serif,
    color = PrimaryLight, textAlign = textAlign, lineHeight = lineHeight
)

@Composable
fun BodyText(text: String, modifier: Modifier = Modifier, textAlign: TextAlign? = null) =
    Text(text, modifier, fontSize = 13.sp, color = OnSurfaceVariant, lineHeight = 18.sp, textAlign = textAlign)

@Composable
fun FieldLabel(text: String, modifier: Modifier = Modifier) =
    Text(text, modifier, fontSize = 12.sp, fontWeight = FontWeight.SemiBold, color = PrimaryLight)

@Composable
fun Hint(text: String, modifier: Modifier = Modifier) =
    Text(text, modifier, fontSize = 11.sp, color = Outline)

/** Interruptor con los colores de la app. */
@Composable
fun AppSwitch(checked: Boolean, onCheckedChange: (Boolean) -> Unit) = Switch(
    checked = checked,
    onCheckedChange = onCheckedChange,
    colors = SwitchDefaults.colors(
        checkedThumbColor = CardBackground, checkedTrackColor = SecondaryContainer,
        uncheckedThumbColor = CardBackground, uncheckedTrackColor = Outline
    )
)