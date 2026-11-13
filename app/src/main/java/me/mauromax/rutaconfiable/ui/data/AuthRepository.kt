package me.mauromax.rutaconfiable.ui.data

import com.google.firebase.FirebaseNetworkException
import com.google.firebase.FirebaseTooManyRequestsException
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseAuthInvalidCredentialsException
import com.google.firebase.auth.FirebaseAuthInvalidUserException
import com.google.firebase.auth.FirebaseAuthUserCollisionException
import com.google.firebase.auth.FirebaseAuthWeakPasswordException
import com.google.firebase.auth.GoogleAuthProvider
import com.google.firebase.firestore.DocumentSnapshot
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.Source
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow
import kotlinx.coroutines.tasks.await

/** Estado de la revisión de identidad de una cuenta. */
enum class VerificationStatus { PENDING, APPROVED, REJECTED }

/**
 * Rol y estado de verificación de la cuenta, leídos de users/{uid}.
 * Solo un administrador puede cambiar la verificación (ver firestore.rules y admin/).
 */
data class AccountStatus(
    val role: String?,
    val verification: VerificationStatus,
    val rejectionReason: String? = null
) {
    val isVerified: Boolean get() = verification == VerificationStatus.APPROVED
}

private fun DocumentSnapshot.toAccountStatus(): AccountStatus {
    val verification = when {
        getBoolean("verified") == true -> VerificationStatus.APPROVED
        getString("verificationStatus") == "rejected" -> VerificationStatus.REJECTED
        else -> VerificationStatus.PENDING
    }
    return AccountStatus(
        role = getString("role"),
        verification = verification,
        rejectionReason = getString("rejectionReason")?.takeIf { it.isNotBlank() }
    )
}

object AuthRepository {
    private val auth get() = FirebaseAuth.getInstance()
    private val db get() = FirebaseFirestore.getInstance()

    val isLoggedIn: Boolean get() = auth.currentUser != null

    suspend fun login(email: String, password: String): Result<Unit> = runCatching {
        auth.signInWithEmailAndPassword(email, password).await()
        Unit
    }

    /** Inicia sesión en Firebase con el ID token que entregó Google (crea la cuenta si es nueva). */
    suspend fun signInWithGoogle(idToken: String): Result<Unit> = runCatching {
        auth.signInWithCredential(GoogleAuthProvider.getCredential(idToken, null)).await()
        Unit
    }

    /** Si el usuario actual ya tiene perfil en Firestore (users/{uid}). Falla si no se puede leer. */
    suspend fun hasProfile(): Result<Boolean> = runCatching {
        val uid = auth.currentUser?.uid ?: error("Sin sesión")
        db.collection("users").document(uid).get().await().exists()
    }

    /**
     * Crea el perfil de una cuenta que entró con Google por primera vez, con el rol elegido.
     * Igual que en el registro normal, nace sin verificar y pendiente de revisión.
     * Nota: no trae documento, licencia ni placa; esos datos solo los pide el registro con correo.
     */
    suspend fun createGoogleProfile(role: String): Result<Unit> = runCatching {
        val user = auth.currentUser ?: error("Sin sesión")
        db.collection("users").document(user.uid).set(
            mapOf(
                "name" to (user.displayName?.takeIf { it.isNotBlank() } ?: user.email ?: "Usuario"),
                "email" to user.email.orEmpty(),
                "role" to role,
                "verified" to false,
                "verificationStatus" to "pending",
                "authProvider" to "google",
                "createdAt" to System.currentTimeMillis()
            )
        ).await()
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
                    "verificationStatus" to "pending",
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

    /**
     * Rol y estado de verificación del usuario actual. Con [fromServer] = true se fuerza la
     * lectura al servidor (no a la caché), para ver una aprobación reciente.
     * Devuelve null si no hay sesión, si falla la lectura (p. ej. sin conexión) o si no existe
     * el perfil. El rol puede venir null si el documento no lo tiene.
     */
    suspend fun getAccountStatus(fromServer: Boolean = false): AccountStatus? = runCatching {
        val uid = auth.currentUser?.uid ?: return@runCatching null
        val ref = db.collection("users").document(uid)
        val doc = if (fromServer) ref.get(Source.SERVER).await() else ref.get().await()
        if (doc.exists()) doc.toAccountStatus() else null
    }.getOrNull()

    /**
     * Escucha en tiempo real el estado del usuario actual, para que la pantalla de
     * verificación avance sola cuando un administrador apruebe la cuenta.
     * Los errores transitorios (sin conexión) se ignoran; el listener sigue activo.
     */
    fun observeAccountStatus(): Flow<AccountStatus> = callbackFlow {
        val uid = auth.currentUser?.uid
        if (uid == null) {
            close()
            return@callbackFlow
        }
        val registration = db.collection("users").document(uid)
            .addSnapshotListener { snapshot, error ->
                if (error == null && snapshot != null && snapshot.exists()) {
                    trySend(snapshot.toAccountStatus())
                }
            }
        awaitClose { registration.remove() }
    }

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