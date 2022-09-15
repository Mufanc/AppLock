package mufanc.tools.applock.core.xposed

import android.app.ActivityThread
import android.content.Context
import android.os.*
import mufanc.easyhook.api.EasyHook
import mufanc.easyhook.api.Logger
import mufanc.easyhook.api.catch
import mufanc.easyhook.api.hook.hook
import mufanc.easyhook.api.reflect.getStaticFieldAs
import mufanc.easyhook.api.signature
import mufanc.tools.applock.App
import mufanc.tools.applock.BuildConfig
import mufanc.tools.applock.IAppLockService
import mufanc.tools.applock.util.channel.ConfigProvider
import mufanc.tools.applock.util.channel.Configs
import mufanc.tools.applock.util.channel.Handshake
import mufanc.tools.applock.util.update
import kotlin.concurrent.thread

class AppLockService private constructor() : IAppLockService.Stub() {

    companion object {
        private val TRANSACTION_CODE = "Lock"
            .toByteArray()
            .mapIndexed { i, ch -> ch.toInt() shl (i * 8) }
            .sum()

        private val instance by lazy { AppLockService() }
        fun query(packageName: String): Pair<Boolean, Int> {
            return Pair(
                instance.scope.contains(packageName),
                instance.killLevel
            )
        }

        private val context by lazy {
            @Suppress("Cast_Never_Succeeds")
            ActivityThread.currentActivityThread().systemContext as Context
        }

        fun init() = EasyHook.handle {  // Hook `onTransact()` 以便与模块通信
            onLoadPackage("android") {
                findClass("miui.process.ProcessManagerNative").hook {
                    method({ name == "onTransact" }) { method ->
                        Logger.i("@Hooker: hook onTransact: ${method.signature()}")
                        before { param ->
                            if (param.args[0] != TRANSACTION_CODE) return@before
                            if (context.packageManager.getNameForUid(Binder.getCallingUid()) != BuildConfig.APPLICATION_ID) return@before
                            (param.args[2] as Parcel).writeStrongBinder(instance)
                            param.result = true
                        }
                    }
                }

                findClass("com.android.server.SystemServiceManager").hook {
                    method({ name == "startBootPhase" }) { method ->
                        Logger.i("@Hooker: hook startBootPhase: ${method.signature()}")

                        val index = method.parameterTypes.indexOf(Int::class.java)
                        val code = findClass("com.android.server.SystemService")
                            .getStaticFieldAs<Int>("PHASE_BOOT_COMPLETED")

                        after { param ->
                            if (param.args[index] == code) {  // 系统启动完成
                                thread {
                                    catch {
                                        val configs = ConfigProvider.fetch(context)
                                        instance.scope.addAll(configs.scope)
                                        instance.killLevel = configs.killLevel
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }

        val client by lazy {
            App.processManager?.let {
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

    private val scope = mutableSetOf<String>()
    private var killLevel: Int = 101  // TRIM_MEMORY

    override fun handshake(): Bundle {
        Logger.i("@Server: handshake from client!")
        return Handshake(Process.myPid(), Process.myUid(), BuildConfig.VERSION_CODE).asBundle()
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

    override fun updateConfigs(bundle: Bundle) {
        val configs = Configs(bundle)

        val newScope = configs.scope.toSet()
        if (scope != newScope) {
            scope.update(newScope)
            Logger.i("@Server: scope updated: $scope")
        }

        if (killLevel != configs.killLevel) {
            killLevel = configs.killLevel
            Logger.i("@Server: kill-level updated: $killLevel (${AppLockHelper.killLevelToString(killLevel)})")
        }
    }
}
