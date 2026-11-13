package me.mauromax.rutaconfiable.ui.data

import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.DocumentSnapshot
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.tasks.await
import me.mauromax.rutaconfiable.ui.screens.AvailableTrip

/** Una reserva de cupos y/o carga en un viaje. [status]: "pending", "confirmed", "rejected"... */
data class Booking(
    val id: String,
    val tripId: String,
    val clientName: String,
    val seats: Int,
    val cargoKg: Int,
    val origin: String,
    val destination: String,
    val date: String,
    val departure: String,
    val seatPrice: String,
    val cargoPrice: String,
    val vehicle: String,
    val plate: String,
    val driverName: String,
    val status: String,
    val createdAt: Long
)

/**
 * Reservas de cupos y flete, guardadas en Firestore (colección "bookings").
 * Cada reserva descuenta los cupos y los kg del viaje en la MISMA transacción, así dos
 * clientes no pueden reservar el mismo cupo.
 */
object BookingRepository {
    private const val BOOKINGS = "bookings"
    private const val TRIPS = "trips"

    private val db get() = FirebaseFirestore.getInstance()
    private val auth get() = FirebaseAuth.getInstance()

    /**
     * Reserva [seats] cupos y/o [cargoKg] kg de carga en [trip] para el cliente con sesión.
     * Devuelve el id de la reserva. Los errores traen un mensaje listo para mostrar
     * ([TripException]); para el resto usa [TripRepository.friendlyError].
     */
    suspend fun book(trip: AvailableTrip, seats: Int, cargoKg: Int): Result<String> = runCatching {
        val uid = auth.currentUser?.uid
            ?: throw TripException("Inicia sesión para reservar.")
        if (seats < 0 || cargoKg < 0 || (seats == 0 && cargoKg == 0)) {
            throw TripException("Elige cuántos cupos o kilos quieres reservar.")
        }
        if (trip.driverUid == uid) {
            throw TripException("No puedes reservar tu propio viaje.")
        }

        val userDoc = db.collection("users").document(uid).get().await()
        if (userDoc.getString("role") != "client") {
            throw TripException("Solo los clientes pueden reservar.")
        }
        if (userDoc.getBoolean("verified") != true) {
            throw TripException("Tu cuenta aún no está verificada. Podrás reservar cuando sea aprobada.")
        }
        val clientName = userDoc.getString("name").orEmpty()

        val tripRef = db.collection(TRIPS).document(trip.id)
        // Id determinista "<códigoViaje>_<uidCliente>": una reserva por cliente y viaje.
        // Las reglas de Firestore lo exigen para validar el descuento de cupos.
        val bookingRef = db.collection(BOOKINGS).document("${trip.id}_$uid")

        db.runTransaction<Boolean> { tx ->
            val snap = tx.get(tripRef)
            if (!snap.exists()) throw TripException("Este viaje ya no está disponible.")
            if (tx.get(bookingRef).exists()) {
                throw TripException("Ya tienes una reserva en este viaje.")
            }

            val seatsFree = snap.getLong("seatsFree")?.toInt() ?: 0
            val cargoFree = snap.getLong("cargoFreeKg")?.toInt() ?: 0
            if (seats > 0 && !(snap.getBoolean("hasSeats") ?: true)) {
                throw TripException("Este viaje no lleva pasajeros.")
            }
            if (cargoKg > 0 && !(snap.getBoolean("hasCargo") ?: true)) {
                throw TripException("Este viaje no lleva carga.")
            }
            if (seats > seatsFree) {
                throw TripException(
                    if (seatsFree == 0) "Ya no quedan cupos." else "Solo quedan $seatsFree cupos."
                )
            }
            if (cargoKg > cargoFree) {
                throw TripException(
                    if (cargoFree == 0) "Ya no queda espacio de carga." else "Solo quedan $cargoFree kg libres."
                )
            }

            tx.update(
                tripRef,
                mapOf(
                    "seatsFree" to seatsFree - seats,
                    "cargoFreeKg" to cargoFree - cargoKg
                )
            )
            tx.set(
                bookingRef,
                mapOf(
                    "tripId" to trip.id,
                    "tripDriverUid" to (snap.getString("driverUid") ?: ""),
                    "clientUid" to uid,
                    "clientName" to clientName,
                    "vehicle" to trip.vehicle,
                    "plate" to trip.plate,
                    "driverName" to trip.driver,
                    "seats" to seats,
                    "cargoKg" to cargoKg,
                    "origin" to trip.origin,
                    "destination" to trip.destination,
                    "date" to trip.date,
                    "departure" to trip.departure,
                    "seatPrice" to trip.seatPrice,
                    "cargoPrice" to trip.cargoPrice,
                    "status" to "pending", // el conductor la confirmará en una etapa posterior
                    "createdAt" to System.currentTimeMillis()
                )
            )
            true
        }.await()

        bookingRef.id
    }

    /** Reservas hechas por el cliente con sesión, de la más reciente a la más antigua. */
    suspend fun loadMyBookings(): Result<List<Booking>> = runCatching {
        val uid = auth.currentUser?.uid
            ?: throw TripException("Inicia sesión para ver tus reservas.")
        db.collection(BOOKINGS)
            .whereEqualTo("clientUid", uid)
            .get()
            .await()
            .documents
            .mapNotNull { it.toBooking() }
            .sortedByDescending { it.createdAt }
    }

    /** Reservas de un viaje del conductor con sesión (para su manifiesto). */
    suspend fun loadTripBookings(tripId: String): Result<List<Booking>> = runCatching {
        val uid = auth.currentUser?.uid
            ?: throw TripException("Inicia sesión para ver las reservas.")
        db.collection(BOOKINGS)
            .whereEqualTo("tripDriverUid", uid)
            .whereEqualTo("tripId", tripId)
            .get()
            .await()
            .documents
            .mapNotNull { it.toBooking() }
            .sortedBy { it.createdAt }
    }

    private fun DocumentSnapshot.toBooking(): Booking? = runCatching {
        Booking(
            id = id,
            tripId = getString("tripId").orEmpty(),
            clientName = getString("clientName").orEmpty(),
            seats = getLong("seats")?.toInt() ?: 0,
            cargoKg = getLong("cargoKg")?.toInt() ?: 0,
            origin = getString("origin").orEmpty(),
            destination = getString("destination").orEmpty(),
            date = getString("date").orEmpty(),
            departure = getString("departure").orEmpty(),
            seatPrice = getString("seatPrice") ?: "-",
            cargoPrice = getString("cargoPrice") ?: "-",
            vehicle = getString("vehicle").orEmpty(),
            plate = getString("plate").orEmpty(),
            driverName = getString("driverName").orEmpty(),
            status = getString("status") ?: "pending",
            createdAt = getLong("createdAt") ?: 0L
        )
    }.getOrNull()
}