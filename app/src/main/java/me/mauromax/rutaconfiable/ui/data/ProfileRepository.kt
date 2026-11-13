package me.mauromax.rutaconfiable.ui.data

import me.mauromax.rutaconfiable.ui.screens.AvailableTrip
import me.mauromax.rutaconfiable.ui.screens.ManifestEntry
import me.mauromax.rutaconfiable.ui.screens.NextTrip
import me.mauromax.rutaconfiable.ui.screens.ProfileStats
import me.mauromax.rutaconfiable.ui.screens.RegistrationRole
import me.mauromax.rutaconfiable.ui.screens.Trip
import me.mauromax.rutaconfiable.ui.theme.Outline
import me.mauromax.rutaconfiable.ui.theme.StatusAvailable
import me.mauromax.rutaconfiable.ui.theme.StatusAvailableBg
import me.mauromax.rutaconfiable.ui.theme.StatusBooked
import me.mauromax.rutaconfiable.ui.theme.StatusBookedBg
import me.mauromax.rutaconfiable.ui.theme.StatusPending
import me.mauromax.rutaconfiable.ui.theme.StatusPendingBg
import me.mauromax.rutaconfiable.ui.theme.SurfaceMuted
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Locale

/** Lo que muestra el perfil, calculado con datos reales del usuario. */
data class ProfileData(
    val name: String = "Usuario",
    val stats: ProfileStats = ProfileStats(),
    val trips: List<Trip> = emptyList(),
    val nextTrip: NextTrip? = null
)

object ProfileRepository {

    /**
     * Carga nombre, contadores, lista de viajes y próximo viaje del usuario actual.
     * Conductor: sus viajes publicados (y las reservas del próximo viaje, para el manifiesto).
     * Cliente: sus reservas.
     * "En curso", "Completados" y las calificaciones quedan en 0 hasta que existan
     * estados de viaje y calificaciones.
     */
    suspend fun load(role: RegistrationRole): ProfileData {
        val name = AuthRepository.getName() ?: "Usuario"
        val todayStart = startOfToday()
        return if (role == RegistrationRole.DRIVER) loadDriver(name, todayStart)
        else loadClient(name, todayStart)
    }

    private suspend fun loadDriver(name: String, todayStart: Long): ProfileData {
        val mine = TripRepository.loadMyTrips().getOrDefault(emptyList())
        val upcoming = mine.map { isUpcoming(it.date, todayStart) }

        // Próximo viaje: el vigente con la fecha más cercana (y hora más temprana).
        val nextTrip = mine
            .mapNotNull { trip -> parseDate(trip.date)?.takeIf { it >= todayStart }?.let { trip to it } }
            .minWithOrNull(compareBy({ it.second }, { it.first.departure }))
            ?.let { (trip, dateMillis) ->
                val bookings = BookingRepository.loadTripBookings(trip.id).getOrDefault(emptyList())
                trip.toNextTrip(dateMillis, todayStart, bookings)
            }

        return ProfileData(
            name = name,
            stats = ProfileStats(active = upcoming.count { it }),
            trips = mine.mapIndexed { i, trip -> trip.toProfileTrip(upcoming[i]) },
            nextTrip = nextTrip
        )
    }

    private suspend fun loadClient(name: String, todayStart: Long): ProfileData {
        val bookings = BookingRepository.loadMyBookings().getOrDefault(emptyList())
        val live = bookings.filter { it.status == "pending" || it.status == "confirmed" }
        val upcoming = bookings.map { isUpcoming(it.date, todayStart) }

        val nextBooking = live
            .mapNotNull { b -> parseDate(b.date)?.takeIf { it >= todayStart }?.let { b to it } }
            .minWithOrNull(compareBy({ it.second }, { it.first.departure }))

        return ProfileData(
            name = name,
            stats = ProfileStats(
                active = bookings.indices.count {
                    upcoming[it] && (bookings[it].status == "pending" || bookings[it].status == "confirmed")
                }
            ),
            trips = bookings.mapIndexed { i, b -> b.toProfileTrip(upcoming[i]) },
            nextTrip = nextBooking?.let { (b, dateMillis) -> b.toNextTrip(dateMillis, todayStart) }
        )
    }

    private fun startOfToday(): Long = Calendar.getInstance().apply {
        set(Calendar.HOUR_OF_DAY, 0)
        set(Calendar.MINUTE, 0)
        set(Calendar.SECOND, 0)
        set(Calendar.MILLISECOND, 0)
    }.timeInMillis

    /** Fecha dd/MM/yyyy a milisegundos, o null si no se puede leer. */
    private fun parseDate(date: String): Long? = runCatching {
        SimpleDateFormat("dd/MM/yyyy", Locale("es", "CO")).apply { isLenient = false }.parse(date)?.time
    }.getOrNull()

    /** Si la fecha no se puede leer, se considera vigente. */
    private fun isUpcoming(date: String, todayStart: Long): Boolean {
        val parsed = parseDate(date) ?: return true
        return parsed >= todayStart
    }

    private fun leavesIn(dateMillis: Long, todayStart: Long): String {
        val days = Math.round((dateMillis - todayStart) / 86_400_000.0).toInt()
        return when {
            days <= 0 -> "Sale hoy"
            days == 1 -> "Sale mañana"
            else -> "Sale en $days días"
        }
    }

    private fun AvailableTrip.toNextTrip(
        dateMillis: Long,
        todayStart: Long,
        bookings: List<Booking>
    ): NextTrip {
        val cargoBooked = if (hasCargo) (cargoTotalKg - cargoFreeKg).coerceAtLeast(0) else 0
        val seatsBooked = if (hasSeats) (seatsTotal - seatsFree).coerceAtLeast(0) else 0
        val ratios = buildList {
            if (hasCargo && cargoTotalKg > 0) add(cargoBooked.toFloat() / cargoTotalKg)
            if (hasSeats && seatsTotal > 0) add(seatsBooked.toFloat() / seatsTotal)
        }
        return NextTrip(
            id = id,
            leavesIn = leavesIn(dateMillis, todayStart),
            from = origin,
            to = destination,
            departure = departure,
            arrival = "—",
            duration = duration,
            via = distance,
            occupancy = if (ratios.isEmpty()) 0f else ratios.average().toFloat().coerceIn(0f, 1f),
            vehicle = listOf(vehicle, plate).filter { it.isNotBlank() }.joinToString(" · "),
            cargo = if (hasCargo) "$cargoBooked / $cargoTotalKg kg" else "No aplica",
            seats = if (hasSeats) "$seatsBooked / $seatsTotal" else "No aplica",
            date = date,
            entries = bookings.map { it.toEntry(it.clientName.ifBlank { "Cliente" }) }
        )
    }

    private fun Booking.toNextTrip(dateMillis: Long, todayStart: Long) = NextTrip(
        id = tripId,
        leavesIn = leavesIn(dateMillis, todayStart),
        from = origin,
        to = destination,
        departure = departure,
        arrival = "—",
        duration = "—",
        via = "—",
        occupancy = null,
        vehicle = listOf(vehicle, plate).filter { it.isNotBlank() }.joinToString(" · ")
            .ifBlank { "—" },
        cargo = if (cargoKg > 0) "$cargoKg kg" else "—",
        seats = if (seats > 0) "$seats" else "—",
        date = date,
        entries = listOf(toEntry("Tú"))
    )

    private fun Booking.toEntry(who: String) = ManifestEntry(
        name = who,
        detail = reservedText(),
        status = statusLabel()
    )

    private fun Booking.reservedText(): String = buildList {
        if (seats > 0) add(if (seats == 1) "1 cupo" else "$seats cupos")
        if (cargoKg > 0) add("$cargoKg kg")
    }.joinToString(" · ")

    private fun Booking.statusLabel() = when (status) {
        "confirmed" -> "CONFIRMADA"
        "rejected" -> "RECHAZADA"
        "cancelled" -> "CANCELADA"
        else -> "PENDIENTE"
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

    private fun Booking.toProfileTrip(upcoming: Boolean): Trip {
        val (bg, fg) = when {
            status == "rejected" || status == "cancelled" -> StatusBookedBg to StatusBooked
            !upcoming -> SurfaceMuted to Outline
            status == "confirmed" -> StatusAvailableBg to StatusAvailable
            else -> StatusPendingBg to StatusPending
        }
        return Trip(
            date = listOf(date, departure).filter { it.isNotBlank() }.joinToString(" · "),
            from = origin,
            to = destination,
            tag = if (!upcoming && status == "pending") "FECHA PASADA" else statusLabel(),
            tagBg = bg,
            tagFg = fg,
            price = if (seats > 0) seatPrice else cargoPrice,
            info = buildList {
                add(reservedText())
                add(listOf(vehicle, plate).filter { it.isNotBlank() }.joinToString(" · "))
            }.filter { it.isNotBlank() }.joinToString(" · ")
        )
    }
}