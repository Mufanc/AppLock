package xyz.mufanc.applock.util

import xyz.mufanc.applock.BuildConfig

object Configs {
    val isDebug get() = BuildConfig.DEBUG || PropHelper.getBoolean("debug.applock", false)
    val isMonitorLogEnabled get() = PropHelper.getBoolean("debug.applock.enable_monitor_log", false)
}
