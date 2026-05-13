package com.example.sicemultiplataform.data.local.dao

import com.example.sicemultiplataform.db.AppDatabase
import com.example.sicemultiplataform.data.local.entity.KardexEntity

class KardexDao(private val db: AppDatabase) {

    fun insert(kardex: KardexEntity) {
        db.kardexQueries.insertOrReplace(
            control = kardex.control,
            jsonData = kardex.jsonData,
            ultimaActualizacion = kardex.ultimaActualizacion
        )
    }

    fun getByControl(control: String): KardexEntity? {
        return db.kardexQueries.getByControl(control)
            .executeAsOneOrNull()
            ?.let {
                KardexEntity(
                    control = it.control,
                    jsonData = it.jsonData,
                    ultimaActualizacion = it.ultimaActualizacion
                )
            }
    }
}