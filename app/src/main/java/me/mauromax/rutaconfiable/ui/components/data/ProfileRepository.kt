package me.mauromax.rutaconfiable.ui.components.data

import me.mauromax.rutaconfiable.ui.screens.AvailableTrip
import me.mauromax.rutaconfiable.ui.screens.ProfileStats
import me.mauromax.rutaconfiable.ui.screens.RegistrationRole
import me.mauromax.rutaconfiable.ui.screens.Trip
import me.mauromax.rutaconfiable.ui.theme.Outline
import me.mauromax.rutaconfiable.ui.theme.StatusAvailable
import me.mauromax.rutaconfiable.ui.theme.StatusAvailableBg
import me.mauromax.rutaconfiable.ui.theme.SurfaceMuted
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Locale

/** Lo que muestra el perfil, calculado con datos reales del usuario. */
data class ProfileData(
    val name: String = "Usuario",
    val stats: ProfileStats = ProfileStats(),
    val trips: List<Trip> = emptyList()
)

object ProfileRepository {

    /**
     * Carga nombre, contadores y viajes del usuario actual.
     * Conductor: sus viajes publicados. Cliente: todavía no hay reservas, así que va vacío.
     * "En curso", "Completados" y las calificaciones quedan en 0 hasta que existan
     * estados de viaje y calificaciones.
     */
    suspend fun load(role: RegistrationRole): ProfileData {
        val name = AuthRepository.getName() ?: "Usuario"
        if (role != RegistrationRole.DRIVER) return ProfileData(name = name)

        val mine = TripRepository.loadMyTrips().getOrDefault(emptyList())
        val todayStart = startOfToday()
        val upcoming = mine.map { isUpcoming(it.date, todayStart) }

        return ProfileData(
            name = name,
            stats = ProfileStats(active = upcoming.count { it }),
            trips = mine.mapIndexed { i, trip -> trip.toProfileTrip(upcoming[i]) }
        )
    }

    private fun startOfToday(): Long = Calendar.getInstance().apply {
        set(Calendar.HOUR_OF_DAY, 0)
        set(Calendar.MINUTE, 0)
        set(Calendar.SECOND, 0)
        set(Calendar.MILLISECOND, 0)
    }.timeInMillis

    /** Fecha dd/MM/yyyy. Si no se puede leer, se considera vigente. */
    private fun isUpcoming(date: String, todayStart: Long): Boolean {
        val parsed = runCatching {
            SimpleDateFormat("dd/MM/yyyy", Locale("es", "CO")).apply { isLenient = false }.parse(date)
        }.getOrNull() ?: return true
        return parsed.time >= todayStart
    }

    private fun AvailableTrip.toProfileTrip(upcoming: Boolean) = Trip(
        date = listOf(date, departure).filter { it.isNotBlank() }.joinToString(" · "),
        from = origin,
        to = destination,
        tag = if (upcoming) "PUBLICADO" else "FECHA PASADA",
        tagBg = if (upcoming) StatusAvailableBg else SurfaceMuted,
        tagFg = if (upcoming) StatusAvailable else Outline,
        price = if (hasCargo) cargoPrice else seatPrice,
        info = buildList {
            add(listOf(vehicle, plate).filter { it.isNotBlank() }.joinToString(" · "))
            if (hasCargo) add("Carga $cargoTotalKg kg")
            if (hasSeats) add("$seatsTotal cupos")
        }.filter { it.isNotBlank() }.joinToString(" · ")
    )
}