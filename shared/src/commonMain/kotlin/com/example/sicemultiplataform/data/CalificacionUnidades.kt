package com.example.sicemultiplataform.data

import kotlinx.serialization.Serializable
import kotlinx.serialization.json.Json
import kotlinx.serialization.json.jsonArray
import kotlinx.serialization.json.jsonObject
import kotlinx.serialization.json.jsonPrimitive
import kotlinx.serialization.json.intOrNull

@Serializable
data class MateriaUnidades(
    val nombre: String,
    val grupo: String,
    val observaciones: String,
    val unidadesActivas: String,
    val calificaciones: List<Int?>
)
fun parseUnidades(xml: String): List<MateriaUnidades> {

    val jsonString = xml
        .substringAfter("<getCalifUnidadesByAlumnoResult>")
        .substringBefore("</getCalifUnidadesByAlumnoResult>")
        .trim()

    val jsonArray = Json.parseToJsonElement(jsonString).jsonArray

    return jsonArray.map { element ->
        val obj = element.jsonObject

        val califs = (1..13).map { index ->
            val key = "C$index"
            obj[key]?.jsonPrimitive?.intOrNull
        }

        MateriaUnidades(
            nombre          = obj["Materia"]?.jsonPrimitive?.content ?: "",
            grupo           = obj["Grupo"]?.jsonPrimitive?.content ?: "",
            observaciones   = obj["Observaciones"]?.jsonPrimitive?.content ?: "",
            unidadesActivas = obj["UnidadesActivas"]?.jsonPrimitive?.content ?: "",
            calificaciones  = califs
        )
    }
}