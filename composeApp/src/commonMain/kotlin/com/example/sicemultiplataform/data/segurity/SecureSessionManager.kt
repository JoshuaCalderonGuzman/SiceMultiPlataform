package com.example.sicemultiplataform.data.segurity

expect class SecureSessionManager {
    fun guardarSesion(matricula: String, password: String)
    fun obtenerMatricula(): String?
    fun obtenerPassword(): String?
    fun cerrarSesion()
}