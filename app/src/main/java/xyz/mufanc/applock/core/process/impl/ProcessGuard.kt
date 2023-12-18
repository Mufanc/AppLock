package xyz.mufanc.applock.core.process.impl

import io.github.libxposed.api.XposedInterface
import io.github.libxposed.api.annotations.BeforeInvocation
import io.github.libxposed.api.annotations.XposedHooker
import xyz.mufanc.applock.core.process.model.ProcessInfo
import xyz.mufanc.applock.core.util.Log
import xyz.mufanc.applock.core.util.signature
import java.lang.reflect.Method

@XposedHooker
object ProcessGuard : XposedInterface.Hooker {

    private const val TAG = "ProcessGuard"

    private var hookTarget: Method? = null
    private lateinit var guardImpl: Adapter

    fun install(ixp: XposedInterface) {
        val implements = Adapter::class.sealedSubclasses

        Log.d(TAG, "process guard implements: ${implements.joinToString(", ") { "${it.simpleName}" }}")

        for (klass in implements) {
            val impl = klass.objectInstance
            val target = impl?.getHookTarget()

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
            Log.e(TAG, "failed to find compatible adapter for process guard!")
        }
    }

    @BeforeInvocation
    @JvmStatic
    fun before(callback: XposedInterface.BeforeHookCallback): ProcessGuard? {
        val pinfo = guardImpl.getProcessInfo(callback)

        Log.d(TAG, "kill process: $pinfo")

        return null
    }

    sealed class Adapter {
        abstract fun getMethod(): Method
        abstract fun getProcessInfo(callback: XposedInterface.BeforeHookCallback): ProcessInfo?

        fun getHookTarget(): Method? {
            return try {
                getMethod()
            } catch (err: Throwable) {
                Log.d(TAG, "failed to load target for ${javaClass.simpleName}, try next rule")
                null
            }
        }
    }
}
