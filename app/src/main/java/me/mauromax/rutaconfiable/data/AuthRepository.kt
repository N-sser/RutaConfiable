package me.mauromax.rutaconfiable.data

import com.google.firebase.FirebaseNetworkException
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseAuthInvalidCredentialsException
import com.google.firebase.auth.FirebaseAuthInvalidUserException
import com.google.firebase.auth.FirebaseAuthUserCollisionException
import com.google.firebase.auth.FirebaseAuthWeakPasswordException
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.tasks.await

object AuthRepository {
    private val auth get() = FirebaseAuth.getInstance()
    private val db get() = FirebaseFirestore.getInstance()

    val isLoggedIn: Boolean get() = auth.currentUser != null

    suspend fun login(email: String, password: String): Result<Unit> = runCatching {
        auth.signInWithEmailAndPassword(email, password).await()
        Unit
    }

    suspend fun register(
        email: String,
        password: String,
        name: String,
        role: String // "driver" o "client"
    ): Result<Unit> = runCatching {
        val result = auth.createUserWithEmailAndPassword(email, password).await()
        val uid = result.user!!.uid
        db.collection("users").document(uid).set(
            mapOf(
                "name" to name,
                "email" to email,
                "role" to role,
                "verified" to false,
                "createdAt" to System.currentTimeMillis()
            )
        ).await()
        Unit
    }

    /** Rol guardado en Firestore para el usuario actual. */
    suspend fun getRole(): String = runCatching {
        val uid = auth.currentUser?.uid ?: return@runCatching "driver"
        db.collection("users").document(uid).get().await().getString("role") ?: "driver"
    }.getOrDefault("driver")

    suspend fun sendPasswordReset(email: String): Result<Unit> = runCatching {
        auth.sendPasswordResetEmail(email).await()
        Unit
    }

    fun logout() = auth.signOut()

    fun friendlyError(e: Throwable): String = when (e) {
        is FirebaseAuthUserCollisionException -> "Ese correo ya tiene una cuenta."
        is FirebaseAuthWeakPasswordException -> "La contraseña es muy débil."
        is FirebaseAuthInvalidCredentialsException,
        is FirebaseAuthInvalidUserException -> "Correo o contraseña incorrectos."
        is FirebaseNetworkException -> "Sin conexión a internet."
        else -> "No se pudo completar. Intenta de nuevo."
    }
}
