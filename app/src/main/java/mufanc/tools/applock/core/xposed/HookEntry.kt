package mufanc.tools.applock.core.xposed

import mufanc.easyhook.api.Logger
import mufanc.easyhook.api.hook.HookHelper
import mufanc.easyhook.api.annotation.XposedEntry
import mufanc.easyhook.api.reflect.findField
import mufanc.tools.applock.BuildConfig
import mufanc.tools.applock.MyApplication

@XposedEntry
class HookEntry : HookHelper(MyApplication.TAG) {
    override fun onHook() {
        handle {
            // 改变模块激活状态  Todo: 整合到 EasyHook
            onLoadPackage(BuildConfig.APPLICATION_ID) {
                findClass(MyApplication::class.java.name)
                    .findField { name == "isModuleActivated" }!!
                    .set(null, true)
                Logger.i("@Client: update module activation status")
            }
        }
        AppLockHelper.init()
    }
}
