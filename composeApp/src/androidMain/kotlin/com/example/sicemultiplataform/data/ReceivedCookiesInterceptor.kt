package com.example.sicemultiplataform.data

import android.content.Context
import androidx.core.content.edit
import okhttp3.Interceptor
import okhttp3.Response
import java.io.IOException

class ReceivedCookiesInterceptor(
    private val context: Context
) : Interceptor {

    @Throws(IOException::class)
    override fun intercept(chain: Interceptor.Chain): Response {
        val originalResponse = chain.proceed(chain.request())

        val setCookieHeaders = originalResponse.headers("Set-Cookie")

        if (setCookieHeaders.isNotEmpty()) {
            val prefs = context.getSharedPreferences("CookiePrefs", Context.MODE_PRIVATE)

            // Cargar cookies existentes como mapa nombre → valor completo
            val cookieMap = prefs.getStringSet("cookies", emptySet())
                ?.associateBy { it.substringBefore("=") }
                ?.toMutableMap()
                ?: mutableMapOf()

            // Actualizar solo las cookies que el servidor manda
            for (cookie in setCookieHeaders) {
                val nombre = cookie.substringBefore("=")
                cookieMap[nombre] = cookie
                println("🍪 ACTUALIZADA: $nombre")
            }

            prefs.edit()
                .putStringSet("cookies", cookieMap.values.toSet())
                .apply()
        }

        return originalResponse
    }
}