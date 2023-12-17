package xyz.mufanc.applock.core.process.model

import io.github.libxposed.api.XposedInterface
import xyz.mufanc.applock.core.util.ApiAdapter

object KillInfoFactory : ApiAdapter<XposedInterface.BeforeHookCallback, KillInfo>() {

    fun create(callback: XposedInterface.BeforeHookCallback): KillInfo {
        return adapt(callback)
    }

    override fun doAndroidP(from: XposedInterface.BeforeHookCallback): KillInfo {
        return KillInfo(
            reason = from.args[0] as String,
            description = "",
            reasonCode = -1,
            subReason = -1,
            processInfo = ProcessInfoFactory.create(from.thisObject!!)
        )
    }

    override fun doAndroidQ(from: XposedInterface.BeforeHookCallback): KillInfo {
        TODO("Not yet implemented")
    }

    override fun doAndroidR(from: XposedInterface.BeforeHookCallback): KillInfo {
        TODO("Not yet implemented")
    }

    override fun doAndroidS(from: XposedInterface.BeforeHookCallback): KillInfo {
        return KillInfo(
            reason = from.args[0] as String,
            description = "",
            reasonCode = from.args[1] as Int,
            subReason = from.args[2] as Int,
            processInfo = ProcessInfoFactory.create(from.thisObject!!)
        )
    }

    override fun doAndroidT(from: XposedInterface.BeforeHookCallback): KillInfo {
        return KillInfo(
            reason = from.args[0] as String,
            description = from.args[1] as String,
            reasonCode = from.args[2] as Int,
            subReason = from.args[3] as Int,
            processInfo = ProcessInfoFactory.create(from.thisObject!!)
        )
    }

    override fun doAndroidU(from: XposedInterface.BeforeHookCallback): KillInfo {
        TODO("Not yet implemented")
    }
}
