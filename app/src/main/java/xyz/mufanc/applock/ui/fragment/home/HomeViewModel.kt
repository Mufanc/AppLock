package xyz.mufanc.applock.ui.fragment.home

import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.asLiveData
import androidx.lifecycle.map
import xyz.mufanc.applock.App
import xyz.mufanc.applock.BuildConfig
import xyz.mufanc.applock.util.I18n

class HomeViewModel : ViewModel() {

    private val fwInfo = App.frameworkInfo.asLiveData()

    val isModuleLoaded = fwInfo.map { it != null }

    val moduleLoadState: LiveData<String> = fwInfo.map { info ->
        if (info != null) {
            I18n.strings.homeStatusModuleLoaded
        } else {
            I18n.strings.homeStatusModuleNotLoaded
        }
    }

    val frameworkInfo = fwInfo.map { info ->
        if (info != null) {
            "${info.name} API ${info.apiVersion} (${info.version}-${info.versionCode})"
        } else {
            BuildConfig.VERSION_NAME
        }
    }
}
