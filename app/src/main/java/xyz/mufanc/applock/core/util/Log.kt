package xyz.mufanc.applock.core.util

import android.util.Log
import io.github.libxposed.api.XposedInterface
import xyz.mufanc.applock.App
import xyz.mufanc.applock.BuildConfig

object Log {

    private const val TAG = "AppLock"

    private var bridge: XposedInterface? = null

    fun initXposed(ixp: XposedInterface) {
        bridge = ixp
    }

    fun d(tag: String, msg: String) {
        if (!App.isDebug) return

        val message = "[$tag] $msg"

        Log.d(TAG, message)
        bridge?.log(message)
    }

    fun i(tag: String, msg: String) {
        val message = "[$tag] $msg"

        Log.i(TAG, message)
        bridge?.log(message)
    }

    fun e(tag: String, msg: String, tr: Throwable? = null) {
        val message = "[$tag] $msg"

        if (tr != null) {
            Log.e(TAG, message, tr)
            bridge?.log(message, tr)
        } else {
            Log.e(TAG, message)
            bridge?.log(message)
        }
    }
}
