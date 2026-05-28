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
            context.getSharedPreferences("CookiePrefs", Context.MODE_PRIVATE)
                .edit()
                .putStringSet("cookies", setCookieHeaders.toSet())
                .apply()
        }

        return originalResponse
    }
}