package xyz.mufanc.applock.core.process

import android.os.Build
import io.github.libxposed.api.XposedInterface
import xyz.mufanc.applock.core.process.dump.BaseDumper
import xyz.mufanc.applock.core.process.dump.DumpAndroidP
import xyz.mufanc.applock.core.process.dump.DumpAndroidS
import xyz.mufanc.applock.core.process.dump.DumpAndroidT
import xyz.mufanc.applock.core.util.Log

object KillProcessDump {

    private const val TAG = "KillProcessHook"

    fun dispatch(ixp: XposedInterface) {
        ProcessRecordHelpers.init()

        val method = when (Build.VERSION.SDK_INT) {
            Build.VERSION_CODES.P -> DumpAndroidP.hookTarget
            Build.VERSION_CODES.Q -> null
            Build.VERSION_CODES.R -> null
            Build.VERSION_CODES.S, Build.VERSION_CODES.S_V2 -> DumpAndroidS.hookTarget
            Build.VERSION_CODES.TIRAMISU -> DumpAndroidT.hookTarget
            Build.VERSION_CODES.UPSIDE_DOWN_CAKE -> null
            else -> null
        }

        if (method != null) {
            ixp.hook(method, BaseDumper::class.java)
        } else {
            Log.e(TAG, "unsupported Android version!")
        }
    }
}
