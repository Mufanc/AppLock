package xyz.mufanc.applock.core

import android.annotation.SuppressLint
import android.os.Bundle
import android.os.IBinder
import io.github.libxposed.api.XposedInterface
import io.github.libxposed.api.XposedInterface.BeforeHookCallback
import io.github.libxposed.api.annotations.BeforeInvocation
import io.github.libxposed.api.annotations.XposedHooker
import xyz.mufanc.applock.BuildConfig
import xyz.mufanc.applock.IAppLockService
import xyz.mufanc.applock.core.util.GraftClassLoader
import xyz.mufanc.applock.core.util.Log
import java.lang.reflect.Method

object AppLockService : IAppLockService.Stub() {

    private const val TAG = "AppLockService"

    override fun handshake(): Bundle {
        return Bundle()
    }

    @SuppressLint("PrivateApi")
    fun init(ixp: XposedInterface) {
        ixp.hook(
            GraftClassLoader.loadClass("android.app.IApplicationThread\$Stub\$Proxy")
                .declaredMethods
                .find { it.name == "bindApplication" }!!,
            BindApplicationHook::class.java
        )
    }

    @XposedHooker
    class BindApplicationHook : XposedInterface.Hooker {
        companion object {

            private var index = -1

            @BeforeInvocation
            @JvmStatic
            @Suppress("Unused", "Unchecked_Cast")
            fun handle(callback: BeforeHookCallback): BindApplicationHook? {
                if (index == -1) {
                    val method = callback.member as Method
                    index = method.parameterTypes.indexOf(Map::class.java)
                }

                val pkg = callback.args[0]
                if (pkg == BuildConfig.APPLICATION_ID) {
                    val serviceCache = callback.args[index] as MutableMap<String, IBinder>
                    serviceCache["applock"] = AppLockService

                    Log.i(TAG, "push applock service to manager!")
                }

                return null
            }
        }
    }
}
