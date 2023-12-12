package xyz.mufanc.applock.core.process.hook

import io.github.libxposed.api.XposedInterface
import xyz.mufanc.applock.core.process.bean.KillInfo

class HookAndroidS : BaseHooker() {
    override fun dump(callback: XposedInterface.BeforeHookCallback): KillInfo {
        TODO("Not yet implemented")
    }
}
