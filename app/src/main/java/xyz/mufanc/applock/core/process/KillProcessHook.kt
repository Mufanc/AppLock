package xyz.mufanc.applock.core.process

import android.os.Build
import io.github.libxposed.api.XposedInterface
import xyz.mufanc.applock.core.process.hook.*
import xyz.mufanc.applock.core.util.Log

object KillProcessHook {

    private const val TAG = "KillProcessHook"

    fun dispatch(ixp: XposedInterface) {
        val method = when (Build.VERSION.SDK_INT) {
            Build.VERSION_CODES.Q -> null
            Build.VERSION_CODES.R -> null
            Build.VERSION_CODES.S, Build.VERSION_CODES.S_V2 -> null
            Build.VERSION_CODES.TIRAMISU -> HookAndroidT.hookTarget
            Build.VERSION_CODES.UPSIDE_DOWN_CAKE -> null
            else -> null
        }

        if (method != null) {
            ixp.hook(method, BaseHooker::class.java)
        } else {
            Log.e(TAG, "unsupported Android version!")
        }
    }
}
