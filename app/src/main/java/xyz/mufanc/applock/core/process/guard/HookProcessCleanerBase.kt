package xyz.mufanc.applock.core.process.guard

import android.annotation.SuppressLint
import io.github.libxposed.api.XposedInterface
import xyz.mufanc.applock.core.process.model.ProcessInfo
import xyz.mufanc.applock.core.process.model.ProcessInfoFactory
import xyz.mufanc.applock.core.util.GraftClassLoader
import java.lang.reflect.Method

@Suppress("Unused")
data object HookProcessCleanerBase : ProcessGuard.Adapter() {

    override val priority: Int = 0

    @SuppressLint("PrivateApi")
    override fun getMethodInner(): Method {
        return GraftClassLoader.loadClass("com.android.server.am.ProcessCleanerBase")
            .declaredMethods
            .filter { it.name == "killOnce" && it.parameterTypes[0].simpleName == "ProcessRecord" }
            .minByOrNull { it.parameterCount }!!
    }

    override fun getProcessInfoInner(callback: XposedInterface.BeforeHookCallback): ProcessInfo? {
        return ProcessInfoFactory.create(callback.args[0]).takeIf { it.isValid }
    }

    override fun skipForKillInner(callback: XposedInterface.BeforeHookCallback) {
        callback.returnAndSkip(null)
    }
}
