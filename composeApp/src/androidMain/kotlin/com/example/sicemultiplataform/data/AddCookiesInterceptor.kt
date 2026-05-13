package com.example.sicemultiplataform.data

import android.content.Context
import okhttp3.Interceptor
import okhttp3.Response
import java.io.IOException


class AddCookiesInterceptor(
    private val context: Context
) : Interceptor {
    @Throws(IOException::class)
    override fun intercept(chain: Interceptor.Chain): Response {
        val builder = chain.request().newBuilder()
        val cookies = context
            .getSharedPreferences("CookiePrefs", Context.MODE_PRIVATE)
            .getStringSet("cookies", emptySet()) ?: emptySet()

        for (cookie in cookies) {
            builder.addHeader("Cookie", cookie)
        }
        return chain.proceed(builder.build())
    }


}