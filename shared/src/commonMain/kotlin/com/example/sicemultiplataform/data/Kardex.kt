package com.example.sicemultiplataform.data

import kotlinx.serialization.Serializable
import kotlinx.serialization.json.Json
import kotlinx.serialization.json.jsonArray
import kotlinx.serialization.json.jsonObject
import kotlinx.serialization.json.jsonPrimitive
import kotlinx.serialization.json.intOrNull
import kotlinx.serialization.json.doubleOrNull

@Serializable
data class KardexMateria(
    val clvMat: String,
    val materia: String,
    val cdts: Int,
    val calif: Int,
    val acred: String,
    val periodo: String,
    val anio: String,
    val semestre: String
)

@Serializable
data class KardexResumen(
    val promedioGral: Double,
    val cdtsAcumulados: Int,
    val cdtsPlan: Int,
    val avance: Double
)

@Serializable
data class KardexCompleto(
    val materias: List<KardexMateria>,
    val resumen: KardexResumen
)

fun parseKardex(xml: String): KardexCompleto {

    val jsonString = xml
        .substringAfter("<getAllKardexConPromedioByAlumnoResult>")
        .substringBefore("</getAllKardexConPromedioByAlumnoResult>")
        .trim()

    val jsonRoot = Json.parseToJsonElement(jsonString).jsonObject

    // Parsear la lista de materias
    val materias = jsonRoot["lstKardex"]!!.jsonArray.map { element ->
        val obj = element.jsonObject
        KardexMateria(
            clvMat   = obj["ClvOfiMat"]?.jsonPrimitive?.content ?: "",
            materia  = obj["Materia"]?.jsonPrimitive?.content ?: "",
            cdts     = obj["Cdts"]?.jsonPrimitive?.intOrNull ?: 0,
            calif    = obj["Calif"]?.jsonPrimitive?.intOrNull ?: 0,
            acred    = obj["Acred"]?.jsonPrimitive?.content ?: "",
            periodo  = obj["P1"]?.jsonPrimitive?.content ?: "",
            anio     = obj["A1"]?.jsonPrimitive?.content ?: "",
            semestre = obj["S1"]?.jsonPrimitive?.content ?: ""
        )
    }

    // Parsear el promedio/resumen
    val promObj = jsonRoot["Promedio"]!!.jsonObject
    val resumen = KardexResumen(
        promedioGral   = promObj["PromedioGral"]?.jsonPrimitive?.doubleOrNull ?: 0.0,
        cdtsAcumulados = promObj["CdtsAcum"]?.jsonPrimitive?.intOrNull ?: 0,
        cdtsPlan       = promObj["CdtsPlan"]?.jsonPrimitive?.intOrNull ?: 0,
        avance         = promObj["AvanceCdts"]?.jsonPrimitive?.doubleOrNull ?: 0.0
    )

    return KardexCompleto(materias, resumen)
}