package me.mauromax.rutaconfiable.ui.screens

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
fun DriverRegistrationFormContent(
    state: RegistrationUiState,
    viewModel: RegistrationViewModel,
    onSubmit: () -> Unit
) {
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
                        imageVector = Icons.Default.LocalShipping,
                        contentDescription = null,
                        tint = Secondary,
                        modifier = Modifier.size(14.dp)
                    )
                    Spacer(modifier = Modifier.width(4.dp))
                    Text(
                        text = "REGISTRO DE CONDUCTOR",
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
            text = "Crea tu cuenta de Conductor",
            fontSize = 24.sp,
            fontWeight = FontWeight.Bold,
            color = PrimaryLight,
            fontFamily = FontFamily.Serif
        )

        Text(
            text = "Coordinación logística regional y flete seguro. Selecciona tu perfil operativo para comenzar.",
            fontSize = 13.sp,
            color = Color(0xFF44474D),
            lineHeight = 18.sp
        )

        CardSectionContainer(
            title = "1. Información Personal",
            icon = Icons.Default.Badge,
            tagText = "Obligatorio",
            tagColor = Color(0xFF74777E)
        ) {
            OutlinedTextField(
                value = state.driverFullName,
                onValueChange = { viewModel.updateDriverFullName(it) },
                label = { Text("Nombre completo") },
                placeholder = { Text("Ej. Carlos Mendoza Rivas") },
                leadingIcon = { Icon(Icons.Default.Person, contentDescription = null) },
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(8.dp),
                colors = customTextFieldColors()
            )

            OutlinedTextField(
                value = state.driverEmail,
                onValueChange = { viewModel.updateDriverEmail(it) },
                label = { Text("Correo electrónico") },
                placeholder = { Text("operaciones@ejemplo.com") },
                leadingIcon = { Icon(Icons.Default.Email, contentDescription = null) },
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(8.dp),
                colors = customTextFieldColors()
            )

            PasswordTextField(
                value = state.driverPassword,
                onValueChange = { viewModel.updateDriverPassword(it) },
                label = "Contraseña",
                placeholder = "Mínimo 8 caracteres"
            )
        }

        CardSectionContainer(
            title = "2. Licencia de Conducción",
            icon = Icons.Default.AssignmentInd,
            tagText = "RUNT Vigente",
            tagColor = Secondary
        ) {
            OutlinedTextField(
                value = state.driverLicenseNumber,
                onValueChange = { viewModel.updateDriverLicenseNumber(it) },
                label = { Text("Número de licencia (Pase)") },
                placeholder = { Text("Ej. 1020485934") },
                leadingIcon = { Icon(Icons.Default.Pin, contentDescription = null) },
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(8.dp),
                colors = customTextFieldColors()
            )
        }

        CardSectionContainer(
            title = "3. Requisitos del Vehículo",
            icon = Icons.Default.DirectionsCar,
            tagText = "Inspección Vial",
            tagColor = Color(0xFF74777E)
        ) {
            OutlinedTextField(
                value = state.driverVehiclePlate,
                onValueChange = { viewModel.updateDriverVehiclePlate(it) },
                label = { Text("Placa del vehículo") },
                placeholder = { Text("AAA-123") },
                leadingIcon = { Icon(Icons.Default.Subtitles, contentDescription = null) },
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(8.dp),
                colors = customTextFieldColors()
            )

            OutlinedTextField(
                value = state.driverSoatNumber,
                onValueChange = { viewModel.updateDriverSoatNumber(it) },
                label = { Text("Número de SOAT") },
                placeholder = { Text("10094827") },
                leadingIcon = { Icon(Icons.Default.Verified, contentDescription = null) },
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(8.dp),
                colors = customTextFieldColors()
            )
        }

        Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
            Row(
                verticalAlignment = Alignment.Top,
                modifier = Modifier.fillMaxWidth()
            ) {
                Checkbox(
                    checked = state.driverTermsAccepted,
                    onCheckedChange = { viewModel.updateDriverTermsAccepted(it) },
                    colors = CheckboxDefaults.colors(checkedColor = PrimaryLight)
                )
                Text(
                    text = "Acepto los Términos y Condiciones de Ruta Confiable y autorizo el tratamiento de mis datos personales.",
                    fontSize = 12.sp,
                    color = Color(0xFF44474D),
                    lineHeight = 16.sp,
                    modifier = Modifier.padding(top = 10.dp)
                )
            }
        }

        PrimaryButton(
            text = "Crear cuenta de conductor",
            onClick = onSubmit
        )
    }
}