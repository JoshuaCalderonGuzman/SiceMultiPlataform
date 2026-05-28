package com.example.sicemultiplataform.data

import android.content.Context
import okhttp3.Interceptor
import okhttp3.Response
import java.io.IOException


// Almacenamiento de cookies en archivo para que sobrevivan entre sesiones
class AddCookiesInterceptor(
    private val context: Context
) : Interceptor {
    @Throws(IOException::class)
    override fun intercept(chain: Interceptor.Chain): Response {
        val builder = chain.request().newBuilder()
        val cookies = context
            .getSharedPreferences("CookiePrefs", Context.MODE_PRIVATE)
            .getStringSet("cookies", emptySet()) ?: emptySet()

        println("🍪 ENVIANDO COOKIES: ${cookies.size} cookies para ${chain.request().url}")
        for (cookie in cookies) {
            println("🍪 COOKIE: $cookie")
            builder.addHeader("Cookie", cookie)
        }
        return chain.proceed(builder.build())
    }


}