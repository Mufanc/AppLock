package mufanc.tools.applock.util.channel

import android.os.Bundle
import mufanc.tools.applock.util.ScopeManager
import mufanc.tools.applock.util.Settings
import kotlin.reflect.KProperty

class Configs(private val bundle: Bundle) {
    val scope: Array<String> by bundle
    val killLevel: Int by bundle

    constructor(scope: Array<String>, killLevel: Int) : this(Bundle()) {
        bundle.putStringArray(::scope.name, scope)
        bundle.putInt(::killLevel.name, killLevel)
    }

    fun getBundle() = bundle

    private operator fun <T> Bundle.getValue(ignored: Configs, property: KProperty<*>): T {
        @Suppress("Unchecked_Cast")
        return get(property.name) as T
    }

    companion object {
        fun collect() = Configs(
            ScopeManager.scope.toTypedArray(),
            when (Settings.KILL_LEVEL.value) {
                Settings.KillLevel.TRIM_MEMORY -> 101
                Settings.KillLevel.NONE -> 100
            }
        )
    }
}
