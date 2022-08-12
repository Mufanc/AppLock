package mufanc.tools.applock.util

import android.widget.Toast
import mufanc.tools.applock.MyApplication
import mufanc.tools.applock.R
import mufanc.tools.applock.core.shizuku.ShizukuHelper
import mufanc.tools.applock.core.xposed.AppLockHelper

object Globals {

    val RESOLVE_MODE get() = MyApplication.prefs.getString("resolve_mode", "category_launcher")

    val WORK_MODE get() = MyApplication.prefs.getString("work_mode", "xposed")

    var LOCKED_APPS: MutableSet<String>
        get() = ScopeDatabase.readScope()
        set(value) {
            ScopeDatabase.writeScope(value)
            when (WORK_MODE) {
                "xposed" ->
                    AppLockHelper.client?.apply {
                        writePackageList(value.toTypedArray())
                        Toast.makeText(MyApplication.context, R.string.scope_saved, Toast.LENGTH_SHORT).show()
                    }
                "shizuku" -> ShizukuHelper.writePackageList(value.toList())
            }
        }

}