package me.mauromax.rutaconfiable.ui.components.data

import androidx.compose.runtime.mutableStateListOf
import me.mauromax.rutaconfiable.ui.screens.AvailableTrip

/** Fuente única de viajes publicados. Luego se reemplaza por Firestore. */
object TripRepository {
    val trips = mutableStateListOf<AvailableTrip>()

    fun publish(trip: AvailableTrip) {
        trips.add(0, trip)
    }

    fun nextId(): String = "VJ-%04d".format(trips.size + 1)
}