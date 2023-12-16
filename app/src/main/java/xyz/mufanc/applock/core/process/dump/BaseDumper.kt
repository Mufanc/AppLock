package xyz.mufanc.applock.core.process.dump

import android.os.Build
import io.github.libxposed.api.XposedInterface
import io.github.libxposed.api.annotations.BeforeInvocation
import io.github.libxposed.api.annotations.XposedHooker
import xyz.mufanc.applock.core.process.bean.KillInfo
import xyz.mufanc.applock.core.util.Log

@XposedHooker
abstract class BaseDumper : XposedInterface.Hooker {

    companion object {

        private const val TAG = "BaseHooker"

        // Todo: hook com.android.server.am.ActivityManagerService.forceStopPackage

        @BeforeInvocation
        @JvmStatic
        fun before(callback: XposedInterface.BeforeHookCallback): BaseDumper {
            val hook = when (Build.VERSION.SDK_INT) {
                Build.VERSION_CODES.P -> DumpAndroidP()
                Build.VERSION_CODES.Q -> DumpAndroidQ()
                Build.VERSION_CODES.R -> DumpAndroidR()
                Build.VERSION_CODES.S, Build.VERSION_CODES.S_V2 -> DumpAndroidS()
                Build.VERSION_CODES.TIRAMISU -> DumpAndroidT()
                Build.VERSION_CODES.UPSIDE_DOWN_CAKE -> DumpAndroidU()
                else -> throw Exception("wtf??")
            }

            val info = hook.dump(callback)

            Log.i(TAG, "$info")
            Log.e(TAG, "backtrace:", Throwable())

            return hook
        }
    }

    abstract fun dump(callback: XposedInterface.BeforeHookCallback): KillInfo
}
