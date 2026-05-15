package com.example.sicemultiplataform.data.segurity

import java.io.File
import java.util.Properties

actual class SecureSessionManager {

    // Implementa las funciones de SecureSessionManager
    private val propsFile = File(System.getProperty("user.home"), ".sicenet/session.properties")
    private val props = Properties()

    // Cargar las propiedades desde el archivo si existe
    init {
        if (propsFile.exists()) {
            propsFile.inputStream().use { props.load(it) }
        }
    }

    // Guardar las propiedades en el archivo
    private fun save() {
        propsFile.parentFile?.mkdirs()
        propsFile.outputStream().use { props.store(it, null) }
    }


    actual fun guardarSesion(matricula: String, password: String) {
        props["matricula"] = matricula
        props["password"]  = password
        save()
    }

    actual fun obtenerMatricula(): String? = props.getProperty("matricula")
    actual fun obtenerPassword(): String?  = props.getProperty("password")

    actual fun cerrarSesion() {
        props.clear()
        propsFile.delete()
    }
}