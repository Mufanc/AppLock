package mufanc.tools.applock.core.xposed

import android.app.IActivityManager
import android.content.Context
import android.os.Binder
import android.os.Build
import android.os.ServiceManager
import android.util.ArrayMap
import android.util.SparseArray
import miui.process.ProcessConfig
import mufanc.easyhook.api.EasyHook
import mufanc.easyhook.api.Logger
import mufanc.easyhook.api.hook.hook
import mufanc.easyhook.api.reflect.findField
import mufanc.easyhook.api.reflect.findMethod
import mufanc.easyhook.api.reflect.findMethods
import mufanc.easyhook.api.reflect.getField
import java.lang.reflect.Field
import java.lang.reflect.Method

object AppLockHelper {

    private val KILLERS = setOf("com.miui.home", "com.android.systemui")

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

    fun init() {
        AppLockManager.init()
        EasyHook.handle {
            // Hook `ProcessManagerService` 实现应用免杀
            onLoadPackage("android") {
                val filter = fun (method: Method): Boolean {
                    return method.name == "killOnce" && method.parameterTypes[0].simpleName == "ProcessRecord"
                }

                val killOnce = findClass("com.android.server.am.ProcessManagerService")
                    .findMethods(filter)
                    .maxByOrNull {
                        it.parameterCount
                    } ?: run {
                        findClass("com.android.server.am.ProcessCleanerBase")
                            .findMethods(filter)
                            .minByOrNull { it.parameterCount }!!
                    }

                killOnce.hook { method ->
                    Logger.i("@Hooker: hook killOnce: $method")
                    val processNameField = findClass("com.android.server.am.ProcessRecord").findField {
                        name == "processName"
                    }!!
                    before { param ->
                        val killer = processNameField.get(processMaps.get(Binder.getCallingPid())) ?: return@before
                        if (KILLERS.contains(killer)) {
                            val processRecord = param.args[0]
                            val processName = processRecord.getField("processName")

                            getPackageList(processRecord).forEach {
                                if (AppLockManager.query(it)) {
                                    param.args[2] = ProcessConfig.KILL_LEVEL_TRIM_MEMORY
                                    Logger.i("@AppLock: $processName")
                                    return@before
                                }
                            }

                            Logger.i("@AppLock: killing $processName, by $killer")
                        }
                    }
                }
            }
        }
    }
}
