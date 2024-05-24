package xyz.mufanc.applock.core

import android.util.ArrayMap
import xyz.mufanc.applock.core.util.ClassUtil
import xyz.mufanc.applock.core.util.GraftClassLoader
import xyz.mufanc.applock.core.util.Log

object ClassMagic {

    private const val TAG = "ProcessRecordUtil"

    fun init() {
        ClassUtil.makePublic(GraftClassLoader.loadClass("com.android.server.am.ProcessRecord"))
        Log.d(TAG, "make class ProcessRecord public.")

//        ClassUtil.makeNonFinal(ArrayMap::class.java)
//        Log.d("DeviceIdleProvider", "make class ArrayMap non-final")
    }
}
