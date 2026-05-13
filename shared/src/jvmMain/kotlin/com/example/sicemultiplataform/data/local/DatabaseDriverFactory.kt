package com.example.sicemultiplataform.data.local

import app.cash.sqldelight.db.SqlDriver
import app.cash.sqldelight.driver.jdbc.sqlite.JdbcSqliteDriver
import com.example.sicemultiplataform.db.AppDatabase
import java.io.File

actual class DatabaseDriverFactory {
    actual fun createDriver(): SqlDriver {
        val dbFile = File("sicenet_db.db")
        val driver = JdbcSqliteDriver("jdbc:sqlite:${dbFile.absolutePath}")
        if (!dbFile.exists()) {
            AppDatabase.Schema.create(driver)
        }
        return driver
    }
}