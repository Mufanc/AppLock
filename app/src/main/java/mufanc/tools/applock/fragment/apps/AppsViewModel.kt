package mufanc.tools.applock.fragment.apps

import android.app.Activity
import android.content.Intent
import android.content.pm.ApplicationInfo
import android.content.pm.PackageManager
import android.graphics.drawable.Drawable
import androidx.lifecycle.ViewModel
import androidx.preference.PreferenceManager
import mufanc.tools.applock.R
import kotlin.concurrent.thread

class AppsViewModel : ViewModel() {
    data class AppInfo(
        val appName: String,
        val packageName: String,
        val icon: Drawable
    )

    val appList = mutableSetOf<AppInfo>()

    fun loadAppList(activity: Activity, refresh: Boolean = false, callback: () -> Unit) {
        thread {
            if (refresh || appList.isEmpty()) {
                val packageManager = activity.packageManager
                val mode = PreferenceManager.getDefaultSharedPreferences(activity)
                    .getString(
                        "resolve_mode",
                        activity.resources.getStringArray(R.array.resolve_mode_values)[0]
                    )
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