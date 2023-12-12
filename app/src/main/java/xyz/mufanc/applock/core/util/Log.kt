package xyz.mufanc.applock.core.util

import android.util.Log

object Log {

    private const val TAG = "AppLock"

    fun d(tag: String, msg: String) {
        Log.d(TAG, "[$tag] $msg")
    }

    fun i(tag: String, msg: String) {
        Log.i(TAG, "[$tag] $msg")
    }

    fun e(tag: String, msg: String, tr: Throwable? = null) {
        if (tr != null) {
            Log.e(TAG, "[$tag] $msg", tr)
        } else {
            Log.e(TAG, "[$tag] $msg")
        }
    }
}
