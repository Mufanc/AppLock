package xyz.mufanc.applock.core.scope.provider

import android.content.SharedPreferences
import io.github.libxposed.api.XposedInterface

@Suppress("Unused")
data object PreferenceScopeProvider : ScopeProvider() {

    private const val TAG = "PreferenceScopeProvider"

    private lateinit var prefs: SharedPreferences

    override fun isAvailable(): Boolean = true

    override fun init(ixp: XposedInterface) {
        prefs = ixp.getRemotePreferences("applock_scope")
        prefs.registerOnSharedPreferenceChangeListener { prefs, _ ->
            emit(prefs.all.keys)
        }
    }
}
