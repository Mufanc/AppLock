package mufanc.tools.applock

import android.annotation.SuppressLint
import android.app.Application
import android.content.Context
import android.content.SharedPreferences
import android.os.IBinder
import android.os.ServiceManager
import android.widget.Toast
import androidx.preference.PreferenceManager
import mufanc.tools.applock.util.ScopeDatabase
import mufanc.tools.applock.xposed.AppLockHelper
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

        AppLockHelper.client?.let {
            val scope = it.importScopeFromOldVersion()
            if (scope.isNotEmpty()) {
                ScopeDatabase.writeScope(scope.toMutableSet())
                Toast.makeText(this, R.string.imported_scope, Toast.LENGTH_SHORT).show()
            }
        }
    }
}
