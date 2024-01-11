package xyz.mufanc.applock.core.scope.provider

import android.content.SharedPreferences
import io.github.libxposed.api.XposedInterface

@Suppress("Unused")
data object PreferenceScopeProvider : ScopeProvider() {

    private lateinit var prefs: SharedPreferences

    override fun isAvailable(): Boolean = false  // Todo: dev

    override fun init(ixp: XposedInterface) {
        prefs = ixp.getRemotePreferences("applock_scope")
    }
}
