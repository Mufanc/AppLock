package xyz.mufanc.applock

import android.app.Application
import android.content.SharedPreferences
import androidx.preference.PreferenceManager
import io.github.libxposed.service.XposedService
import io.github.libxposed.service.XposedServiceHelper
import io.github.libxposed.service.XposedServiceHelper.OnServiceListener
import kotlinx.coroutines.flow.MutableStateFlow
import xyz.mufanc.applock.core.util.Log
import xyz.mufanc.applock.util.FrameworkInfo
import xyz.mufanc.applock.util.I18n

class App : Application() {

    companion object {
        private const val TAG = "App"

        val frameworkInfo = MutableStateFlow<FrameworkInfo?>(null)

        lateinit var instance: Application
        lateinit var prefs: SharedPreferences
    }

    override fun onCreate() {
        super.onCreate()

        Log.i(TAG, "onCreate called")

        I18n.init(applicationContext)

        XposedServiceHelper.registerListener(
            object : OnServiceListener {
                override fun onServiceBind(service: XposedService) {
                    frameworkInfo.value = FrameworkInfo(service)
                    Log.i(TAG, "onServiceBind: $service")
                }

                override fun onServiceDied(service: XposedService) {
                    frameworkInfo.value = null
                    Log.i(TAG, "onServiceDied: $service")
                }
            }
        )

        instance = this
        prefs = PreferenceManager.getDefaultSharedPreferences(this)
    }
}
