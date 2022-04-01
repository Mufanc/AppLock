package mufanc.tools.applock

import android.app.Application

class MyApplication : Application() {
    companion object {
        @JvmStatic
        var isModuleActivated = false
    }
}