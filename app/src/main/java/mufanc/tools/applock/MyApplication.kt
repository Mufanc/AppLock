package mufanc.tools.applock

import android.app.Application
import android.content.SharedPreferences
import android.os.IBinder
import android.os.ServiceManager
import androidx.preference.PreferenceManager
import org.lsposed.hiddenapibypass.HiddenApiBypass
import rikka.sui.Sui

class MyApplication : Application() {
    companion object {
        init {
            HiddenApiBypass.setHiddenApiExemptions("")
        }

        @JvmStatic
        var isModuleActivated = false

        val processManager: IBinder? = ServiceManager.getService("ProcessManager")

        lateinit var prefs: SharedPreferences
    }

    override fun onCreate() {
        super.onCreate()
        prefs = PreferenceManager.getDefaultSharedPreferences(this)
        Sui.init(BuildConfig.APPLICATION_ID)
    }
}
