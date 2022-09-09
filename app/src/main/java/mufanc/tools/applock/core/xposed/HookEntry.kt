package mufanc.tools.applock.core.xposed

import mufanc.easyhook.api.Logger
import mufanc.easyhook.api.annotation.XposedEntry
import mufanc.easyhook.api.hook.HookHelper
import mufanc.easyhook.api.reflect.findField
import mufanc.tools.applock.App
import mufanc.tools.applock.BuildConfig

@XposedEntry
class HookEntry : HookHelper(App.TAG) {
    override fun onHook() {
        if (BuildConfig.DEBUG) {
            Logger.configure(target = +Logger.Target.XPOSED_BRIDGE, level = Logger.Level.TRACE)
        }
        handle {
            // 改变模块激活状态  Todo: 整合到 EasyHook
            onLoadPackage(BuildConfig.APPLICATION_ID) {
                findClass(App::class.java.name)
                    .findField { name == "isModuleActivated" }!!
                    .set(null, true)
                Logger.i("@Module: update module activation status")
            }
        }
        AppLockHelper.init()
    }
}
