package mufanc.tools.applock

import android.annotation.SuppressLint
import android.app.Application
import android.content.Context
import android.content.SharedPreferences
import android.os.IBinder
import android.os.ServiceManager
import androidx.preference.PreferenceManager
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

        @SuppressLint("StaticFieldLeak")
        lateinit var context: Context
    }

    override fun onCreate() {
        super.onCreate()
        Sui.init(BuildConfig.APPLICATION_ID)
        prefs = PreferenceManager.getDefaultSharedPreferences(this)
        context = applicationContext
    }
}
