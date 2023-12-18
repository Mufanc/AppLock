package xyz.mufanc.applock

import android.app.Application

class App : Application() {

    companion object {
        val isDebug by lazy { BuildConfig.DEBUG }
    }

}