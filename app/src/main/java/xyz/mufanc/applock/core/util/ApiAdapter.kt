package xyz.mufanc.applock.core.util

import android.os.Build

abstract class ApiAdapter<T, R> {

    object UnsupportedAndroidVersionException : UnsupportedOperationException("unsupported android version")

    fun adapt(from: T): R {
        return when (Build.VERSION.SDK_INT) {
            Build.VERSION_CODES.P -> doAndroidP(from)
            Build.VERSION_CODES.Q -> doAndroidQ(from)
            Build.VERSION_CODES.R -> doAndroidR(from)
            Build.VERSION_CODES.S, Build.VERSION_CODES.S_V2 -> doAndroidS(from)
            Build.VERSION_CODES.TIRAMISU -> doAndroidT(from)
            Build.VERSION_CODES.UPSIDE_DOWN_CAKE -> doAndroidU(from)
            else -> throw UnsupportedAndroidVersionException
        }
    }

    protected abstract fun doAndroidP(from: T): R

    protected abstract fun doAndroidQ(from: T): R

    protected abstract fun doAndroidR(from: T): R

    protected abstract fun doAndroidS(from: T): R

    protected abstract fun doAndroidT(from: T): R

    protected abstract fun doAndroidU(from: T): R
}
