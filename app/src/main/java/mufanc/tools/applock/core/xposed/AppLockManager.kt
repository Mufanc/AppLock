package mufanc.tools.applock.core.xposed

import android.app.ActivityThread
import android.content.Context
import android.net.Uri
import android.os.*
import androidx.core.os.bundleOf
import mufanc.easyhook.api.EasyHook
import mufanc.easyhook.api.Logger
import mufanc.easyhook.api.hook
import mufanc.tools.applock.BuildConfig
import mufanc.tools.applock.IAppLockManager
import mufanc.tools.applock.MyApplication

class AppLockManager private constructor() : IAppLockManager.Stub() {

    companion object {
        private val TRANSACTION_CODE = "Lock"
            .toByteArray()
            .mapIndexed { i, ch -> ch.toInt() shl (i * 8) }
            .sum()

        private val instance by lazy { AppLockManager() }
        fun query(packageName: String): Boolean {
            return instance.whitelist.contains(packageName)
        }

        private val context by lazy {
            ActivityThread.currentActivityThread().systemContext as Context
        }

        fun init() = EasyHook.handle {  // Hook `onTransact()` 以便与模块通信
            onLoadPackage("android") {
                findClass("miui.process.ProcessManagerNative").hook {
                    method({ name == "onTransact" }) {
                        before { param ->
                            if (param.args[0] != TRANSACTION_CODE) return@before
                            if (context.packageManager.getNameForUid(Binder.getCallingUid()) != BuildConfig.APPLICATION_ID) return@before
                            (param.args[2] as Parcel).writeStrongBinder(instance)
                            param.result = true
                        }
                    }
                }
            }
        }

        val client by lazy {
            MyApplication.processManager?.let {
                val data = Parcel.obtain()
                val reply = Parcel.obtain()
                try {
                    if (it.transact(TRANSACTION_CODE, data, reply, 0)) {
                        return@lazy asInterface(reply.readStrongBinder())
                    }
                } finally {
                    data.recycle()
                    reply.recycle()
                }
                return@lazy null
            }
        }
    }

    private val whitelist: MutableSet<String> by lazy {
        var result = mutableSetOf<String>()
        Binder.restoreCallingIdentity(Binder.clearCallingIdentity().also {
            context.contentResolver.call(
                Uri.parse("content://${BuildConfig.APPLICATION_ID}.provider"),
                "scope", null, null
            )?.getStringArray("scope")?.also {
                result = it.toMutableSet()
                Logger.i("@AppLock: load scope from provider: ${it.contentToString()}")
            } ?: let {
                Logger.w("@AppLock: failed to resolve whitelist!")
            }
        })
        result
    }

    override fun handshake(): Bundle {
        Logger.i("@AppLock: handshake from client!")
        return bundleOf(
            "pid" to Process.myPid(),
            "uid" to Process.myUid(),
            "version" to BuildConfig.VERSION_CODE
        )
    }

    override fun reboot() {
        Binder.restoreCallingIdentity(
            Binder.clearCallingIdentity().also {
                IPowerManager.Stub.asInterface(
                    ServiceManager.getService(Context.POWER_SERVICE)
                ).reboot(false, null, false)
            }
        )
    }

    override fun updateWhitelist(packageList: Array<out String>) {
        whitelist.clear()
        whitelist.addAll(packageList)
        Logger.i("@AppLock: scope updated: ${packageList.contentToString()}")
    }
}