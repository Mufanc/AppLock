package mufanc.tools.applock.xposed

import com.github.mufanc.easyhook.HookHelper
import com.github.mufanc.easyhook.util.catch
import com.github.mufanc.easyhook.util.findField
import mufanc.tools.applock.BuildConfig
import mufanc.tools.applock.MyApplication

class HookEntry : HookHelper(MyApplication.TAG) {
    override fun onHandleLoadPackage() {
        catch {
            when (lpparam.packageName) {
                BuildConfig.APPLICATION_ID -> {
                    findField(MyApplication::class.java.name) {
                        name == "isModuleActivated"
                    }!!.set(null, true)
                }
                "android" -> AppLockHelper.main()
            }
        }
    }
}