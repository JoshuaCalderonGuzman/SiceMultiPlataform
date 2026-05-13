package com.example.sicemultiplataform.data.local.dao

import com.example.sicemultiplataform.db.AppDatabase
import com.example.sicemultiplataform.data.local.entity.CargaEntity

class CargaDao(private val db: AppDatabase) {

    fun insert(carga: CargaEntity) {
        db.cargaQueries.insertOrReplace(
            control = carga.control,
            jsonData = carga.jsonData,
            ultimaActualizacion = carga.ultimaActualizacion
        )
    }

    fun getByControl(control: String): CargaEntity? {
        return db.cargaQueries.getByControl(control)
            .executeAsOneOrNull()
            ?.let {
                CargaEntity(
                    control = it.control,
                    jsonData = it.jsonData,
                    ultimaActualizacion = it.ultimaActualizacion
                )
            }
    }
}