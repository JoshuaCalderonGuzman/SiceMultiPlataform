
package com.example.sicemultiplataform.data

import com.example.sicemultiplataform.data.local.DatabaseDriverFactory
import com.example.sicemultiplataform.data.local.createDatabase
import com.example.sicemultiplataform.data.local.dao.*
import com.example.sicemultiplataform.data.repository.LocalRepository
import com.example.sicemultiplataform.network.SICENETWService
import io.ktor.client.HttpClient
import io.ktor.client.plugins.HttpRedirect
import io.ktor.client.plugins.cookies.*
import io.ktor.client.plugins.logging.LogLevel
import io.ktor.client.plugins.logging.Logging
import io.ktor.http.*
import java.io.File

class DefaultAppContainerDesktop(
    driverFactory: DatabaseDriverFactory
) : AppContainer {

    // Almacenamiento de cookies en archivo para que sobrevivan entre sesiones
    private val cookieStorage = FileCookieStorage()

    private val httpClient = HttpClient {
        install(HttpCookies) {
            storage = cookieStorage
        }
        install(HttpRedirect) {
            checkHttpMethod = false
        }
        install(Logging) { level = LogLevel.HEADERS }
    }

    private val snService = SICENETWService(httpClient)

    override val snRepository: SNRepository by lazy {
        NetworkSNRepository(
            snApiService = snService,
            onClearCookies = {
                cookieStorage.clear()
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