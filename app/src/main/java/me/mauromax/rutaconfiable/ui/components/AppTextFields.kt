package me.mauromax.rutaconfiable.ui.components

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowDropDown
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.Phone
import androidx.compose.material.icons.filled.Visibility
import androidx.compose.material.icons.filled.VisibilityOff
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import me.mauromax.rutaconfiable.ui.theme.*

@Composable
fun customTextFieldColors() = OutlinedTextFieldDefaults.colors(
    focusedBorderColor = PrimaryLight,
    unfocusedBorderColor = OutlineVariant,
    focusedLabelColor = PrimaryLight,
    unfocusedLabelColor = Outline,
    focusedLeadingIconColor = PrimaryLight,
    unfocusedLeadingIconColor = Outline
)

@Composable
fun AppOutlinedTextField(
    value: String,
    onValueChange: (String) -> Unit,
    label: String? = null,
    placeholder: String = "",
    leadingIcon: ImageVector? = null,
    trailingIcon: @Composable (() -> Unit)? = null,
    modifier: Modifier = Modifier,
    singleLine: Boolean = true,
    visualTransformation: VisualTransformation = VisualTransformation.None
) {
    OutlinedTextField(
        value = value,
        onValueChange = onValueChange,
        label = if (label != null) { { Text(label) } } else null,
        placeholder = { Text(placeholder) },
        leadingIcon = if (leadingIcon != null) { { Icon(leadingIcon, null) } } else null,
        trailingIcon = trailingIcon,
        visualTransformation = visualTransformation,
        modifier = modifier.fillMaxWidth(),
        shape = RoundedCornerShape(8.dp),
        colors = customTextFieldColors(),
        singleLine = singleLine
    )
}

@Composable
fun PasswordTextField(
    value: String,
    onValueChange: (String) -> Unit,
    label: String = "Contraseña",
    placeholder: String = "Mínimo 8 caracteres",
    modifier: Modifier = Modifier
) {
    var visible by remember { mutableStateOf(false) }

    AppOutlinedTextField(
        value, onValueChange, label, placeholder, Icons.Default.Lock,
        trailingIcon = {
            IconButton(onClick = { visible = !visible }) {
                Icon(
                    if (visible) Icons.Default.VisibilityOff else Icons.Default.Visibility,
                    if (visible) "Ocultar" else "Mostrar"
                )
            }
        },
        modifier = modifier,
        visualTransformation = if (visible) VisualTransformation.None else PasswordVisualTransformation()
    )
}

@Composable
fun PhoneWithCountrySelectorField(
    phoneValue: String,
    onPhoneChange: (String) -> Unit,
    modifier: Modifier = Modifier,
    placeholderText: String = "Ej. 310 987 6543 o usuario"
) {
    Row(
        modifier = modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.spacedBy(8.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Surface(
            modifier = Modifier.height(56.dp),
            shape = RoundedCornerShape(8.dp),
            color = SurfaceMuted,
            border = BorderStroke(1.dp, OutlineVariant)
        ) {
            Row(Modifier.padding(horizontal = 10.dp), verticalAlignment = Alignment.CenterVertically) {
                Text("🇨🇴 +57", fontSize = 13.sp, fontWeight = FontWeight.Bold, color = PrimaryLight)
                Spacer(Modifier.width(2.dp))
                Icon(Icons.Default.ArrowDropDown, null, tint = PrimaryLight)
            }
        }

        OutlinedTextField(
            value = phoneValue,
            onValueChange = onPhoneChange,
            placeholder = { Text(placeholderText, fontSize = 13.sp) },
            leadingIcon = { Icon(Icons.Default.Phone, null) },
            modifier = Modifier.weight(1f),
            shape = RoundedCornerShape(8.dp),
            colors = customTextFieldColors(),
            singleLine = true
        )
    }
}
