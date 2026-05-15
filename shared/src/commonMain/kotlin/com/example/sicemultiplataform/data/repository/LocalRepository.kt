package com.example.sicemultiplataform.data.repository

import com.example.sicemultiplataform.data.local.dao.AlumnoDao
import com.example.sicemultiplataform.data.local.dao.CargaDao
import com.example.sicemultiplataform.data.local.dao.KardexDao
import com.example.sicemultiplataform.data.local.dao.CalifFinalDao
import com.example.sicemultiplataform.data.local.dao.CalifUnidadesDao
import com.example.sicemultiplataform.data.local.entity.AlumnoEntity
import com.example.sicemultiplataform.data.local.entity.CargaEntity
import com.example.sicemultiplataform.data.local.entity.KardexEntity
import com.example.sicemultiplataform.data.local.entity.CalifFinalEntity
import com.example.sicemultiplataform.data.local.entity.CalifUnidadesEntity

// Clase para acceder a los datos locales
class LocalRepository(
    private val alumnoDao: AlumnoDao,
    private val cargaDao: CargaDao,
    private val kardexDao: KardexDao,
    private val califFinalDao: CalifFinalDao,
    private val califUnidadesDao: CalifUnidadesDao
) {
    //ALUMNO
    fun saveAlumno(entity: AlumnoEntity) = alumnoDao.insert(entity)
    fun getAlumno(control: String) = alumnoDao.get(control)

    //CARGA ACADÉMICA
    fun insertCarga(carga: CargaEntity) = cargaDao.insert(carga)
    fun getCargaByControl(control: String) = cargaDao.getByControl(control)

    //KARDEX
    fun insertKardex(kardex: KardexEntity) = kardexDao.insert(kardex)
    fun getKardexByControl(control: String) = kardexDao.getByControl(control)

    // CALIFICACIONES FINALES
    fun insertCalifFinal(calif: CalifFinalEntity) = califFinalDao.insert(calif)
    fun getCalifFinalByControl(control: String) = califFinalDao.getByControl(control)

    //CALIFICACIONES UNIDADES
    fun insertCalifUnidades(calif: CalifUnidadesEntity) = califUnidadesDao.insert(calif)
    fun getCalifUnidadesByControl(control: String) = califUnidadesDao.getByControl(control)
}