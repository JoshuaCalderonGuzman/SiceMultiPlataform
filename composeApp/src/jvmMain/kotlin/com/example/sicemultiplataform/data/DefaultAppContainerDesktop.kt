package com.example.sicemultiplataform.data

import com.example.sicemultiplataform.data.local.DatabaseDriverFactory
import com.example.sicemultiplataform.data.local.createDatabase
import com.example.sicemultiplataform.data.local.dao.*
import com.example.sicemultiplataform.data.repository.LocalRepository
import com.example.sicemultiplataform.network.SICENETWService
import io.ktor.client.HttpClient
import io.ktor.client.plugins.HttpRedirect
import io.ktor.client.plugins.cookies.HttpCookies
import io.ktor.client.plugins.logging.LogLevel
import io.ktor.client.plugins.logging.Logging

class DefaultAppContainerDesktop(
    driverFactory: DatabaseDriverFactory
) : AppContainer {

    private val httpClient = HttpClient {
        install(HttpCookies)
        install(HttpRedirect) {
            checkHttpMethod = false
        }
        install(Logging) { level = LogLevel.HEADERS }
    }

    private val snService = SICENETWService(httpClient)

    override val snRepository: SNRepository by lazy {
        NetworkSNRepository(snApiService = snService)
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