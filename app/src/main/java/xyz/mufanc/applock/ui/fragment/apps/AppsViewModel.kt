package xyz.mufanc.applock.ui.fragment.apps

import android.content.SharedPreferences
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.asLiveData
import xyz.mufanc.applock.App
import xyz.mufanc.applock.bean.AppInfo
import xyz.mufanc.applock.ui.util.AppsHelper

class AppsViewModel : ViewModel() {
    val apps: LiveData<List<AppInfo>> = AppsHelper.getAppList().asLiveData()
    val loading = MutableLiveData(true)
    val scopePrefs: LiveData<SharedPreferences?> = App.scopePrefs.asLiveData()
    val query = MutableLiveData("")
}
