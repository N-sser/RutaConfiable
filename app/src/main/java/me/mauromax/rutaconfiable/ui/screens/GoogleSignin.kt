package me.mauromax.rutaconfiable.ui.screens

import android.content.Context
import androidx.credentials.CredentialManager
import androidx.credentials.CustomCredential
import androidx.credentials.GetCredentialRequest
import androidx.credentials.exceptions.GetCredentialCancellationException
import androidx.credentials.exceptions.GetCredentialException
import androidx.credentials.exceptions.NoCredentialException
import com.google.android.libraries.identity.googleid.GetSignInWithGoogleOption
import com.google.android.libraries.identity.googleid.GoogleIdTokenCredential
import com.google.android.libraries.identity.googleid.GoogleIdTokenParsingException
import me.mauromax.rutaconfiable.ui.data.AuthRepository

/** Error de Google con un mensaje ya listo para mostrar al usuario. */
class GoogleSignInException(message: String) : Exception(message)

/**
 * Pide a Android la cuenta de Google del usuario y devuelve su ID token, que luego se
 * entrega a Firebase Auth ([me.mauromax.rutaconfiable.ui.data.AuthRepository.signInWithGoogle]).
 */
object GoogleSignIn {

    /**
     * [context] debe ser el de la Activity (en Compose, LocalContext.current) para poder
     * mostrar el selector de cuentas.
     *
     * El ID de cliente web lo genera el plugin de Google Services como
     * R.string.default_web_client_id, pero SOLO si google-services.json trae un cliente OAuth
     * web (se obtiene activando Google en Firebase Auth y agregando el SHA-1 de la app).
     * Se busca por nombre para que el proyecto compile aunque aún no esté configurado.
     */
    suspend fun getIdToken(context: Context): Result<String> = runCatching {
        val resId = context.resources.getIdentifier("default_web_client_id", "string", context.packageName)
        if (resId == 0) {
            throw GoogleSignInException(
                "Google aún no está configurado en Firebase. Activa el método Google, agrega el SHA-1 " +
                        "de la app y vuelve a descargar google-services.json."
            )
        }
        val option = GetSignInWithGoogleOption.Builder(context.getString(resId)).build()
        val request = GetCredentialRequest.Builder().addCredentialOption(option).build()
        val credential = CredentialManager.create(context).getCredential(context, request).credential

        if (credential is CustomCredential &&
            credential.type == GoogleIdTokenCredential.TYPE_GOOGLE_ID_TOKEN_CREDENTIAL
        ) {
            GoogleIdTokenCredential.createFrom(credential.data).idToken
        } else {
            throw GoogleSignInException("No se pudo obtener la cuenta de Google.")
        }
    }

    /** Mensaje para mostrar, o null si el usuario simplemente canceló (no se muestra error). */
    fun friendlyError(e: Throwable): String? = when (e) {
        is GetCredentialCancellationException -> null
        is NoCredentialException ->
            "No hay una cuenta de Google en este dispositivo. Agrega una en Ajustes e intenta de nuevo."
        is GoogleSignInException -> e.message
        is GoogleIdTokenParsingException -> "No se pudo leer la cuenta de Google."
        is GetCredentialException -> "No se pudo iniciar sesión con Google. Intenta de nuevo."
        else -> AuthRepository.friendlyError(e)
    }
}