package me.mauromax.rutaconfiable.ui.screens

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.imePadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.AccountBalanceWallet
import androidx.compose.material.icons.filled.CalendarToday
import androidx.compose.material.icons.filled.CreditCard
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Store
import androidx.compose.material.icons.filled.Visibility
import androidx.compose.material.icons.filled.VisibilityOff
import androidx.compose.material3.Checkbox
import androidx.compose.material3.CheckboxDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import me.mauromax.rutaconfiable.ui.components.BodyText
import me.mauromax.rutaconfiable.ui.components.PrimaryButton
import me.mauromax.rutaconfiable.ui.components.ScreenTitle
import me.mauromax.rutaconfiable.ui.components.SecurityAdviceBox
import me.mauromax.rutaconfiable.ui.components.SegmentedSelector
import me.mauromax.rutaconfiable.ui.components.StandardAppTopBar
import me.mauromax.rutaconfiable.ui.components.customTextFieldColors
import me.mauromax.rutaconfiable.ui.theme.Background
import me.mauromax.rutaconfiable.ui.theme.OnSurfaceVariant
import me.mauromax.rutaconfiable.ui.theme.Outline
import me.mauromax.rutaconfiable.ui.theme.OutlineVariant
import me.mauromax.rutaconfiable.ui.theme.PrimaryLight
import me.mauromax.rutaconfiable.ui.theme.RutaConfiableTheme
import me.mauromax.rutaconfiable.ui.theme.Secondary
import me.mauromax.rutaconfiable.ui.theme.SurfaceSoft

@Composable
fun PaymentMethodsScreen(
    onBackClick: () -> Unit = {},
    onFinish: () -> Unit = onBackClick
) {
    var tab by remember { mutableIntStateOf(0) }
    var holder by remember { mutableStateOf("") }
    var number by remember { mutableStateOf("") }
    var expiry by remember { mutableStateOf("") }
    var cvv by remember { mutableStateOf("") }
    var showCvv by remember { mutableStateOf(false) }
    var isDefault by remember { mutableStateOf(true) }
    var saveCard by remember { mutableStateOf(true) }

    val valid = holder.isNotBlank() && number.length == 16 && expiry.length == 4 && cvv.length >= 3

    Scaffold(
        topBar = { StandardAppTopBar(showSecurityBadge = true) },
        containerColor = Background
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .imePadding()
                .verticalScroll(rememberScrollState())
                .padding(horizontal = 16.dp, vertical = 12.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            Row(Modifier.fillMaxWidth(), Arrangement.SpaceBetween, Alignment.CenterVertically) {
                Row(Modifier.clickable { onBackClick() }, verticalAlignment = Alignment.CenterVertically) {
                    Icon(Icons.AutoMirrored.Filled.ArrowBack, null, Modifier.size(16.dp), tint = Secondary)
                    Spacer(Modifier.width(4.dp))
                    Text("Volver a Registro", fontSize = 12.sp, fontWeight = FontWeight.Bold, color = Secondary)
                }
                Text(
                    "Omitir por ahora", fontSize = 12.sp, fontWeight = FontWeight.Bold,
                    color = Secondary, modifier = Modifier.clickable { onFinish() }
                )
            }

            Row(verticalAlignment = Alignment.CenterVertically) {
                Surface(Modifier.size(20.dp), shape = CircleShape, color = Secondary) {
                    Box(contentAlignment = Alignment.Center) {
                        Text("3", fontSize = 11.sp, fontWeight = FontWeight.Bold, color = Color.White)
                    }
                }
                Spacer(Modifier.width(8.dp))
                Text("PASO FINAL DE ACTIVACIÓN", fontSize = 11.sp, fontWeight = FontWeight.Bold,
                    color = Secondary, letterSpacing = 0.5.sp)
            }

            ScreenTitle("Método de Pago", 26.sp)
            BodyText("Configure sus métodos para reservar cupos y despachar encomiendas sin filas ni demoras en taquilla.")

            CardPreview(number, holder, expiry)

            SegmentedSelector(
                listOf(
                    "Tarjeta" to Icons.Default.CreditCard,
                    "Billeteras" to Icons.Default.AccountBalanceWallet,
                    "Taquilla" to Icons.Default.Store
                ),
                tab
            ) { tab = it }

            if (tab != 0) {
                Text("Este método estará disponible próximamente.", fontSize = 13.sp, color = Outline)
            } else {
                Row(Modifier.fillMaxWidth(), Arrangement.SpaceBetween, Alignment.CenterVertically) {
                    Text("Aceptamos franquicias nacionales", fontSize = 11.sp, color = OnSurfaceVariant)
                    Row(horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                        listOf("VISA", "MC", "AMEX").forEach { brand ->
                            Surface(color = Color.White, shape = RoundedCornerShape(4.dp),
                                border = BorderStroke(1.dp, OutlineVariant)) {
                                Text(brand, Modifier.padding(horizontal = 6.dp, vertical = 2.dp),
                                    fontSize = 10.sp, fontWeight = FontWeight.Bold, color = PrimaryLight)
                            }
                        }
                    }
                }

                LabeledField("Nombre del titular de la tarjeta", holder, { holder = it },
                    "Como aparece en el plástico", Icons.Default.Person)
                LabeledField("Número de tarjeta", number, { number = it.filter(Char::isDigit).take(16) },
                    "4500 0000 0000 0000", Icons.Default.CreditCard, numeric = true, aside = "Débito o Crédito")
                Row(Modifier.fillMaxWidth(), Arrangement.spacedBy(12.dp)) {
                    LabeledField("Vencimiento (MM/AA)", expiry, { expiry = it.filter(Char::isDigit).take(4) },
                        "MM/AA", Icons.Default.CalendarToday, Modifier.weight(1f), numeric = true)
                    LabeledField("Código (CVV)", cvv, { cvv = it.filter(Char::isDigit).take(4) },
                        "123", Icons.Default.Lock, Modifier.weight(1f), numeric = true, hidden = !showCvv,
                        trailing = {
                            IconButton(onClick = { showCvv = !showCvv }) {
                                Icon(if (showCvv) Icons.Default.VisibilityOff else Icons.Default.Visibility, null)
                            }
                        })
                }

                CheckOption(isDefault, { isDefault = it }, "Método de pago predeterminado",
                    "Prioriza en reservas rápidas y despacho de encomiendas")
                CheckOption(saveCard, { saveCard = it }, "Recordar tarjeta para próximos viajes",
                    "Tus credenciales se tokenizan de forma segura")

                SecurityAdviceBox(
                    title = "Cifrado Bancario SSL 256 bits y PCI-DSS",
                    message = "Tus datos financieros viajan encriptados de extremo a extremo. Ruta Confiable no almacena tu código de seguridad CVV."
                )

                PrimaryButton(
                    text = "Guardar método de pago", onClick = onFinish, enabled = valid,
                    leadingIcon = Icons.Default.Lock, trailingIcon = null
                )
            }

            Surface(
                modifier = Modifier.fillMaxWidth().clip(RoundedCornerShape(8.dp)).clickable { onFinish() },
                color = SurfaceSoft, shape = RoundedCornerShape(8.dp), border = BorderStroke(1.dp, OutlineVariant)
            ) {
                Row(Modifier.padding(14.dp), Arrangement.Center, Alignment.CenterVertically) {
                    Icon(Icons.Default.Store, null, Modifier.size(16.dp), tint = PrimaryLight)
                    Spacer(Modifier.width(8.dp))
                    Text("Pagaré en efectivo en la terminal (Omitir)", fontSize = 12.sp,
                        fontWeight = FontWeight.Bold, color = PrimaryLight)
                }
            }
            Spacer(Modifier.height(8.dp))
        }
    }
}

@Composable
private fun CardPreview(number: String, holder: String, expiry: String) {
    val numberText = number.padEnd(16, '•').chunked(4).joinToString("  ")
    val expiryText = if (expiry.length > 2) expiry.take(2) + "/" + expiry.drop(2) else expiry.ifEmpty { "MM/AA" }
    Box(
        Modifier
            .fillMaxWidth()
            .height(180.dp)
            .clip(RoundedCornerShape(16.dp))
            .background(Brush.linearGradient(listOf(PrimaryLight, Color(0xFF1E3A5F))))
            .padding(18.dp)
    ) {
        Column(Modifier.fillMaxSize(), Arrangement.SpaceBetween) {
            Row(Modifier.fillMaxWidth(), Arrangement.SpaceBetween, Alignment.CenterVertically) {
                Text("RUTA CARD", fontSize = 16.sp, fontWeight = FontWeight.Bold,
                    color = Color.White, fontFamily = FontFamily.Serif)
                Surface(color = Color.White.copy(alpha = 0.15f), shape = RoundedCornerShape(6.dp)) {
                    Text("DÉBITO / CRÉDITO", Modifier.padding(horizontal = 8.dp, vertical = 3.dp),
                        fontSize = 9.sp, fontWeight = FontWeight.Bold, color = Color.White)
                }
            }
            Surface(Modifier.size(width = 36.dp, height = 28.dp), shape = RoundedCornerShape(6.dp),
                color = Color(0xFFE0B04A)) {}
            Text(numberText, fontSize = 18.sp, fontWeight = FontWeight.Medium, color = Color.White, letterSpacing = 1.sp)
            Row(Modifier.fillMaxWidth(), Arrangement.SpaceBetween, Alignment.Bottom) {
                Column(Modifier.weight(1f)) {
                    Text("TITULAR", fontSize = 8.sp, color = Color.White.copy(alpha = 0.6f))
                    Text(holder.ifBlank { "NOMBRE COMPLETO" }.uppercase(), fontSize = 12.sp,
                        fontWeight = FontWeight.Bold, color = Color.White, maxLines = 1)
                }
                Column(horizontalAlignment = Alignment.End) {
                    Text("VENCE", fontSize = 8.sp, color = Color.White.copy(alpha = 0.6f))
                    Text(expiryText, fontSize = 12.sp, fontWeight = FontWeight.Bold, color = Color.White)
                }
            }
        }
    }
}

@Composable
private fun LabeledField(
    label: String,
    value: String,
    onChange: (String) -> Unit,
    placeholder: String,
    icon: ImageVector,
    modifier: Modifier = Modifier,
    numeric: Boolean = false,
    hidden: Boolean = false,
    aside: String? = null,
    trailing: (@Composable () -> Unit)? = null
) {
    Column(modifier) {
        Row(Modifier.fillMaxWidth(), Arrangement.SpaceBetween) {
            Text(label, fontSize = 12.sp, fontWeight = FontWeight.SemiBold, color = PrimaryLight)
            aside?.let { Text(it, fontSize = 11.sp, color = Outline) }
        }
        Spacer(Modifier.height(4.dp))
        OutlinedTextField(
            value = value,
            onValueChange = onChange,
            placeholder = { Text(placeholder) },
            leadingIcon = { Icon(icon, null) },
            trailingIcon = trailing,
            visualTransformation = if (hidden) PasswordVisualTransformation() else VisualTransformation.None,
            keyboardOptions = KeyboardOptions(keyboardType = if (numeric) KeyboardType.Number else KeyboardType.Text),
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(8.dp),
            colors = customTextFieldColors(),
            singleLine = true
        )
    }
}

@Composable
private fun CheckOption(checked: Boolean, onChange: (Boolean) -> Unit, title: String, subtitle: String) {
    Row(Modifier.fillMaxWidth(), verticalAlignment = Alignment.CenterVertically) {
        Column(Modifier.weight(1f)) {
            Text(title, fontSize = 12.sp, fontWeight = FontWeight.Bold, color = PrimaryLight)
            Text(subtitle, fontSize = 11.sp, color = Outline)
        }
        Checkbox(checked, onChange, colors = CheckboxDefaults.colors(checkedColor = PrimaryLight))
    }
}

@Preview(showBackground = true, heightDp = 1200)
@Composable
fun PaymentMethodsScreenPreview() {
    RutaConfiableTheme { PaymentMethodsScreen() }
}