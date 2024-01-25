package xyz.mufanc.applock.util

import xyz.mufanc.applock.App
import xyz.mufanc.applock.BuildConfig
import xyz.mufanc.applock.ui.util.ColorUtils

object Configs {
    val isDebug get() = BuildConfig.DEBUG || PropHelper.getBoolean("debug.applock", false)

    val isMonitorLogEnabled get() = PropHelper.getBoolean("debug.applock.enable_monitor_log", false)

    val isMonetEnabled by lazy { ColorUtils.isMonetEnabled(App.instance) }
}
