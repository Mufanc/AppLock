package mufanc.tools.applock.fragment.apps

import android.app.Activity
import android.content.pm.ApplicationInfo
import android.graphics.drawable.Drawable
import androidx.lifecycle.ViewModel
import kotlin.concurrent.thread

class AppsViewModel : ViewModel() {
    data class AppInfo(
        val appName: String,
        val packageName: String,
        val icon: Drawable
    )

    lateinit var appList: List<AppInfo>

    fun loadAppList(activity: Activity, callback: () -> Unit) {
        thread {
            if (!this::appList.isInitialized) {
                val packageManager = activity.packageManager
                appList = packageManager
                    .getInstalledApplications(0)
                    .filter { it.flags and ApplicationInfo.FLAG_SYSTEM == 0 }
                    .map {
                        AppInfo(
                            it.loadLabel(packageManager).toString(),
                            it.packageName,
                            it.loadIcon(packageManager)
                        )
                    }
            }
            activity.runOnUiThread(callback)
        }
    }
}