package com.example.sicemultiplataform

interface Platform {
    val name: String
}

expect fun getPlatform(): Platform