package xyz.mufanc.applock.ui.util

import android.content.Intent
import android.content.pm.ApplicationInfo
import android.content.pm.PackageManager
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.map
import xyz.mufanc.applock.App

object AppsHelper {

    private val pm = App.instance.packageManager

    enum class ResolveMode {
        LAUNCHER, NON_SYSTEM, ALL
    }

    private val currentMode = MutableStateFlow(ResolveMode.LAUNCHER)

    private val apps: Flow<List<ApplicationInfo>> = currentMode.map { mode ->
        when (mode) {
            ResolveMode.LAUNCHER -> {
                pm
                    .queryIntentActivities(
                        Intent(Intent.ACTION_MAIN).addCategory(Intent.CATEGORY_LAUNCHER),
                        PackageManager.MATCH_ALL or PackageManager.MATCH_DISABLED_COMPONENTS
                    )
                    .map { it.activityInfo.applicationInfo }
            }
            ResolveMode.NON_SYSTEM -> {
                pm
                    .getInstalledApplications(PackageManager.MATCH_DISABLED_COMPONENTS)
                    .filter { it.flags and ApplicationInfo.FLAG_SYSTEM == 0 }
            }
            ResolveMode.ALL -> {
                pm.getInstalledApplications(PackageManager.MATCH_DISABLED_COMPONENTS)
            }
        }
            .distinctBy { it.packageName }
    }

    fun setResolveMode(mode: ResolveMode) {
        currentMode.value = mode
    }

    fun getAppList(): Flow<List<ApplicationInfo>> = apps
}
