package xyz.mufanc.applock.core.process

import xyz.mufanc.applock.core.util.ClassUtils
import xyz.mufanc.applock.core.util.GraftClassLoader
import xyz.mufanc.applock.core.util.Log

object ProcessRecordHelpers {

    private const val TAG = "ProcessRecordHelpers"

    fun init() {
        ClassUtils.makePublic(GraftClassLoader.loadClass("com.android.server.am.ProcessRecord"))
        Log.i(TAG, "make class ProcessRecord public.")
    }
}
