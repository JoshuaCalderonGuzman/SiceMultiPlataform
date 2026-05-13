package com.example.sicemultiplataform.data.segurity

import android.content.Context
import androidx.security.crypto.EncryptedSharedPreferences
import androidx.security.crypto.MasterKey
import androidx.core.content.edit

class SecureSessionManager(context: Context) {

    private val masterKey = MasterKey.Builder(context)
        .setKeyScheme(MasterKey.KeyScheme.AES256_GCM)
        .build()

    private val securePrefs = EncryptedSharedPreferences.create(
        context,
        "secure_user_session",
        masterKey,
        EncryptedSharedPreferences.PrefKeyEncryptionScheme.AES256_SIV,
        EncryptedSharedPreferences.PrefValueEncryptionScheme.AES256_GCM
    )

    fun guardarSesion(matricula: String, password: String) {
        securePrefs.edit {
            putString("matricula", matricula)
                .putString("password", password)
        }
    }

    fun obtenerMatricula(): String? {
        return securePrefs.getString("matricula", null)
    }

    fun obtenerPassword(): String? {
        return securePrefs.getString("password", null)
    }

    fun cerrarSesion() {
        securePrefs.edit { clear() }
    }
}