package mufanc.tools.applock

import android.app.Application
import android.os.IBinder
import android.os.ServiceManager
import org.lsposed.hiddenapibypass.HiddenApiBypass

class MyApplication : Application() {

    companion object {
        init {
            HiddenApiBypass.setHiddenApiExemptions("")
        }

        @JvmStatic
        var isModuleActivated = false
        var processManager: IBinder? = ServiceManager.getService("ProcessManager")
    }

    override fun onCreate() {
        super.onCreate()
    }
}