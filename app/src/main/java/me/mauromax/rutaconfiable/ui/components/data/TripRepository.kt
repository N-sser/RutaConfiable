package me.mauromax.rutaconfiable.ui.components.data

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.DocumentSnapshot
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.FirebaseFirestoreException
import com.google.firebase.firestore.ListenerRegistration
import com.google.firebase.firestore.Query
import kotlinx.coroutines.tasks.await
import me.mauromax.rutaconfiable.ui.screens.AvailableTrip
import java.security.SecureRandom

/** Error con un mensaje ya listo para mostrar al usuario. */
class TripException(message: String) : Exception(message)

/**
 * Viajes publicados, guardados en Firestore (colección "trips").
 * El id del documento es el código visible del viaje (ej. VJ-7K3M9Q).
 * [trips] se mantiene al día con un listener en tiempo real mientras se está escuchando.
 */
object TripRepository {
    private const val COLLECTION = "trips"
    private const val CODE_PREFIX = "VJ-"
    private const val CODE_ALPHABET = "ABCDEFGHJKLMNPQRSTUVWXYZ23456789"
    private const val CODE_LENGTH = 6
    private const val MAX_TRIPS = 100L

    private val db get() = FirebaseFirestore.getInstance()
    private val auth get() = FirebaseAuth.getInstance()
    private val random = SecureRandom()
    private var registration: ListenerRegistration? = null

    val trips = mutableStateListOf<AvailableTrip>()

    var isLoading by mutableStateOf(false)
        private set

    var loadError by mutableStateOf<String?>(null)
        private set

    /** Empieza a escuchar los viajes en tiempo real. Si ya está escuchando, no hace nada. */
    fun startListening() {
        if (registration != null) return
        isLoading = true
        registration = db.collection(COLLECTION)
            .orderBy("createdAt", Query.Direction.DESCENDING)
            .limit(MAX_TRIPS)
            .addSnapshotListener { snapshot, error ->
                isLoading = false
                if (error != null) {
                    loadError = friendlyError(error)
                    return@addSnapshotListener
                }
                loadError = null
                val list = snapshot?.documents?.mapNotNull { it.toTrip() }.orEmpty()
                trips.clear()
                trips.addAll(list)
            }
    }

    fun stopListening() {
        registration?.remove()
        registration = null
        isLoading = false
    }

    /**
     * Publica un viaje del conductor con sesión iniciada. El código del viaje lo genera
     * el repositorio (se ignora [AvailableTrip.id]) y el nombre del conductor se toma de su
     * perfil. Devuelve el viaje ya con su código definitivo.
     */
    suspend fun publish(trip: AvailableTrip): Result<AvailableTrip> = runCatching {
        val uid = auth.currentUser?.uid
            ?: throw TripException("Inicia sesión para publicar un viaje.")

        val driverName = runCatching {
            db.collection("users").document(uid).get().await().getString("name")
        }.getOrNull()?.takeIf { it.isNotBlank() } ?: trip.driver

        repeat(5) {
            val code = newCode()
            val ref = db.collection(COLLECTION).document(code)
            // La transacción evita pisar un viaje existente si el código se repite.
            val created = db.runTransaction<Boolean> { tx ->
                if (tx.get(ref).exists()) {
                    false
                } else {
                    tx.set(ref, trip.toMap(uid, driverName))
                    true
                }
            }.await()
            if (created) return@runCatching trip.copy(id = code, driver = driverName)
        }
        throw TripException("No se pudo generar el código del viaje. Intenta de nuevo.")
    }

    /** Viajes publicados por el usuario con sesión iniciada, del más reciente al más antiguo. */
    suspend fun loadMyTrips(): Result<List<AvailableTrip>> = runCatching {
        val uid = auth.currentUser?.uid
            ?: throw TripException("Inicia sesión para ver tus viajes.")
        db.collection(COLLECTION)
            .whereEqualTo("driverUid", uid)
            .get()
            .await()
            .documents
            .mapNotNull { it.toTrip() }
            .sortedByDescending { it.createdAt }
    }

    fun friendlyError(e: Throwable): String = when {
        e is TripException -> e.message ?: "No se pudo completar. Intenta de nuevo."
        e is FirebaseFirestoreException && e.code == FirebaseFirestoreException.Code.UNAVAILABLE ->
            "Sin conexión a internet."
        e is FirebaseFirestoreException && e.code == FirebaseFirestoreException.Code.PERMISSION_DENIED ->
            "No tienes permiso para esta acción."
        else -> "No se pudo completar. Intenta de nuevo."
    }

    private fun newCode(): String = CODE_PREFIX + String(
        CharArray(CODE_LENGTH) { CODE_ALPHABET[random.nextInt(CODE_ALPHABET.length)] }
    )

    private fun AvailableTrip.toMap(driverUid: String, driverName: String): Map<String, Any> = mapOf(
        "driverUid" to driverUid,
        "driver" to driverName,
        "departure" to departure,
        "rating" to rating,
        "verifiedTrips" to verifiedTrips,
        "vehicle" to vehicle,
        "vehicleDetail" to vehicleDetail,
        "plate" to plate,
        "cargoFreeKg" to cargoFreeKg,
        "cargoTotalKg" to cargoTotalKg,
        "cargoNote" to cargoNote,
        "seatsFree" to seatsFree,
        "seatsTotal" to seatsTotal,
        "seatsNote" to seatsNote,
        "cargoPrice" to cargoPrice,
        "seatPrice" to seatPrice,
        "distance" to distance,
        "duration" to duration,
        "road" to road,
        "origin" to origin,
        "destination" to destination,
        "date" to date,
        "hasCargo" to hasCargo,
        "hasSeats" to hasSeats,
        "createdAt" to System.currentTimeMillis()
    )

    private fun DocumentSnapshot.toTrip(): AvailableTrip? = runCatching {
        AvailableTrip(
            id = id,
            departure = getString("departure").orEmpty(),
            driver = getString("driver").orEmpty(),
            rating = getDouble("rating") ?: 0.0,
            verifiedTrips = getLong("verifiedTrips")?.toInt() ?: 0,
            vehicle = getString("vehicle").orEmpty(),
            vehicleDetail = getString("vehicleDetail").orEmpty(),
            plate = getString("plate").orEmpty(),
            cargoFreeKg = getLong("cargoFreeKg")?.toInt() ?: 0,
            cargoTotalKg = getLong("cargoTotalKg")?.toInt() ?: 1,
            cargoNote = getString("cargoNote").orEmpty(),
            seatsFree = getLong("seatsFree")?.toInt() ?: 0,
            seatsTotal = getLong("seatsTotal")?.toInt() ?: 0,
            seatsNote = getString("seatsNote").orEmpty(),
            cargoPrice = getString("cargoPrice") ?: "-",
            seatPrice = getString("seatPrice") ?: "-",
            distance = getString("distance") ?: "—",
            duration = getString("duration") ?: "—",
            road = getString("road").orEmpty(),
            origin = getString("origin").orEmpty(),
            destination = getString("destination").orEmpty(),
            date = getString("date").orEmpty(),
            hasCargo = getBoolean("hasCargo") ?: true,
            hasSeats = getBoolean("hasSeats") ?: true,
            driverUid = getString("driverUid").orEmpty(),
            createdAt = getLong("createdAt") ?: 0L
        )
    }.getOrNull()
}