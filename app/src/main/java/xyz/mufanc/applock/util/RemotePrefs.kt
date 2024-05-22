package xyz.mufanc.applock.util

import android.content.SharedPreferences
import io.github.libxposed.service.XposedService
import kotlinx.coroutines.flow.MutableStateFlow

object RemotePrefs {

    val scope = MutableStateFlow<SharedPreferences?>(null)
    val disabledProviders = MutableStateFlow<SharedPreferences?>(null)

    fun init(service: XposedService) {
        scope.value = service.getRemotePreferences("applock_scope")
        disabledProviders.value = service.getRemotePreferences("applock_disabled_providers")
    }
}
