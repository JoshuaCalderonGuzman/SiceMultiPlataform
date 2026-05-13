package com.example.sicemultiplataform.data

import kotlinx.serialization.Serializable
import kotlinx.serialization.json.Json
import kotlinx.serialization.json.jsonArray
import kotlinx.serialization.json.jsonObject
import kotlinx.serialization.json.jsonPrimitive
import kotlinx.serialization.json.intOrNull

@Serializable
data class MateriaCarga(
    val materia: String,
    val grupo: String,
    val docente: String,
    val clvOficial: String,
    val estadoMateria: String,
    val creditos: Int,
    val observaciones: String,
    val semipresencial: String,
    val lunes: String,
    val martes: String,
    val miercoles: String,
    val jueves: String,
    val viernes: String,
    val sabado: String
) {
    fun obtenerHorarioPorDia(dia: String): String {
        return when (dia) {
            "Lunes"     -> lunes
            "Martes"    -> martes
            "Miercoles" -> miercoles
            "Jueves"    -> jueves
            "Viernes"   -> viernes
            "Sabado"    -> sabado
            else        -> ""
        }
    }
}

fun parseCargaAcademica(xml: String): List<MateriaCarga> {

    val jsonString = xml
        .substringAfter("<getCargaAcademicaByAlumnoResult>")
        .substringBefore("</getCargaAcademicaByAlumnoResult>")
        .trim()

    val jsonArray = Json.parseToJsonElement(jsonString).jsonArray

    return jsonArray.map { element ->
        val obj = element.jsonObject
        MateriaCarga(
            materia       = obj["Materia"]?.jsonPrimitive?.content ?: "",
            grupo         = obj["Grupo"]?.jsonPrimitive?.content ?: "",
            docente       = obj["Docente"]?.jsonPrimitive?.content ?: "",
            clvOficial    = obj["clvOficial"]?.jsonPrimitive?.content ?: "",
            estadoMateria = obj["EstadoMateria"]?.jsonPrimitive?.content ?: "",
            creditos      = obj["CreditosMateria"]?.jsonPrimitive?.intOrNull ?: 0,
            observaciones = obj["Observaciones"]?.jsonPrimitive?.content ?: "",
            semipresencial = obj["Semipresencial"]?.jsonPrimitive?.content ?: "",
            lunes         = obj["Lunes"]?.jsonPrimitive?.content ?: "",
            martes        = obj["Martes"]?.jsonPrimitive?.content ?: "",
            miercoles     = obj["Miercoles"]?.jsonPrimitive?.content ?: "",
            jueves        = obj["Jueves"]?.jsonPrimitive?.content ?: "",
            viernes       = obj["Viernes"]?.jsonPrimitive?.content ?: "",
            sabado        = obj["Sabado"]?.jsonPrimitive?.content ?: ""
        )
    }
}