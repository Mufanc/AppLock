package mufanc.tools.applock.fragment.home

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import mufanc.tools.applock.BuildConfig
import mufanc.tools.applock.MyApplication

class HomeViewModel : ViewModel() {
    val isModuleActivated = MyApplication.isModuleActivated
    val isServiceFound = MyApplication.processManager != null
    val isHookerWorking by lazy {
        replyFromHook.value = "pid:12345, uid:1000"
        true
    }

    val versionName = BuildConfig.VERSION_NAME
    val managerName = "${MyApplication.processManager}".removePrefix("android.os.")
    val replyFromHook = MutableLiveData("Testing...")
}
