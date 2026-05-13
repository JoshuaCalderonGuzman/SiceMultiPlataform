package com.example.sicemultiplataform.data.local

import com.example.sicemultiplataform.db.AppDatabase

fun createDatabase(driverFactory: DatabaseDriverFactory): AppDatabase {
    return AppDatabase(driverFactory.createDriver())
}