package mufanc.tools.applock.hook

import com.github.mufanc.easyhook.HookHelper
import com.github.mufanc.easyhook.util.Log
import com.github.mufanc.easyhook.util.findField
import mufanc.tools.applock.BuildConfig
import mufanc.tools.applock.MyApplication

class HookEntry : HookHelper("AppLock") {
    override fun onHandleLoadPackage() {
        when (lpparam.packageName) {
            BuildConfig.APPLICATION_ID ->
                findField(MyApplication::class.java.name) {
                    name == "isModuleActivated"
                }!!.set(null, true)
            "android" ->
                Log.i("")
        }
    }
}