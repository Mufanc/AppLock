package mufanc.tools.applock.fragment.home

import androidx.lifecycle.ViewModel
import mufanc.tools.applock.BuildConfig
import mufanc.tools.applock.MyApplication
import mufanc.tools.applock.xposed.AppLockHelper

class HomeViewModel : ViewModel() {
    val isModuleActivated = MyApplication.isModuleActivated
    val isServiceFound = MyApplication.processManager != null
    val isHookerWorking = AppLockHelper.client != null

    val versionName = BuildConfig.VERSION_NAME
    val managerName = "${MyApplication.processManager}".removePrefix("android.os.")
    val replyFromHook = AppLockHelper.client?.let {
        val reply = it.handshake()
        "pid:${reply[0]}, uid:${reply[1]}"
    } ?: "failed."
}
