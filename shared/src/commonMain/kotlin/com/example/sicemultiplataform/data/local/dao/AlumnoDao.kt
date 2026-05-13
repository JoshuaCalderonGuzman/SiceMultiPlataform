package com.example.sicemultiplataform.data.local.dao

import com.example.sicemultiplataform.db.AppDatabase
import com.example.sicemultiplataform.data.local.entity.AlumnoEntity

class AlumnoDao(private val db: AppDatabase) {

    fun insert(alumno: AlumnoEntity) {
        db.alumnoQueries.insertOrReplace(
            control = alumno.control,
            jsonData = alumno.jsonData,
            ultimaActualizacion = alumno.ultimaActualizacion
        )
    }

    fun get(control: String): AlumnoEntity? {
        return db.alumnoQueries.getByControl(control)
            .executeAsOneOrNull()
            ?.let {
                AlumnoEntity(
                    control = it.control,
                    jsonData = it.jsonData,
                    ultimaActualizacion = it.ultimaActualizacion
                )
            }
    }
}