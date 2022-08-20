package mufanc.tools.applock.util

import mufanc.tools.applock.MyApplication

object Globals {
    val WORK_MODE get() = MyApplication.prefs.getString("work_mode", "xposed")
}