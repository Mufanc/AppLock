package xyz.mufanc.applock.ui.fragment.apps

import androidx.lifecycle.ViewModel
import androidx.lifecycle.asLiveData
import xyz.mufanc.applock.ui.util.AppsHelper

class AppsViewModel : ViewModel() {
    val apps = AppsHelper.getAppList().asLiveData()
}
