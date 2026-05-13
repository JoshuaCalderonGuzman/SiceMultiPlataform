package com.example.sicemultiplataform.data.local.dao

import com.example.sicemultiplataform.db.AppDatabase
import com.example.sicemultiplataform.data.local.entity.CalifUnidadesEntity

class CalifUnidadesDao(private val db: AppDatabase) {

    fun insert(calif: CalifUnidadesEntity) {
        db.califUnidadesQueries.insertOrReplace(
            control = calif.control,
            jsonData = calif.jsonData,
            ultimaActualizacion = calif.ultimaActualizacion
        )
    }

    fun getByControl(control: String): CalifUnidadesEntity? {
        return db.califUnidadesQueries.getByControl(control)
            .executeAsOneOrNull()
            ?.let {
                CalifUnidadesEntity(
                    control = it.control,
                    jsonData = it.jsonData,
                    ultimaActualizacion = it.ultimaActualizacion
                )
            }
    }
}