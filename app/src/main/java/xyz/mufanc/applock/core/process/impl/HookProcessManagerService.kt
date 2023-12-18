package xyz.mufanc.applock.core.process.impl

import android.annotation.SuppressLint
import io.github.libxposed.api.XposedInterface
import xyz.mufanc.applock.core.process.model.ProcessInfo
import xyz.mufanc.applock.core.process.model.ProcessInfoFactory
import xyz.mufanc.applock.core.util.GraftClassLoader
import java.lang.reflect.Method

data object HookProcessManagerService : ProcessGuard.Adapter() {
    @SuppressLint("PrivateApi")
    override fun getMethod(): Method {
        return GraftClassLoader.loadClass("com.android.server.am.ProcessManagerService")
            .declaredMethods
            .filter { it.name == "killOnce" && it.parameterTypes[0].simpleName == "ProcessRecord" }
            .maxByOrNull { it.parameterCount }!!
    }

    override fun getProcessInfo(callback: XposedInterface.BeforeHookCallback): ProcessInfo? {
        return ProcessInfoFactory.create(callback.args[0]).takeIf { it.isValid }
    }
}
