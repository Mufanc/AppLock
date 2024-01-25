package xyz.mufanc.applock.core.scope.provider

import io.github.libxposed.api.XposedInterface
import xyz.mufanc.applock.core.scope.provider.ScopeProvider.ScopeType

@Suppress("TopLevel_TypeAliases_Only")
sealed class ScopeProvider {

    typealias ScopeType = Set<String>

    interface OnScopeChangedListener {
        fun onScopeChanged(old: ScopeType, new: ScopeType)
    }

    private var oldScope: ScopeType = emptySet()
    private var registeredListener: OnScopeChangedListener? = null

    abstract fun isAvailable(): Boolean

    abstract fun init(ixp: XposedInterface)

    fun registerOnScopeChangedListener(listener: OnScopeChangedListener) {
        registeredListener = listener
    }

    protected fun emit(newScope: Set<String>) {
        registeredListener?.onScopeChanged(oldScope, newScope)
        oldScope = newScope
    }
}
