package com.example.sicemultiplataform.provider

import android.content.ContentProvider
import android.content.ContentValues
import android.content.UriMatcher
import android.database.Cursor
import android.database.MatrixCursor
import android.net.Uri
import app.cash.sqldelight.driver.android.AndroidSqliteDriver
import com.example.sicemultiplataform.db.AppDatabase

class CargaKardexProvider : ContentProvider() {

    private lateinit var db: AppDatabase
    private lateinit var driver: AndroidSqliteDriver

    companion object {
        const val AUTHORITY = "com.example.sicemultiplataform.provider"
        private const val CARGA = 1
        private const val KARDEX = 2
        val uriMatcher = UriMatcher(UriMatcher.NO_MATCH).apply {
            addURI(AUTHORITY, "carga", CARGA)
            addURI(AUTHORITY, "kardex", KARDEX)
        }
    }

    override fun onCreate(): Boolean {
        driver = AndroidSqliteDriver(AppDatabase.Schema, context!!, "sicenet_db")
        db = AppDatabase(driver)
        return true
    }

    override fun query(
        uri: Uri,
        projection: Array<out String>?,
        selection: String?,
        selectionArgs: Array<out String>?,
        sortOrder: String?
    ): Cursor {
        val cursor = MatrixCursor(arrayOf("control", "jsonData", "ultimaActualizacion"))
        when (uriMatcher.match(uri)) {
            CARGA -> db.cargaQueries.getAll().executeAsList().forEach {
                cursor.addRow(arrayOf<Any>(it.control, it.jsonData, it.ultimaActualizacion))
            }
            KARDEX -> db.kardexQueries.getAll().executeAsList().forEach {
                cursor.addRow(arrayOf<Any>(it.control, it.jsonData, it.ultimaActualizacion))
            }
        }
        return cursor
    }

    override fun insert(uri: Uri, values: ContentValues?): Uri? {
        val control = values?.getAsString("control") ?: return null
        val jsonData = values.getAsString("jsonData") ?: return null
        val tiempo = values.getAsLong("ultimaActualizacion") ?: System.currentTimeMillis()
        when (uriMatcher.match(uri)) {
            CARGA  -> db.cargaQueries.insertOrReplace(control, jsonData, tiempo)
            KARDEX -> db.kardexQueries.insertOrReplace(control, jsonData, tiempo)
        }
        return Uri.withAppendedPath(uri, control)
    }

    override fun delete(uri: Uri, selection: String?, selectionArgs: Array<out String>?): Int {
        val control = selectionArgs?.firstOrNull() ?: return 0
        when (uriMatcher.match(uri)) {
            CARGA  -> db.cargaQueries.deleteByControl(control)
            KARDEX -> db.kardexQueries.deleteByControl(control)
        }
        return 1
    }

    override fun update(uri: Uri, values: ContentValues?, selection: String?, selectionArgs: Array<out String>?): Int {
        return 0 // usar insert con OR REPLACE
    }

    override fun getType(uri: Uri): String? = null
}