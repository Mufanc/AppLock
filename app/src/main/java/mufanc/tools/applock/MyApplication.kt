package mufanc.tools.applock

import android.app.Application
import android.os.IBinder
import android.os.ServiceManager
import mufanc.easyhook.api.Logger
import org.lsposed.hiddenapibypass.HiddenApiBypass
import rikka.sui.Sui

class MyApplication : Application() {
    companion object {
        const val TAG = "AppLock"

        @JvmStatic
        var isModuleActivated = false

        val processManager: IBinder? = ServiceManager.getService("ProcessManager")

        init {
            HiddenApiBypass.setHiddenApiExemptions("")
            Logger.configure(TAG = TAG)
        }

        lateinit var context: Application
    }

    override fun onCreate() {
        super.onCreate()
        context = this
        Sui.init(BuildConfig.APPLICATION_ID)
    }
}
