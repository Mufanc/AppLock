package xyz.mufanc.applock.core.scope.provider

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.os.PowerManager
import android.os.ServiceManager
import android.util.ArrayMapStub
import io.github.libxposed.api.XposedInterface
import org.joor.Reflect
import xyz.mufanc.applock.core.util.ContextHelper
import xyz.mufanc.applock.core.util.Log

@Suppress("Unused")
data object PowerManagerProvider : ScopeProvider() {

    private const val TAG = "DeviceIdleProvider"

    private var whitelistApps: ArrayMapStub<String, *>? = null

    private val whitelistChangedReceiver = object : BroadcastReceiver() {
        override fun onReceive(context: Context?, intent: Intent?) {
            val apps = whitelistApps ?: return
            emit(apps.keys)
        }
    }
    private val whitelistChangedFilter = IntentFilter(
        Reflect.onClass(PowerManager::class.java).get<String>("ACTION_POWER_SAVE_WHITELIST_CHANGED")
    )

    override fun isAvailable(): Boolean {
        val binderService = ServiceManager.getService("deviceidle") ?: return false

        val serviceClass = binderService.javaClass
        val controllerField = serviceClass.declaredFields.find { field ->
            field.name.matches("this\\$\\d+".toRegex())
        } ?: return false

        val controller = controllerField.get(binderService)
        whitelistApps = Reflect.on(controller).get("mPowerSaveWhitelistUserApps")

        return whitelistApps != null
    }

    override fun init(ixp: XposedInterface) {
//        Reflect.on(apps).set("shadow\$_klass_", ArrayMapProxy::class.java)
        ContextHelper.systemContext()
            .registerReceiver(whitelistChangedReceiver, whitelistChangedFilter)

        Log.d(TAG, "register receiver")
    }

//    class ArrayMapProxy<K, V> : ArrayMapStub<K, V>() {
//        override fun put(key: K, value: V): V? {
//            Log.d(TAG, "+ $key")
//            return super.put(key, value)
//        }
//
//        override fun remove(key: K): V? {
//            Log.d(TAG, "- $key")
//            return super.remove(key)
//        }
//    }
}
