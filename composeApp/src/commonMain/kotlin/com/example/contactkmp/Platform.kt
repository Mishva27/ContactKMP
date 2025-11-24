package com.example.contactkmp

interface Platform {
    val name: String
}

expect fun getPlatform(): Platform

// In commonMain
expect interface CrashlyticsService {
    fun recordException(throwable: Throwable)
    fun setCustomKey(key: String, value: String)
}
