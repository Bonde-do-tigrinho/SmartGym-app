package org.smartgym

interface Platform {
    val name: String
}

expect fun getPlatform(): Platform