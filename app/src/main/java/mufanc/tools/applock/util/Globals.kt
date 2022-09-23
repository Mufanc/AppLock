package mufanc.tools.applock.util

import android.os.SystemProperties
import androidx.lifecycle.MutableLiveData
import mufanc.tools.applock.App
import mufanc.tools.applock.BuildConfig
import mufanc.tools.applock.core.shizuku.ShizukuHelper
import mufanc.tools.applock.core.xposed.AppLockService
import mufanc.tools.applock.util.channel.Handshake

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
    val isModuleActivated = MutableLiveData(App.isModuleActivated)
    val moduleVersion = MutableLiveData(BuildConfig.VERSION_NAME)

    // ProcessManager
    val isProcessManagerFound = MutableLiveData(
        App.processManager?.pingBinder() == true
    )
    val serviceInfo = MutableLiveData(
        App.processManager?.let {
            "${"$it".removePrefix("android.os.")} (${it.getRemotePid()})"
        } ?: "Not Found."
    )

    // AppLock service
    val isHookerWorking = MutableLiveData(AppLockService.client != null)
    var isServiceVersionOutdated = false
    val hookerInfo = MutableLiveData(
        AppLockService.client?.handshake()?.let {
            val handshake = Handshake(it)
            isServiceVersionOutdated = handshake.version < BuildConfig.VERSION_CODE
            "Pid:${handshake.pid}, Uid:${handshake.uid}"
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
