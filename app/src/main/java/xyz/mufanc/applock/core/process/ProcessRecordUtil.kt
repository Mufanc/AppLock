package xyz.mufanc.applock.core.process

import xyz.mufanc.applock.core.util.ClassUtil
import xyz.mufanc.applock.core.util.GraftClassLoader
import xyz.mufanc.applock.core.util.Log

object ProcessRecordUtil {

    private const val TAG = "ProcessRecordUtil"

    fun init() {
        ClassUtil.makePublic(GraftClassLoader.loadClass("com.android.server.am.ProcessRecord"))
        Log.i(TAG, "make class ProcessRecord public.")
    }
}
