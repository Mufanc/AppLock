package mufanc.tools.applock.fragment.home

import android.os.SystemProperties
import androidx.lifecycle.ViewModel
import mufanc.tools.applock.BuildConfig
import mufanc.tools.applock.MyApplication
import mufanc.tools.applock.xposed.AppLockHelper

class HomeViewModel : ViewModel() {

    val isModuleActivated = MyApplication.isModuleActivated
    val versionName = BuildConfig.VERSION_NAME

    val isServiceFound = MyApplication.processManager != null
    val managerBinder = "${MyApplication.processManager}".removePrefix("android.os.")

    val isHookerWorking = AppLockHelper.client != null
    var requireReboot = false
    val replyFromHook = AppLockHelper.client?.let {
        val (coreVersion, pid, uid) = it.handshake()
        requireReboot = coreVersion < BuildConfig.VERSION_CODE
        "pid:$pid, uid:$uid"
    } ?: "failed."

    private val miuiVersion = SystemProperties.get("ro.miui.ui.version.code")
    val isMiuiRom = miuiVersion.isNotEmpty()
    val versionSummary = "Version: " + miuiVersion.ifBlank { "Unknown." }
}
