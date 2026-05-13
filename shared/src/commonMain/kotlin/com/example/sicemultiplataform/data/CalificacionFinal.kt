package com.example.sicemultiplataform.data

import kotlinx.serialization.Serializable
import kotlinx.serialization.json.Json
import kotlinx.serialization.json.jsonArray
import kotlinx.serialization.json.jsonObject
import kotlinx.serialization.json.jsonPrimitive
import kotlinx.serialization.json.intOrNull

@Serializable
data class CalificacionFinal(
    val calif: Int,
    val acred: String,
    val grupo: String,
    val materia: String,
    val observaciones: String
)

fun parseCalifFinal(xml: String): List<CalificacionFinal> {

    val jsonString = xml
        .substringAfter("<getAllCalifFinalByAlumnosResult>")
        .substringBefore("</getAllCalifFinalByAlumnosResult>")
        .trim()

    val jsonArray = Json.parseToJsonElement(jsonString).jsonArray

    return jsonArray.map { element ->
        val obj = element.jsonObject
        CalificacionFinal(
            calif         = obj["calif"]?.jsonPrimitive?.intOrNull ?: 0,
            acred         = obj["acred"]?.jsonPrimitive?.content ?: "",
            grupo         = obj["grupo"]?.jsonPrimitive?.content ?: "",
            materia       = obj["materia"]?.jsonPrimitive?.content ?: "",
            observaciones = obj["Observaciones"]?.jsonPrimitive?.content ?: ""
        )
    }
}