package xyz.mufanc.applock.ui

import androidx.lifecycle.ViewModel
import androidx.lifecycle.asLiveData
import xyz.mufanc.applock.App

class MainViewModel : ViewModel() {
    val frameworkInfo = App.frameworkInfo.asLiveData()
}
