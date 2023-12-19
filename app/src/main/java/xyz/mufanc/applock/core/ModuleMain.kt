package xyz.mufanc.applock.core

import io.github.libxposed.api.XposedInterface
import io.github.libxposed.api.XposedModule
import io.github.libxposed.api.XposedModuleInterface
import io.github.libxposed.api.XposedModuleInterface.ModuleLoadedParam
import xyz.mufanc.applock.App
import xyz.mufanc.applock.core.scope.ScopeManager
import xyz.mufanc.applock.core.process.KillProcessMonitor
import xyz.mufanc.applock.core.process.ProcessRecordUtil
import xyz.mufanc.applock.core.process.guard.ProcessGuard
import xyz.mufanc.applock.core.util.GraftClassLoader
import xyz.mufanc.applock.core.util.Log
import xyz.mufanc.autox.annotation.XposedEntry

@XposedEntry(["system"])
@Suppress("Unused")
class ModuleMain(
    private val ixp: XposedInterface,
    private val mlp: ModuleLoadedParam
) : XposedModule(ixp, mlp) {

    companion object {
        private const val TAG: String = "ModuleMain"
    }

    override fun onPackageLoaded(param: XposedModuleInterface.PackageLoadedParam) {

    }

    override fun onSystemServerLoaded(param: XposedModuleInterface.SystemServerLoadedParam) {
        if (App.isDebug) {
            Log.initXposed(ixp)
        }

        Log.i(TAG, "module loaded in ${mlp.processName}.")
        Log.d(TAG, "${param.classLoader}")

        GraftClassLoader.init(param.classLoader)

        ProcessRecordUtil.init()
        ScopeManager.init()
        ProcessGuard.install(ixp)

        if (App.isDebug) {
            KillProcessMonitor.init(ixp)
        }
    }
}
