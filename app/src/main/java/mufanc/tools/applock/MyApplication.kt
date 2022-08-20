package mufanc.tools.applock

import android.app.Application
import android.content.SharedPreferences
import android.os.IBinder
import android.os.ServiceManager
import androidx.preference.PreferenceManager
import mufanc.tools.applock.util.ScopeManager
import org.lsposed.hiddenapibypass.HiddenApiBypass
import rikka.sui.Sui

class MyApplication : Application() {
    companion object {
        init { HiddenApiBypass.setHiddenApiExemptions("") }

        const val TAG = "AppLock"

        @JvmStatic
        var isModuleActivated = false

        val processManager: IBinder? = ServiceManager.getService("ProcessManager")

        lateinit var prefs: SharedPreferences

        lateinit var context: Application
    }

    override fun onCreate() {
        super.onCreate()
        context = this
        ScopeManager.init(context)
        Sui.init(BuildConfig.APPLICATION_ID)
        prefs = PreferenceManager.getDefaultSharedPreferences(this)
    }
}
