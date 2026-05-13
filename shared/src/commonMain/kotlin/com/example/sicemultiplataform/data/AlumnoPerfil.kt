package com.example.sicemultiplataform.data

import kotlinx.serialization.Serializable
import kotlinx.serialization.json.Json
import kotlinx.serialization.json.jsonObject
import kotlinx.serialization.json.jsonPrimitive
import kotlinx.serialization.json.intOrNull
import kotlinx.serialization.json.booleanOrNull

@Serializable
data class Alumno(
    val fechaReins: String = "",
    val modEducativo: Int = 0,
    val adeudo: Boolean = false,
    val urlFoto: String = "",
    val adeudoDescriptivo: String = "",
    val inscrito: Boolean = false,
    val estatus: String = "",
    val semActual: String = "",
    val cdtosAcumulados: Int = 0,
    val cdtosActuales: Int = 0,
    val especialidad: String = "",
    val carrera: String = "",
    val liniamiento: Int = 0,
    val nombre: String = "",
    val matricula: String = "",
)

fun parseAlumno(xml: String): Alumno {

    val jsonString = xml
        .substringAfter("<getAlumnoAcademicoWithLineamientoResult>")
        .substringBefore("</getAlumnoAcademicoWithLineamientoResult>")
        .trim()

    val json = Json.parseToJsonElement(jsonString).jsonObject

    return Alumno(
        fechaReins        = json["fechaReins"]?.jsonPrimitive?.content ?: "",
        modEducativo      = json["modEducativo"]?.jsonPrimitive?.intOrNull ?: 0,
        adeudo            = json["adeudo"]?.jsonPrimitive?.booleanOrNull ?: false,
        urlFoto           = json["urlFoto"]?.jsonPrimitive?.content ?: "",
        adeudoDescriptivo = json["adeudoDescriptivo"]?.jsonPrimitive?.content ?: "",
        inscrito          = json["inscrito"]?.jsonPrimitive?.booleanOrNull ?: false,
        estatus           = json["estatus"]?.jsonPrimitive?.content ?: "",
        semActual         = json["semActual"]?.jsonPrimitive?.content ?: "",
        cdtosAcumulados   = json["cdtosAcumulados"]?.jsonPrimitive?.intOrNull ?: 0,
        cdtosActuales     = json["cdtosActuales"]?.jsonPrimitive?.intOrNull ?: 0,
        especialidad      = json["especialidad"]?.jsonPrimitive?.content ?: "",
        carrera           = json["carrera"]?.jsonPrimitive?.content ?: "",
        liniamiento       = json["liniamiento"]?.jsonPrimitive?.intOrNull ?: 0,
        nombre            = json["nombre"]?.jsonPrimitive?.content ?: "",
        matricula         = json["matricula"]?.jsonPrimitive?.content ?: "",
    )
}