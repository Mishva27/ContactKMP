package com.example.contactkmp

import android.os.Build
import com.google.firebase.crashlytics.FirebaseCrashlytics

class AndroidPlatform : Platform {
    override val name: String = "Android ${Build.VERSION.SDK_INT}"
}

actual fun getPlatform(): Platform = AndroidPlatform()


actual interface CrashlyticsService {
    actual fun recordException(throwable: Throwable)
    actual fun setCustomKey(key: String, value: String)
}