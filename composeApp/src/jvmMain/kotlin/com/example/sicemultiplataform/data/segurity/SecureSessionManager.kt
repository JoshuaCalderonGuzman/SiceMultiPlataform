package com.example.sicemultiplataform.data.segurity

import java.io.File
import java.util.Properties

actual class SecureSessionManager {

    private val propsFile = File(System.getProperty("user.home"), ".sicenet/session.properties")
    private val props = Properties()

    init {
        if (propsFile.exists()) {
            propsFile.inputStream().use { props.load(it) }
        }
    }

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