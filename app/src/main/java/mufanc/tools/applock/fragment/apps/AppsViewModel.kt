package mufanc.tools.applock.fragment.apps

import android.app.Activity
import android.content.Intent
import android.content.pm.ApplicationInfo
import android.content.pm.PackageManager
import android.graphics.drawable.Drawable
import androidx.lifecycle.ViewModel
import mufanc.tools.applock.MyApplication
import mufanc.tools.applock.xposed.AppLockHelper
import kotlin.concurrent.thread

class AppsViewModel : ViewModel() {
    data class AppInfo(
        val appName: String,
        val packageName: String,
        val icon: Drawable
    )

    val appList = mutableSetOf<AppInfo>()

    val lockedAppList: MutableSet<String> get() =
        when (MyApplication.prefs.getString("work_mode", "xposed")) {
            "xposed" -> AppLockHelper.client?.readPackageList()?.toMutableSet()
            "shizuku" -> {
                MyApplication.prefs.getString("locked_app_list", "")!!.let {
                    it.ifEmpty { return@let null }
                    it.split("#").toMutableSet()
                }
            }
            else -> throw RuntimeException()
        } ?: mutableSetOf()

    fun loadAppList(activity: Activity, refresh: Boolean = false, callback: () -> Unit) {
        thread {
            if (refresh || appList.isEmpty()) {
                val packageManager = activity.packageManager
                val mode = MyApplication.prefs.getString("resolve_mode", "category_launcher")
                appList.clear()
                appList.addAll(when (mode) {
                    "category_launcher" -> packageManager
                        .queryIntentActivities(
                            Intent(Intent.ACTION_MAIN).addCategory(Intent.CATEGORY_LAUNCHER),
                            PackageManager.MATCH_ALL
                        )
                        .map { it.activityInfo.applicationInfo }
                        .map {
                            AppInfo(
                                it.loadLabel(packageManager).toString(),
                                it.packageName,
                                it.loadIcon(packageManager)
                            )
                        }
                    "non_system_apps" -> packageManager
                        .getInstalledApplications(0)
                        .filter { it.flags and ApplicationInfo.FLAG_SYSTEM == 0 }
                        .map {
                            AppInfo(
                                it.loadLabel(packageManager).toString(),
                                it.packageName,
                                it.loadIcon(packageManager)
                            )
                        }
                    else -> throw RuntimeException()
                })
            }
            activity.runOnUiThread(callback)
        }
    }
}