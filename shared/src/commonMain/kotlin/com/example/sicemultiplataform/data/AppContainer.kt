package com.example.sicemultiplataform.data

import com.example.sicemultiplataform.data.repository.LocalRepository

interface AppContainer {
    val snRepository: SNRepository
    val localRepository: LocalRepository
}