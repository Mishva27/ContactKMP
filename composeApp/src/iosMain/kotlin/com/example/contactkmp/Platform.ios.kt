package com.example.contactkmp

import platform.UIKit.UIDevice

class IOSPlatform: Platform {
    override val name: String = UIDevice.currentDevice.systemName() + " " + UIDevice.currentDevice.systemVersion
}

actual fun getPlatform(): Platform = IOSPlatform()

actual interface CrashlyticsService {
    actual fun recordException(throwable: Throwable)
    actual fun setCustomKey(key: String, value: String)
}