package xyz.mufanc.applock.core.process.hook

import android.os.Build
import io.github.libxposed.api.XposedInterface
import io.github.libxposed.api.annotations.BeforeInvocation
import io.github.libxposed.api.annotations.XposedHooker
import xyz.mufanc.applock.core.process.bean.KillInfo
import xyz.mufanc.applock.core.util.Log

@XposedHooker
abstract class BaseHooker : XposedInterface.Hooker {

    companion object {

        private const val TAG = "BaseHooker"

        @BeforeInvocation
        @JvmStatic
        fun before(callback: XposedInterface.BeforeHookCallback): BaseHooker {
            val hook = when (Build.VERSION.SDK_INT) {
                Build.VERSION_CODES.Q -> HookAndroidQ()
                Build.VERSION_CODES.R -> HookAndroidR()
                Build.VERSION_CODES.S, Build.VERSION_CODES.S_V2 -> HookAndroidS()
                Build.VERSION_CODES.TIRAMISU -> HookAndroidT()
                Build.VERSION_CODES.UPSIDE_DOWN_CAKE -> HookAndroidU()
                else -> throw Exception("wtf??")
            }

            val info = hook.dump(callback)

            Log.i(TAG, "$info")

            if (hook.isProtectedProcess(info)) {
                callback.returnAndSkip(null)
            }

            return hook
        }
    }

    abstract fun dump(callback: XposedInterface.BeforeHookCallback): KillInfo

    open fun isProtectedProcess(info: KillInfo): Boolean {
        return false
    }
}
