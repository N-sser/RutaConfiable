package me.mauromax.rutaconfiable.ui.screens

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import me.mauromax.rutaconfiable.ui.components.*
import me.mauromax.rutaconfiable.ui.theme.*

@Composable
fun ClientRegistrationFormContent(
    state: RegistrationUiState,
    viewModel: RegistrationViewModel,
    onSubmit: () -> Unit
) {
    var docDropdownExpanded by remember { mutableStateOf(false) }

    Column(verticalArrangement = Arrangement.spacedBy(16.dp)) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Surface(
                color = Color(0xFFE5EEFF),
                shape = RoundedCornerShape(16.dp)
            ) {
                Row(
                    modifier = Modifier.padding(horizontal = 10.dp, vertical = 4.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Icon(
                        imageVector = Icons.Default.Person,
                        contentDescription = null,
                        tint = Secondary,
                        modifier = Modifier.size(14.dp)
                    )
                    Spacer(modifier = Modifier.width(4.dp))
                    Text(
                        text = "REGISTRO DE CLIENTE",
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Bold,
                        color = PrimaryLight
                    )
                }
            }

            Surface(
                color = Color(0xFFE5EEFF),
                shape = RoundedCornerShape(16.dp)
            ) {
                Text(
                    text = "Paso 1 de 2",
                    modifier = Modifier.padding(horizontal = 10.dp, vertical = 4.dp),
                    fontSize = 11.sp,
                    fontWeight = FontWeight.Bold,
                    color = PrimaryLight
                )
            }
        }

        Text(
            text = "Crea tu cuenta de Cliente",
            fontSize = 24.sp,
            fontWeight = FontWeight.Bold,
            color = PrimaryLight,
            fontFamily = FontFamily.Serif
        )

        Text(
            text = "Reserva cupos cómodos en viajes intermunicipales o despacha encomiendas con monitoreo satelital en tiempo real.",
            fontSize = 13.sp,
            color = Color(0xFF44474D),
            lineHeight = 18.sp
        )

        CardSectionContainer(
            title = "1. Datos Personales y Acceso",
            icon = Icons.Default.Badge,
            subtitle = "Tus credenciales para gestionar reservas y despachos"
        ) {
            OutlinedTextField(
                value = state.clientFullName,
                onValueChange = { viewModel.updateClientFullName(it) },
                label = { Text("Nombre completo") },
                placeholder = { Text("Ej: Valentina Gómez Salazar") },
                leadingIcon = { Icon(Icons.Default.Person, contentDescription = null) },
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(8.dp),
                colors = customTextFieldColors()
            )

            OutlinedTextField(
                value = state.clientEmail,
                onValueChange = { viewModel.updateClientEmail(it) },
                label = { Text("Correo electrónico") },
                placeholder = { Text("ejemplo@correo.com") },
                leadingIcon = { Icon(Icons.Default.Email, contentDescription = null) },
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(8.dp),
                colors = customTextFieldColors()
            )

            Column {
                Text(
                    text = "Número de WhatsApp / Celular",
                    fontSize = 12.sp,
                    fontWeight = FontWeight.SemiBold,
                    color = PrimaryLight,
                    modifier = Modifier.padding(bottom = 4.dp)
                )
                PhoneWithCountrySelectorField(
                    phoneValue = state.clientPhone,
                    onPhoneChange = { viewModel.updateClientPhone(it) },
                    placeholderText = "310 456 7890"
                )
            }

            Column {
                PasswordTextField(
                    value = state.clientPassword,
                    onValueChange = { viewModel.updateClientPassword(it) },
                    label = "Contraseña segura",
                    placeholder = "Mínimo 8 caracteres"
                )
                Text(
                    text = "Incluye mayúsculas, números o caracteres especiales.",
                    fontSize = 11.sp,
                    color = Color(0xFF74777E),
                    modifier = Modifier.padding(top = 4.dp, start = 4.dp)
                )
            }
        }

        CardSectionContainer(
            title = "2. Validación de Seguridad",
            icon = Icons.Default.VerifiedUser,
            subtitle = "Requerido para control de abordaje en terminal"
        ) {
            Text(
                text = "Documento de Identidad",
                fontSize = 12.sp,
                fontWeight = FontWeight.SemiBold,
                color = PrimaryLight
            )

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Box {
                    Surface(
                        modifier = Modifier
                            .height(56.dp)
                            .clickable { docDropdownExpanded = true },
                        shape = RoundedCornerShape(8.dp),
                        color = Color(0xFFEFF4FF),
                        border = BorderStroke(1.dp, Color(0xFFC4C6CE))
                    ) {
                        Row(
                            modifier = Modifier.padding(horizontal = 12.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text(state.clientDocumentType, fontSize = 13.sp, fontWeight = FontWeight.Bold, color = PrimaryLight)
                            Spacer(modifier = Modifier.width(4.dp))
                            Icon(Icons.Default.ArrowDropDown, contentDescription = null, tint = PrimaryLight)
                        }
                    }

                    DropdownMenu(
                        expanded = docDropdownExpanded,
                        onDismissRequest = { docDropdownExpanded = false }
                    ) {
                        DropdownMenuItem(
                            text = { Text("C.C.") },
                            onClick = { viewModel.updateClientDocumentType("C.C."); docDropdownExpanded = false }
                        )
                        DropdownMenuItem(
                            text = { Text("C.E.") },
                            onClick = { viewModel.updateClientDocumentType("C.E."); docDropdownExpanded = false }
                        )
                        DropdownMenuItem(
                            text = { Text("Pasaporte") },
                            onClick = { viewModel.updateClientDocumentType("Pasaporte"); docDropdownExpanded = false }
                        )
                    }
                }

                OutlinedTextField(
                    value = state.clientDocumentNumber,
                    onValueChange = { viewModel.updateClientDocumentNumber(it) },
                    placeholder = { Text("1.020.485.934") },
                    leadingIcon = { Icon(Icons.Default.Pin, contentDescription = null) },
                    modifier = Modifier.weight(1f),
                    shape = RoundedCornerShape(8.dp),
                    colors = customTextFieldColors()
                )
            }
        }

        Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
            Row(
                verticalAlignment = Alignment.Top,
                modifier = Modifier.fillMaxWidth()
            ) {
                Checkbox(
                    checked = state.clientTermsAccepted,
                    onCheckedChange = { viewModel.updateClientTermsAccepted(it) },
                    colors = CheckboxDefaults.colors(checkedColor = Secondary)
                )
                Text(
                    text = "Acepto los Términos y Condiciones del servicio de transporte y la Política de Privacidad de datos de Ruta Confiable.",
                    fontSize = 12.sp,
                    color = Color(0xFF44474D),
                    lineHeight = 16.sp,
                    modifier = Modifier.padding(top = 10.dp)
                )
            }

            Row(
                verticalAlignment = Alignment.Top,
                modifier = Modifier.fillMaxWidth()
            ) {
                Checkbox(
                    checked = state.clientWhatsappAlertsAccepted,
                    onCheckedChange = { viewModel.updateClientWhatsappAlerts(it) },
                    colors = CheckboxDefaults.colors(checkedColor = Secondary)
                )
                Text(
                    text = "Deseo recibir alertas de WhatsApp con el estado del bus y ubicación en tiempo real de mis encomiendas.",
                    fontSize = 12.sp,
                    color = Color(0xFF44474D),
                    lineHeight = 16.sp,
                    modifier = Modifier.padding(top = 10.dp)
                )
            }
        }

        PrimaryButton(
            text = "Crear cuenta de cliente",
            onClick = onSubmit
        )
    }
}