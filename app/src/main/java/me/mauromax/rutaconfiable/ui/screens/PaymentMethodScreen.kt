package me.mauromax.rutaconfiable.ui.screens

import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import me.mauromax.rutaconfiable.ui.components.StandardAppTopBar
import me.mauromax.rutaconfiable.ui.theme.*

@Composable
fun PaymentMethodsScreen(onBackClick: () -> Unit = {}) {
    Scaffold(
        topBar = { StandardAppTopBar(onBackClick = onBackClick, showSecurityBadge = true) },
        containerColor = Background
    ) { innerPadding ->
        Box(
            modifier = Modifier.fillMaxSize().padding(innerPadding),
            contentAlignment = Alignment.Center
        ) {
            Text(
                text = "Métodos de pago",
                fontSize = 22.sp,
                fontWeight = FontWeight.Bold,
                color = PrimaryLight,
                fontFamily = FontFamily.Serif
            )
        }
    }
}

@Preview(showBackground = true)
@Composable
fun PaymentMethodsScreenPreview() {
    RutaConfiableTheme { PaymentMethodsScreen() }
}