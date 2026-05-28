package com.example.sicemultiplataform.data.segurity

import android.content.Context
import androidx.security.crypto.EncryptedSharedPreferences
import androidx.security.crypto.MasterKey
import androidx.core.content.edit

actual class SecureSessionManager(private val context: Context) {


    private val masterKey = MasterKey.Builder(context)
        .setKeyScheme(MasterKey.KeyScheme.AES256_GCM)
        .build()

    private val securePrefs = try {
        EncryptedSharedPreferences.create(
            context,
            "secure_user_session",
            masterKey,
            EncryptedSharedPreferences.PrefKeyEncryptionScheme.AES256_SIV,
            EncryptedSharedPreferences.PrefValueEncryptionScheme.AES256_GCM
        )
    } catch (e: Exception) {
        // Las llaves del KeyStore no coinciden con los datos guardados
        context.deleteSharedPreferences("secure_user_session")
        EncryptedSharedPreferences.create(
            context,
            "secure_user_session",
            masterKey,
            EncryptedSharedPreferences.PrefKeyEncryptionScheme.AES256_SIV,
            EncryptedSharedPreferences.PrefValueEncryptionScheme.AES256_GCM
        )
    }

    actual fun guardarSesion(matricula: String, password: String) {
        securePrefs.edit {
            putString("matricula", matricula)
            putString("password", password)
        }
    }

    actual fun obtenerMatricula(): String? = securePrefs.getString("matricula", null)
    actual fun obtenerPassword(): String?  = securePrefs.getString("password", null)

    actual fun cerrarSesion() {
        securePrefs.edit { clear() }
    }
}