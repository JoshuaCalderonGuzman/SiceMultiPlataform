package com.example.sicemultiplataform.data

import com.example.sicemultiplataform.network.SICENETWService
import io.ktor.client.statement.bodyAsText

interface SNRepository {
    suspend fun acceso(m: String, p: String): LoginResult
    suspend fun alumnoDatos(): Alumno
    suspend fun kardex(lineamiento: Int): KardexCompleto
    suspend fun califFinal(lineamiento: Int): List<CalificacionFinal>
    suspend fun califUnidades(): List<MateriaUnidades>
    suspend fun cargaAcademica(): List<MateriaCarga>
    suspend fun logoutSession()
}

class NetworkSNRepository(
    private val snApiService: SICENETWService,
    private val onClearCookies: () -> Unit = {}
) : SNRepository {

    override suspend fun acceso(m: String, p: String): LoginResult {
        logoutSession()

        snApiService.con()
        val xml = snApiService.acceso(m, p)
        logXML(xml, "Login")

        if (!xml.contains("<accesoLoginResult>")) {
            return LoginResult(false, "Error de conexión")
        }

        val result = xml
            .substringAfter("<accesoLoginResult>")
            .substringBefore("</accesoLoginResult>")
            .trim()

        return if (result.contains("\"acceso\":true")) {
            LoginResult(true, "Login correcto")
        } else {
            LoginResult(false, "Usuario o contraseña incorrectos")
        }
    }

    override suspend fun logoutSession() {
        snApiService.clearCookies()
    }

    override suspend fun alumnoDatos(): Alumno {
        val xml = snApiService.alumnoDatos()
        logXML(xml, "AlumnoDatos")
        return parseAlumno(xml)
    }

    override suspend fun kardex(lineamiento: Int): KardexCompleto {
        return try {
            val xml = snApiService.cardex(lineamiento)
            logXML(xml, "KARDEX_SUCCESS")
            parseKardex(xml)
        } catch (e: Exception) {
            println("KARDEX_ERROR: ${e.message}")
            KardexCompleto(emptyList(), KardexResumen(0.0, 0, 0, 0.0))
        }
    }

    override suspend fun califFinal(lineamiento: Int): List<CalificacionFinal> {
        return try {
            val xml = snApiService.califFinal(lineamiento)
            logXML(xml, "CALIFFINAL_SUCCESS")
            parseCalifFinal(xml)
        } catch (e: Exception) {
            println("CALIFFINAL_ERROR: ${e.message}")
            emptyList()
        }
    }

    override suspend fun califUnidades(): List<MateriaUnidades> {
        return try {
            val xml = snApiService.califUnidades()
            logXML(xml, "CALIFUNIDADES_SUCCESS")
            parseUnidades(xml)
        } catch (e: Exception) {
            println("CALIFUNIDADES_ERROR: ${e.message}")
            emptyList()
        }
    }

    override suspend fun cargaAcademica(): List<MateriaCarga> {
        return try {
            val xml = snApiService.cargaAcademica()
            logXML(xml, "CARGAACADEMICA_SUCCESS")
            parseCargaAcademica(xml)
        } catch (e: Exception) {
            println("CARGAACADEMICA_ERROR: ${e.message}")
            emptyList()
        }
    }

    private fun logXML(xml: String, tag: String = "XML_DEBUG") {
        val maxLogSize = 1000
        for (i in xml.indices step maxLogSize) {
            println("###$tag: ${xml.substring(i, minOf(i + maxLogSize, xml.length))}")
        }
    }
}