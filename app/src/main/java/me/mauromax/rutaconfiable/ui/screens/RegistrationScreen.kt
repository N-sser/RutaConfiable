
package me.mauromax.rutaconfiable.ui.screens

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.LocalShipping
import androidx.compose.material.icons.filled.Person
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import me.mauromax.rutaconfiable.ui.components.*
import me.mauromax.rutaconfiable.ui.theme.*

enum class RegistrationRole {
    DRIVER,
    CLIENT
}

private val Body = Color(0xFF44474D)

@Composable
fun RegistrationScreen(
    initialRole: RegistrationRole = RegistrationRole.DRIVER,
    onRegistrationSubmitted: (RegistrationRole) -> Unit = {},
    onBackClick: () -> Unit = {},
    onLoginClick: () -> Unit = {},
    onAddPaymentClick: () -> Unit = {},
    viewModel: RegistrationViewModel = viewModel()
) {
    val state = viewModel.uiState

    LaunchedEffect(initialRole) {
        viewModel.updateRole(initialRole)
    }

    Scaffold(
        topBar = {
            StandardAppTopBar(
                onBackClick = onBackClick,
                showSecurityBadge = true
            )
        },
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

            // Selector de tipo de usuario
            Surface(
                modifier = Modifier.fillMaxWidth(),
                color = Color(0xFFEFF4FF),
                shape = RoundedCornerShape(12.dp)
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(4.dp)
                ) {
                    RoleOptionButton(
                        text = "Conductor",
                        icon = Icons.Default.LocalShipping,
                        isSelected = state.currentRole == RegistrationRole.DRIVER,
                        modifier = Modifier.weight(1f),
                        onClick = {
                            viewModel.updateRole(RegistrationRole.DRIVER)
                        }
                    )

                    RoleOptionButton(
                        text = "Cliente",
                        icon = Icons.Default.Person,
                        isSelected = state.currentRole == RegistrationRole.CLIENT,
                        modifier = Modifier.weight(1f),
                        onClick = {
                            viewModel.updateRole(RegistrationRole.CLIENT)
                        }
                    )
                }
            }

            // Mostrar errores de Firebase
            viewModel.errorMessage?.let { message ->
                Text(
                    text = message,
                    color = MaterialTheme.colorScheme.error,
                    fontSize = 12.sp,
                    modifier = Modifier.padding(horizontal = 4.dp)
                )
            }

            // Formulario correspondiente al rol
            if (state.currentRole == RegistrationRole.DRIVER) {

                DriverRegistrationFormContent(
                    state = state,
                    viewModel = viewModel,
                    onSubmit = {
                        viewModel.registerDriver {
                            onRegistrationSubmitted(
                                RegistrationRole.DRIVER
                            )
                        }
                    }
                )

            } else {

                ClientRegistrationFormContent(
                    state = state,
                    viewModel = viewModel,
                    onSubmit = {
                        viewModel.registerClient {
                            onRegistrationSubmitted(
                                RegistrationRole.CLIENT
                            )
                        }
                    }
                )
            }

            // Acceso al inicio de sesión
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 4.dp),
                horizontalArrangement = Arrangement.Center,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "¿Ya tienes una cuenta registrada? ",
                    fontSize = 13.sp,
                    color = Body
                )

                Text(
                    text = "Iniciar sesión",
                    fontSize = 13.sp,
                    fontWeight = FontWeight.Bold,
                    color = Secondary,
                    modifier = Modifier.clickable {
                        onLoginClick()
                    }
                )
            }

            Spacer(modifier = Modifier.height(16.dp))
        }
    }
}
