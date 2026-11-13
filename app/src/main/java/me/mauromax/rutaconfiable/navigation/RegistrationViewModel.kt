package me.mauromax.rutaconfiable.ui.screens

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.launch
import me.mauromax.rutaconfiable.ui.data.AuthRepository

data class RegistrationUiState(
    val currentRole: RegistrationRole = RegistrationRole.DRIVER,
    val clientFullName: String = "",
    val clientEmail: String = "",
    val clientPhone: String = "",
    val clientPassword: String = "",
    val clientDocumentType: String = "C.C.",
    val clientDocumentNumber: String = "",
    val clientTermsAccepted: Boolean = false,
    val clientWhatsappAlertsAccepted: Boolean = false,
    val driverFullName: String = "",
    val driverEmail: String = "",
    val driverPassword: String = "",
    val driverLicenseNumber: String = "",
    val driverSelectedVehicleType: String = "Camión Ligero",
    val driverVehiclePlate: String = "",
    val driverSoatNumber: String = "",
    val driverSoatExpiration: String = "",
    val driverTermsAccepted: Boolean = false,
    val driverPoliciesAccepted: Boolean = false
)

class RegistrationViewModel : ViewModel() {
    var uiState by mutableStateOf(RegistrationUiState())
        private set

    var isLoading by mutableStateOf(false)
        private set

    var errorMessage by mutableStateOf<String?>(null)
        private set

    fun updateRole(role: RegistrationRole) {
        uiState = uiState.copy(currentRole = role)
        errorMessage = null
    }

    fun registerClient(onSuccess: () -> Unit) {
        val s = uiState
        if (s.clientFullName.isBlank() || s.clientEmail.isBlank() || s.clientPassword.isBlank()) {
            errorMessage = "Por favor completa los campos obligatorios (nombre, correo y contraseña)."
            return
        }
        if (!s.clientTermsAccepted) {
            errorMessage = "Debes aceptar los Términos y Condiciones."
            return
        }
        errorMessage = null
        isLoading = true
        viewModelScope.launch {
            AuthRepository.register(
                email = s.clientEmail.trim(),
                password = s.clientPassword,
                name = s.clientFullName.trim(),
                role = "client"
            ).onSuccess {
                isLoading = false
                onSuccess()
            }.onFailure { e ->
                isLoading = false
                errorMessage = AuthRepository.friendlyError(e)
            }
        }
    }

    fun registerDriver(onSuccess: () -> Unit) {
        val s = uiState
        if (s.driverFullName.isBlank() || s.driverEmail.isBlank() || s.driverPassword.isBlank()) {
            errorMessage = "Por favor completa los campos obligatorios (nombre, correo y contraseña)."
            return
        }
        if (!s.driverTermsAccepted || !s.driverPoliciesAccepted) {
            errorMessage = "Debes aceptar los Términos y Políticas."
            return
        }
        errorMessage = null
        isLoading = true
        viewModelScope.launch {
            AuthRepository.register(
                email = s.driverEmail.trim(),
                password = s.driverPassword,
                name = s.driverFullName.trim(),
                role = "driver"
            ).onSuccess {
                isLoading = false
                onSuccess()
            }.onFailure { e ->
                isLoading = false
                errorMessage = AuthRepository.friendlyError(e)
            }
        }
    }

    fun updateClientFullName(v: String) { uiState = uiState.copy(clientFullName = v) }
    fun updateClientEmail(v: String) { uiState = uiState.copy(clientEmail = v) }
    fun updateClientPhone(v: String) { uiState = uiState.copy(clientPhone = v) }
    fun updateClientPassword(v: String) { uiState = uiState.copy(clientPassword = v) }
    fun updateClientDocumentType(v: String) { uiState = uiState.copy(clientDocumentType = v) }
    fun updateClientDocumentNumber(v: String) { uiState = uiState.copy(clientDocumentNumber = v) }
    fun updateClientTermsAccepted(v: Boolean) { uiState = uiState.copy(clientTermsAccepted = v) }
    fun updateClientWhatsappAlerts(v: Boolean) { uiState = uiState.copy(clientWhatsappAlertsAccepted = v) }

    fun updateDriverFullName(v: String) { uiState = uiState.copy(driverFullName = v) }
    fun updateDriverEmail(v: String) { uiState = uiState.copy(driverEmail = v) }
    fun updateDriverPassword(v: String) { uiState = uiState.copy(driverPassword = v) }
    fun updateDriverLicenseNumber(v: String) { uiState = uiState.copy(driverLicenseNumber = v) }
    fun updateDriverVehicleType(v: String) { uiState = uiState.copy(driverSelectedVehicleType = v) }
    fun updateDriverVehiclePlate(v: String) { uiState = uiState.copy(driverVehiclePlate = v) }
    fun updateDriverSoatNumber(v: String) { uiState = uiState.copy(driverSoatNumber = v) }
    fun updateDriverSoatExpiration(v: String) { uiState = uiState.copy(driverSoatExpiration = v) }
    fun updateDriverTermsAccepted(v: Boolean) { uiState = uiState.copy(driverTermsAccepted = v) }
    fun updateDriverPoliciesAccepted(v: Boolean) { uiState = uiState.copy(driverPoliciesAccepted = v) }
}
