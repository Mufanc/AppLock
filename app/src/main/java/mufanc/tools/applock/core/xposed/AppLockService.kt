package mufanc.tools.applock.core.xposed

import android.app.ActivityThread
import android.content.Context
import android.os.*
import android.util.ArrayMap
import mufanc.easyhook.api.EasyHook
import mufanc.easyhook.api.Logger
import mufanc.easyhook.api.catch
import mufanc.easyhook.api.hook.hook
import mufanc.easyhook.api.reflect.getStaticFieldAs
import mufanc.easyhook.api.signature
import mufanc.tools.applock.BuildConfig
import mufanc.tools.applock.IAppLockService
import mufanc.tools.applock.util.channel.ConfigProvider
import mufanc.tools.applock.util.channel.Configs
import mufanc.tools.applock.util.channel.Handshake
import mufanc.tools.applock.util.update
import kotlin.concurrent.thread

class AppLockService private constructor() : IAppLockService.Stub() {

    companion object {
        private val INSTANCE by lazy { AppLockService() }
        fun query(packageName: String): Pair<Boolean, Int> {
            return Pair(
                INSTANCE.scope.contains(packageName),
                INSTANCE.killLevel
            )
        }

        private val context by lazy {
            @Suppress("Cast_Never_Succeeds")
            ActivityThread.currentActivityThread().systemContext as Context
        }

        fun init() = EasyHook.handle {  // Hook `onTransact()` 以便与模块通信
            onLoadPackage("android") {
                findClass("android.app.IApplicationThread\$Stub\$Proxy").hook {
                    method({ name == "bindApplication" }) { method ->
                        Logger.i("@Hooker: hook bindApplication: ${method.signature()}")
                        val index = method.parameterTypes.indexOf(Map::class.java)
                        before { param ->
                            if (param.args[0] != BuildConfig.APPLICATION_ID) return@before
                            @Suppress("Unchecked_Cast")
                            (param.args[index] as ArrayMap<String, IBinder>)[BuildConfig.APPLICATION_ID] = INSTANCE
                            Logger.i("@Server: put service cache!")
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
                                        INSTANCE.scope.addAll(configs.scope)
                                        INSTANCE.killLevel = configs.killLevel
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }

        val client by lazy {
            ServiceManager.getService(BuildConfig.APPLICATION_ID)?.let {
                asInterface(it)
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
