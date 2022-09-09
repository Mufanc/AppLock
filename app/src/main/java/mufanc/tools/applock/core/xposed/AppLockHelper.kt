package mufanc.tools.applock.core.xposed

import android.app.IActivityManager
import android.content.Context
import android.os.Binder
import android.os.Build
import android.os.ServiceManager
import android.util.ArrayMap
import android.util.SparseArray
import mufanc.easyhook.api.EasyHook
import mufanc.easyhook.api.LoaderContext
import mufanc.easyhook.api.Logger
import mufanc.easyhook.api.hook.hook
import mufanc.easyhook.api.reflect.findField
import mufanc.easyhook.api.reflect.findMethod
import mufanc.easyhook.api.reflect.findMethods
import mufanc.easyhook.api.reflect.getField
import mufanc.tools.applock.BuildConfig
import mufanc.tools.applock.util.signature
import java.lang.reflect.Field
import java.lang.reflect.Method

object AppLockHelper {

    private val KILLERS = setOf("com.miui.home", "com.android.systemui", "system")

    private val processMaps: SparseArray<*> by lazy {
        IActivityManager.Stub.asInterface(
            ServiceManager.getService(Context.ACTIVITY_SERVICE)
        ).getField("mPidsSelfLocked")!!.let {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
                it.getField("mPidMap")
            } else {
                it
            }
        } as SparseArray<*>
    }

    private lateinit var packageListField1: Field
    private lateinit var packageListField2: Field
    private lateinit var getKeySetMethod: Method

    private fun getPackageList(processRecord: Any): Set<String> {
        if (!::packageListField1.isInitialized) {
            packageListField1 = processRecord.javaClass.findField {
                name == "pkgList" || name == "mPkgList"
            }!!
        }
        val pkgList = packageListField1.get(processRecord)
        @Suppress("Unchecked_Cast")
        return if (pkgList is ArrayMap<*, *>) {  // Android 9
            pkgList.keys as Set<String>
        } else {  // Android 10+
            if (!::packageListField2.isInitialized) {
                packageListField2 = pkgList.javaClass.findField { name == "mPkgList" }!!
                getKeySetMethod = packageListField2.get(pkgList).javaClass.findMethod{ name == "keySet" }!!
            }
            getKeySetMethod.invoke(packageListField2.get(pkgList)) as Set<String>
        }
    }

    // decompiled from miui-services.jar
    fun killLevelToString(level: Int): String {
        return when (level) {
            100 -> "none"
            101 -> "trim-memory"
            102 -> "kill-background"
            103 -> "kill"
            104 -> "force-stop"
            else -> "unknown"
        }
    }

    private fun hookKillOnce(loader: LoaderContext, method: Method) = loader.apply {
        method.hook {
            Logger.i("@Hooker: hook killOnce: ${method.signature()}")
            val processNameField = findClass("com.android.server.am.ProcessRecord").findField {
                name == "processName"
            }!!
            before { param ->
                val processRecord = param.args[0]
                val killLevel = param.args[2] as Int
                val processName = processNameField.get(processRecord)
                val killer = processNameField.get(processMaps.get(Binder.getCallingPid())) ?: run {
                    Logger.w("No killer found for process: $processName, skipped!")
                    return@before
                }
                if (KILLERS.contains(killer)) {
                    getPackageList(processRecord).forEach {
                        val (isProtected, killLevelTarget) = AppLockService.query(it)
                        if (isProtected) {
                            param.args[2] = killLevelTarget
                            Logger.i("@AppLock: protected $processName " +
                                    "(${killLevelToString(killLevel)} -> ${killLevelToString(killLevelTarget)})")
                            return@before
                        }
                    }
                }
                if (BuildConfig.DEBUG) {
                    Logger.v("@AppLock: [$killer] killing $processName (${killLevelToString(killLevel)})")
                }
            }
        }
    }

    fun init() {
        AppLockService.init()
        EasyHook.handle {
            // Hook `ProcessManagerService` 实现应用免杀
            onLoadPackage("android") {
                val filter = fun (method: Method): Boolean {
                    return method.name == "killOnce" && method.parameterTypes[0].simpleName == "ProcessRecord"
                }

                hookKillOnce(
                    this,
                    findClass("com.android.server.am.ProcessManagerService")
                        .findMethods(filter)
                        .maxByOrNull {
                            it.parameterCount
                        } ?: run {
                            findClass("com.android.server.am.ProcessCleanerBase")
                                .findMethods(filter)
                                .minByOrNull { it.parameterCount }!!
                        }
                )
            }
        }
    }
}
