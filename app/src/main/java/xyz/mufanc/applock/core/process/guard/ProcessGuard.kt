package xyz.mufanc.applock.core.process.guard

import io.github.libxposed.api.XposedInterface
import io.github.libxposed.api.annotations.BeforeInvocation
import io.github.libxposed.api.annotations.XposedHooker
import xyz.mufanc.applock.core.process.model.ProcessInfo
import xyz.mufanc.applock.core.scope.ScopeManager
import xyz.mufanc.applock.core.util.Log
import xyz.mufanc.applock.core.util.signature
import java.lang.reflect.Method

@XposedHooker
object ProcessGuard : XposedInterface.Hooker {

    private const val TAG = "ProcessGuard"

    private var hookTarget: Method? = null
    private lateinit var guardImpl: Adapter

    fun install(ixp: XposedInterface) {
        val implements = Adapter::class.sealedSubclasses.sortedBy { it.objectInstance!!.priority }

        Log.d(TAG, "process guard implements: ${implements.joinToString(", ") { "${it.simpleName}" }}")

        for (klass in implements) {
            val impl = klass.objectInstance
            val target = impl?.getMethod()

            if (target != null) {
                hookTarget = target
                guardImpl = impl
                break
            }
        }

        if (hookTarget != null) {
            ixp.hook(hookTarget!!, ProcessGuard::class.java)
            Log.i(TAG, "use impl: ${guardImpl.javaClass.simpleName}")
            Log.i(TAG, "hook target: ${hookTarget!!.signature()}")
        } else {
            Log.e(TAG, "failed to find compatible adapter!")
        }
    }

    @BeforeInvocation
    @JvmStatic
    @Suppress("Unused", "SameReturnValue")
    fun before(callback: XposedInterface.BeforeHookCallback): ProcessGuard? {
        val pinfo = guardImpl.getProcessInfo(callback)
        val pkg = pinfo?.packageList?.getOrNull(0)

        if (pkg == null) {
            Log.d(TAG, "failed to get package name, skip")
            return null
        }

        if (ScopeManager.query(pkg)) {
            guardImpl.skipForKill(callback)
            Log.d(TAG, "protected process: ${pinfo.name}")
        }

        return null
    }

    sealed class Adapter {

        abstract val priority: Int  // the smaller the number, the higher the priority

        protected abstract fun getMethodInner(): Method
        protected abstract fun getProcessInfoInner(callback: XposedInterface.BeforeHookCallback): ProcessInfo?
        protected abstract fun skipForKillInner(callback: XposedInterface.BeforeHookCallback)

        fun getMethod(): Method? {
            return try {
                getMethodInner()
            } catch (err: Throwable) {
                Log.d(TAG, "failed to load target for ${javaClass.simpleName}, try next rule")
                null
            }
        }

        fun getProcessInfo(callback: XposedInterface.BeforeHookCallback): ProcessInfo? {
            return getProcessInfoInner(callback)?.takeIf { it.isValid }
        }

        fun skipForKill(callback: XposedInterface.BeforeHookCallback) {
            skipForKillInner(callback)
        }
    }
}
