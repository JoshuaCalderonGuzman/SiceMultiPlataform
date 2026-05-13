package com.example.sicemultiplataform.network

import io.ktor.client.HttpClient
import io.ktor.client.request.get
import io.ktor.client.request.header
import io.ktor.client.request.post
import io.ktor.client.request.setBody
import io.ktor.client.statement.bodyAsText
import io.ktor.http.ContentType
import io.ktor.http.contentType

private const val BASE_URL = "https://sicenet.surguanajuato.tecnm.mx"
private const val ENDPOINT = "/ws/wsalumnos.asmx"

// SOAP bodies — igual que antes, sin cambios
private val bodyAcceso = """
    <?xml version="1.0" encoding="utf-8"?>
    <soap:Envelope xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance" xmlns:xsd="http://www.w3.org/2001/XMLSchema" xmlns:soap="http://schemas.xmlsoap.org/soap/envelope/">
      <soap:Body>
        <accesoLogin xmlns="http://tempuri.org/">
          <strMatricula>%s</strMatricula>
          <strContrasenia>%s</strContrasenia>
          <tipoUsuario>ALUMNO</tipoUsuario>
        </accesoLogin>
      </soap:Body>
    </soap:Envelope>
""".trimIndent()

private val bodyDatos = """
    <?xml version="1.0" encoding="utf-8"?>
    <soap:Envelope xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance" xmlns:xsd="http://www.w3.org/2001/XMLSchema" xmlns:soap="http://schemas.xmlsoap.org/soap/envelope/">
      <soap:Body>
        <getAlumnoAcademicoWithLineamiento xmlns="http://tempuri.org/" />
      </soap:Body>
    </soap:Envelope>
""".trimIndent()

private val bodyCalifFinal = """
    <?xml version="1.0" encoding="utf-8"?>
    <soap:Envelope xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance" xmlns:xsd="http://www.w3.org/2001/XMLSchema" xmlns:soap="http://schemas.xmlsoap.org/soap/envelope/">
      <soap:Body>
        <getAllCalifFinalByAlumnos xmlns="http://tempuri.org/">
          <bytModEducativo>%s</bytModEducativo>
        </getAllCalifFinalByAlumnos>
      </soap:Body>
    </soap:Envelope>
""".trimIndent()

private val bodyCalifUnidades = """
    <?xml version="1.0" encoding="utf-8"?>
    <soap:Envelope xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance" xmlns:xsd="http://www.w3.org/2001/XMLSchema" xmlns:soap="http://schemas.xmlsoap.org/soap/envelope/">
      <soap:Body>
        <getCalifUnidadesByAlumno xmlns="http://tempuri.org/" />
      </soap:Body>
    </soap:Envelope>
""".trimIndent()

private val bodyCardex = """
    <?xml version="1.0" encoding="utf-8"?>
    <soap:Envelope xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance" xmlns:xsd="http://www.w3.org/2001/XMLSchema" xmlns:soap="http://schemas.xmlsoap.org/soap/envelope/">
      <soap:Body>
        <getAllKardexConPromedioByAlumno xmlns="http://tempuri.org/">
          <aluLineamiento>%s</aluLineamiento>
        </getAllKardexConPromedioByAlumno>
      </soap:Body>
    </soap:Envelope>
""".trimIndent()

private val bodyCargaAcademica = """
    <?xml version="1.0" encoding="utf-8"?>
    <soap:Envelope xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance" xmlns:xsd="http://www.w3.org/2001/XMLSchema" xmlns:soap="http://schemas.xmlsoap.org/soap/envelope/">
      <soap:Body>
        <getCargaAcademicaByAlumno xmlns="http://tempuri.org/" />
      </soap:Body>
    </soap:Envelope>
""".trimIndent()

class SICENETWService(private val client: HttpClient) {

    // Reemplaza @GET("/")
    suspend fun con() {
        client.get(BASE_URL)
    }

    // Reemplaza clearCookies — Ktor maneja cookies por sesión del cliente
    fun clearCookies() {
        // Las cookies se limpian creando un nuevo HttpClient en AppContainer
        // o usando un CookieStorage mutable
    }

    // Reemplaza @POST con SOAPAction: accesoLogin
    suspend fun acceso(m: String, p: String): String =
        client.post("$BASE_URL$ENDPOINT") {
            contentType(ContentType.Text.Xml)
            header("SOAPAction", "http://tempuri.org/accesoLogin")
            setBody(bodyAcceso.format(m, p))
        }.bodyAsText()

    // Reemplaza @POST con SOAPAction: getAlumnoAcademicoWithLineamiento
    suspend fun alumnoDatos(): String =
        client.post("$BASE_URL$ENDPOINT") {
            contentType(ContentType.Text.Xml)
            header("SOAPAction", "http://tempuri.org/getAlumnoAcademicoWithLineamiento")
            setBody(bodyDatos)
        }.bodyAsText()

    // Reemplaza @POST con SOAPAction: getAllCalifFinalByAlumnos
    suspend fun califFinal(lineamiento: Int): String =
        client.post("$BASE_URL$ENDPOINT") {
            contentType(ContentType.Text.Xml)
            header("SOAPAction", "http://tempuri.org/getAllCalifFinalByAlumnos")
            setBody(bodyCalifFinal.format(lineamiento.toString()))
        }.bodyAsText()

    // Reemplaza @POST con SOAPAction: getCalifUnidadesByAlumno
    suspend fun califUnidades(): String =
        client.post("$BASE_URL$ENDPOINT") {
            contentType(ContentType.Text.Xml)
            header("SOAPAction", "http://tempuri.org/getCalifUnidadesByAlumno")
            setBody(bodyCalifUnidades)
        }.bodyAsText()

    // Reemplaza @POST con SOAPAction: getAllKardexConPromedioByAlumno
    suspend fun cardex(lineamiento: Int): String =
        client.post("$BASE_URL$ENDPOINT") {
            contentType(ContentType.Text.Xml)
            header("SOAPAction", "http://tempuri.org/getAllKardexConPromedioByAlumno")
            setBody(bodyCardex.format(lineamiento.toString()))
        }.bodyAsText()

    // Reemplaza @POST con SOAPAction: getCargaAcademicaByAlumno
    suspend fun cargaAcademica(): String =
        client.post("$BASE_URL$ENDPOINT") {
            contentType(ContentType.Text.Xml)
            header("SOAPAction", "http://tempuri.org/getCargaAcademicaByAlumno")
            setBody(bodyCargaAcademica)
        }.bodyAsText()
}