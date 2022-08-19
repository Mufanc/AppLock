package mufanc.tools.applock.core.xposed

import android.app.ActivityThread
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
import mufanc.easyhook.api.hook
import mufanc.easyhook.api.reflect.findField
import mufanc.easyhook.api.reflect.findMethod
import mufanc.easyhook.api.reflect.findMethods
import mufanc.easyhook.api.reflect.getField
import java.lang.reflect.Field
import java.lang.reflect.Method

object AppLockHelper {

    private val KILLERS = setOf("com.miui.home", "com.android.systemui")

    private val context by lazy {
        ActivityThread.currentActivityThread().systemContext as Context
    }

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
            // Hook `ProcessManagerService` 实现应用免杀  Todo: 检查 MIUI 13
            onLoadPackage("android") {
                findClass("com.android.server.am.ProcessManagerService").hook { clazz ->
                    clazz.findMethods { name == "killOnce" && parameterTypes[0].simpleName == "ProcessRecord" }
                        .maxByOrNull { it.parameterCount }!!.hook {
                        val processNameField = findClass("com.android.server.am.ProcessRecord").findField {
                            name == "processName"
                        }!!
                        before { param ->
                            val killer = processNameField.get(processMaps.get(Binder.getCallingPid())) ?: return@before
                            if (KILLERS.contains(killer)) {
                                val processRecord = param.args[0]
                                getPackageList(processRecord).forEach {
                                    if (AppLockManager.query(it)) {
                                        param.args[2] = ProcessConfig.KILL_LEVEL_TRIM_MEMORY
                                        Logger.i("@AppLock: ${processRecord.getField("processName")}")
                                        return@forEach
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}