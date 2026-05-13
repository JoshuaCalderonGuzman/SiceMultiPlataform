package com.example.sicemultiplataform.data

import android.content.Context
import com.example.sicemultiplataform.data.local.DatabaseDriverFactory
import com.example.sicemultiplataform.data.local.createDatabase
import com.example.sicemultiplataform.data.local.dao.*
import com.example.sicemultiplataform.data.repository.LocalRepository
import com.example.sicemultiplataform.network.SICENETWService
import io.ktor.client.HttpClient
import io.ktor.client.engine.okhttp.OkHttp
import io.ktor.client.plugins.logging.LogLevel
import io.ktor.client.plugins.logging.Logging
import okhttp3.OkHttpClient

class DefaultAppContainer(
    driverFactory: DatabaseDriverFactory,
    private val context: Context
) : AppContainer {

    private val httpClient = HttpClient(OkHttp) {
        engine {
            preconfigured = OkHttpClient.Builder()
                .addInterceptor(AddCookiesInterceptor(context))
                .addInterceptor(ReceivedCookiesInterceptor(context))
                .build()
        }
        install(Logging) { level = LogLevel.HEADERS }
    }

    private val snService = SICENETWService(httpClient)

    override val snRepository: SNRepository by lazy {
        NetworkSNRepository(
            snApiService = snService,
            onClearCookies = {
                context.getSharedPreferences("CookiePrefs", Context.MODE_PRIVATE)
                    .edit().clear().apply()
            }
        )
    }

    override val localRepository: LocalRepository by lazy {
        val database = createDatabase(driverFactory)
        LocalRepository(
            alumnoDao        = AlumnoDao(database),
            cargaDao         = CargaDao(database),
            kardexDao        = KardexDao(database),
            califFinalDao    = CalifFinalDao(database),
            califUnidadesDao = CalifUnidadesDao(database)
        )
    }
}