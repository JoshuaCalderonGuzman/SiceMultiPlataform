
package com.example.sicemultiplataform.data

import io.ktor.client.plugins.cookies.CookiesStorage
import io.ktor.http.*
import kotlinx.coroutines.sync.Mutex
import kotlinx.coroutines.sync.withLock
import java.io.File
import java.util.Properties

// Almacenamiento de cookies en archivo para que sobrevivan entre sesiones
class FileCookieStorage : CookiesStorage {

    private val file = File(System.getProperty("user.home"), ".sicenet/cookies.properties")
    private val mutex = Mutex()
    private val props = Properties()

    init {
        if (file.exists()) {
            file.inputStream().use { props.load(it) }
        }
    }

    override suspend fun get(requestUrl: Url): List<Cookie> = mutex.withLock {
        props.entries.mapNotNull { (k, v) ->
            runCatching {
                val parts = (v as String).split("|")
                Cookie(
                    name  = k as String,
                    value = parts[0],
                    path  = parts.getOrNull(1),
                    domain = parts.getOrNull(2)
                )
            }.getOrNull()
        }
    }

    override suspend fun addCookie(requestUrl: Url, cookie: Cookie): Unit = mutex.withLock {
        props[cookie.name] = "${cookie.value}|${cookie.path ?: ""}|${cookie.domain ?: ""}"
        save()
    }

    override fun close() { /* no-op */ }

    fun clear() {
        props.clear()
        file.delete()
    }

    private fun save() {
        file.parentFile?.mkdirs()
        file.outputStream().use { props.store(it, null) }
    }
}