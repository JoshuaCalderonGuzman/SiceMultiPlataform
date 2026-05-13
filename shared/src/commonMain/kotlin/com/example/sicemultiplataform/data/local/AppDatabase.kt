package com.example.sicemultiplataform.data.local

import com.example.sicemultiplataform.db.AppDatabase

class DatabaseProvider(
    driverFactory: DatabaseDriverFactory
) {

    val database: AppDatabase = AppDatabase(
        driver = driverFactory.createDriver()
    )
}