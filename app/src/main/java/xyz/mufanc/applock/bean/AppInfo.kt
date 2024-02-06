package xyz.mufanc.applock.bean

import android.content.pm.ApplicationInfo
import xyz.mufanc.applock.App

class AppInfo(info: ApplicationInfo) : ApplicationInfo(info) {

    val label by lazy { pm.getApplicationLabel(this).toString() }

    companion object {
        private val pm = App.instance.packageManager
    }
}
