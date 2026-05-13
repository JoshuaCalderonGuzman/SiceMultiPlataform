package com.example.sicemultiplataform.data.local.dao

import com.example.sicemultiplataform.db.AppDatabase
import com.example.sicemultiplataform.data.local.entity.CalifFinalEntity

class CalifFinalDao(private val db: AppDatabase) {

    fun insert(calif: CalifFinalEntity) {
        db.califFinalQueries.insertOrReplace(
            control = calif.control,
            jsonData = calif.jsonData,
            ultimaActualizacion = calif.ultimaActualizacion
        )
    }

    fun getByControl(control: String): CalifFinalEntity? {
        return db.califFinalQueries.getByControl(control)
            .executeAsOneOrNull()
            ?.let {
                CalifFinalEntity(
                    control = it.control,
                    jsonData = it.jsonData,
                    ultimaActualizacion = it.ultimaActualizacion
                )
            }
    }
}