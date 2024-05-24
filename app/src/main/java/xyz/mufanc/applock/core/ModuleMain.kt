package xyz.mufanc.applock.core

import android.os.Handler
import android.os.Looper
import androidx.annotation.Keep
import io.github.libxposed.api.XposedInterface
import io.github.libxposed.api.XposedModule
import io.github.libxposed.api.XposedModuleInterface
import io.github.libxposed.api.XposedModuleInterface.ModuleLoadedParam
import xyz.mufanc.applock.core.process.KillProcessMonitor
import xyz.mufanc.applock.core.process.guard.ProcessGuard
import xyz.mufanc.applock.core.scope.ScopeManager
import xyz.mufanc.applock.core.util.GraftClassLoader
import xyz.mufanc.applock.core.util.Log
import xyz.mufanc.applock.util.Configs
import xyz.mufanc.autox.annotation.XposedEntry
import kotlin.concurrent.thread

@Keep
@XposedEntry(["system"])
@Suppress("Unused")
class ModuleMain(
    private val ixp: XposedInterface,
    private val mlp: ModuleLoadedParam
) : XposedModule(ixp, mlp) {

    companion object {
        private const val TAG: String = "ModuleMain"
    }

    override fun onSystemServerLoaded(param: XposedModuleInterface.SystemServerLoadedParam) {
        if (Configs.isDebug) {
            Log.initXposed(ixp)
        }

        Log.i(TAG, "module loaded in ${mlp.processName}.")
        Log.d(TAG, "${param.classLoader}")

        GraftClassLoader.init(param.classLoader)
        ClassMagic.init()

        Log.d(TAG, "waiting for system...")

        runOnSystemReady {
            AppLockService.init(ixp)
            ScopeManager.init(ixp)
            ProcessGuard.install(ixp)

            if (Configs.isDebug) {
                KillProcessMonitor.init(ixp)
            }
        }
    }

    private fun runOnSystemReady(block: () -> Unit) {
        thread {
            try {
                while (Looper.getMainLooper() == null) {
                    Thread.sleep(1000)
                }

                Handler(Looper.getMainLooper()).post {
                    block()
                }
            } catch (err: Throwable) {
                Log.e(TAG, "", err)
            }
        }
    }
}
