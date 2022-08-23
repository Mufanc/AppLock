package mufanc.tools.applock.util

import android.os.SystemProperties
import androidx.lifecycle.MutableLiveData
import mufanc.tools.applock.BuildConfig
import mufanc.tools.applock.MyApplication
import mufanc.tools.applock.core.shizuku.ShizukuHelper
import mufanc.tools.applock.core.xposed.AppLockManager
import mufanc.tools.applock.core.xposed.AppLockManager.Companion.BundleKeys

object Globals {
    // MIUI
    val isMiuiRom = MutableLiveData(SystemProperties.get("ro.miui.ui.version.code").isNotEmpty())
    val miuiVersion = MutableLiveData(
        if (isMiuiRom.value!!) {
            SystemProperties.get("ro.build.version.incremental")
        } else {
            "Unknown."
        }
    )

    // Xposed
    val isModuleActivated = MutableLiveData(MyApplication.isModuleActivated)
    val moduleVersion = MutableLiveData(BuildConfig.VERSION_NAME)

    // ProcessManager
    val isProcessManagerFound = MutableLiveData(
        MyApplication.processManager?.pingBinder() == true
    )
    val serviceInfo = MutableLiveData(
        MyApplication.processManager?.let {
            "${"$it".removePrefix("android.os.")} (${it.getRemotePid()})"
        } ?: "Not Found."
    )

    // AppLock service
    val isHookerWorking = MutableLiveData(AppLockManager.client != null)
    var isServiceVersionOutdated = false
    val hookerInfo = MutableLiveData(
        AppLockManager.client?.handshake()?.run {
            isServiceVersionOutdated = getInt(BundleKeys.VERSION.name) < BuildConfig.VERSION_CODE
            "Pid:${getInt(BundleKeys.PID.name)}, Uid:${getInt(BundleKeys.UID.name)}"
        } ?: "Failed."
    )

    // Shizuku
    val isShizukuAvailable = MutableLiveData(ShizukuHelper.checkPermission())
    val shizukuSelinuxContext = MutableLiveData(ShizukuHelper.getSelinuxContext())
    fun requestShizukuPermission() {
        ShizukuHelper.requestPermission {
            isShizukuAvailable.value = ShizukuHelper.checkPermission()
            shizukuSelinuxContext.value = ShizukuHelper.getSelinuxContext()
        }
    }
}