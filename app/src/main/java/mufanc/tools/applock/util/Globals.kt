package mufanc.tools.applock.util

import mufanc.tools.applock.MyApplication
import mufanc.tools.applock.shizuku.ShizukuHelper
import mufanc.tools.applock.xposed.AppLockHelper

object Globals {

    val RESOLVE_MODE get() = MyApplication.prefs.getString("resolve_mode", "category_launcher")

    val WORK_MODE get() = MyApplication.prefs.getString("work_mode", "xposed")

    var LOCKED_APPS: MutableSet<String>
        get() {
            return when (WORK_MODE) {
                "xposed" -> AppLockHelper.client?.readPackageList()?.toMutableSet()
                "shizuku" -> {
                    MyApplication.prefs.getString("locked_app_list", "")!!.let {
                        it.ifEmpty { return@let null }
                        it.split("#").toMutableSet()
                    }
                }
                else -> throw RuntimeException()
            } ?: mutableSetOf()
        }
        set(value) {
            when (Globals.WORK_MODE) {
                "xposed" -> AppLockHelper.client?.writePackageList(value.toTypedArray())
                "shizuku" -> {
                    ShizukuHelper.writePackageList(value.toList())
                    MyApplication.prefs.edit().also {
                        it.putString("locked_app_list", value.joinToString("#"))
                    }.apply()
                }
            }
        }

}