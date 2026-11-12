package me.mauromax.rutaconfiable.ui.components.data

import com.google.firebase.FirebaseNetworkException
import com.google.firebase.FirebaseTooManyRequestsException
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

    /**
     * Crea la cuenta en Firebase Auth y guarda el perfil en Firestore (users/{uid}).
     * [profile] trae los datos extra del formulario (teléfono, documento, vehículo, etc.).
     * Nunca se guarda la contraseña. Los campos base (name, email, role, verified, createdAt)
     * van después de [profile], así que este no puede sobrescribirlos.
     * Si falla el guardado en Firestore, se elimina la cuenta recién creada para no dejar
     * un usuario de Auth sin perfil (y poder reintentar con el mismo correo).
     */
    suspend fun register(
        email: String,
        password: String,
        name: String,
        role: String, // "driver" o "client"
        profile: Map<String, Any?> = emptyMap()
    ): Result<Unit> = runCatching {
        val result = auth.createUserWithEmailAndPassword(email, password).await()
        val user = result.user!!
        try {
            db.collection("users").document(user.uid).set(
                profile + mapOf(
                    "name" to name,
                    "email" to email,
                    "role" to role,
                    "verified" to false,
                    "createdAt" to System.currentTimeMillis()
                )
            ).await()
        } catch (e: Exception) {
            runCatching { user.delete().await() }
            throw e
        }
        Unit
    }

    /**
     * Rol guardado en Firestore para el usuario actual ("driver" o "client").
     * Devuelve null si no hay sesión, si falla la lectura o si el documento no tiene rol,
     * para no asumir un rol por defecto.
     */
    suspend fun getRole(): String? = runCatching {
        val uid = auth.currentUser?.uid ?: return@runCatching null
        db.collection("users").document(uid).get().await().getString("role")
    }.getOrNull()

    /** Nombre guardado en el perfil del usuario actual, o null si no se puede leer. */
    suspend fun getName(): String? = runCatching {
        val uid = auth.currentUser?.uid ?: return@runCatching null
        db.collection("users").document(uid).get().await().getString("name")
    }.getOrNull()?.takeIf { it.isNotBlank() }

    /**
     * Envía el correo con el enlace para restablecer la contraseña.
     * Si el correo no está registrado se trata como éxito, para no revelar qué cuentas existen.
     */
    suspend fun sendPasswordReset(email: String): Result<Unit> = runCatching {
        auth.sendPasswordResetEmail(email).await()
        Unit
    }.recoverCatching { e ->
        if (e is FirebaseAuthInvalidUserException) Unit else throw e
    }

    fun logout() = auth.signOut()

    fun friendlyError(e: Throwable): String = when (e) {
        is FirebaseAuthUserCollisionException -> "Ese correo ya tiene una cuenta."
        is FirebaseAuthWeakPasswordException -> "La contraseña es muy débil."
        is FirebaseAuthInvalidCredentialsException,
        is FirebaseAuthInvalidUserException -> "Correo o contraseña incorrectos."
        is FirebaseNetworkException -> "Sin conexión a internet."
        is FirebaseTooManyRequestsException -> "Demasiados intentos. Espera un momento e intenta de nuevo."
        else -> "No se pudo completar. Intenta de nuevo."
    }
}