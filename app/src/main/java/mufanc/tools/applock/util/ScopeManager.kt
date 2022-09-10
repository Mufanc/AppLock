package mufanc.tools.applock.util

import android.annotation.SuppressLint
import mufanc.tools.applock.core.shizuku.ShizukuHelper
import mufanc.tools.applock.core.xposed.AppLockService
import mufanc.tools.applock.util.channel.Configs

object ScopeManager {
    val scope = Settings.SCOPE.value.toMutableSet()

    @SuppressLint("ApplySharedPref")
    fun commit() {
        Settings.prefs.edit().apply {
            putStringSet(Settings.SCOPE.key, scope)
        }.commit()

        when (Settings.WORK_MODE.value) {
            Settings.WorkMode.XPOSED -> {
                AppLockService.client?.updateConfigs(Configs.collect().asBundle())
            }
            Settings.WorkMode.SHIZUKU -> {
                ShizukuHelper.writePackageList(scope.toList())
            }
        }
    }
}
