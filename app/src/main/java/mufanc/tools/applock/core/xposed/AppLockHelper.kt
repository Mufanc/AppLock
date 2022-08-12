package mufanc.tools.applock.core.xposed

import android.app.ActivityThread
import android.app.IActivityManager
import android.content.Context
import android.os.Binder
import android.os.Build
import android.os.Parcel
import android.os.ServiceManager
import android.util.ArrayMap
import android.util.SparseArray
import miui.process.ProcessConfig
import mufanc.easyhook.wrapper.EasyHook
import mufanc.easyhook.wrapper.Logger
import mufanc.easyhook.wrapper.hook
import mufanc.easyhook.wrapper.reflect.findField
import mufanc.easyhook.wrapper.reflect.findMethod
import mufanc.easyhook.wrapper.reflect.findMethods
import mufanc.easyhook.wrapper.reflect.getField
import mufanc.tools.applock.BuildConfig
import mufanc.tools.applock.IAppLockManager
import mufanc.tools.applock.MyApplication
import java.lang.reflect.Field
import java.lang.reflect.Method

object AppLockHelper {

    val client by lazy {
        MyApplication.processManager?.let {
            val data = Parcel.obtain()
            val reply = Parcel.obtain()
            try {
                if (it.transact(TRANSACTION_CODE, data, reply, 0)) {
                    return@lazy IAppLockManager.Stub.asInterface(reply.readStrongBinder())
                }
            } finally {
                data.recycle()
                reply.recycle()
            }
            return@lazy null
        }
    }

    // constants
    private val TRANSACTION_CODE = "Lock"
        .toByteArray()
        .mapIndexed { i, ch -> ch.toInt() shl (i * 8) }
        .sum()

    private val KILLERS = setOf("com.miui.home", "com.android.systemui")

    // system context
    private val context by lazy {
        ActivityThread.currentActivityThread().systemContext as Context
    }

    // AppLock service
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

    // entry
    fun main() = EasyHook.handle {
        // Hook 系统服务
        onLoadPackage("android") {

            // Hook `onTransact()` 以便与模块通信
            findClass("miui.process.ProcessManagerNative") hook {
                method({ name == "onTransact" }) {
                    before { param ->
                        if (param.args[0] != TRANSACTION_CODE) return@before
                        if (context.packageManager.getNameForUid(Binder.getCallingUid()) != BuildConfig.APPLICATION_ID) return@before
                        (param.args[2] as Parcel).writeStrongBinder(AppLockManagerService.getInstance(context))
                        param.result = true
                    }
                }
            }

            // Hook `ProcessManagerService` 实现应用免杀  Todo: 检查 MIUI 13
            findClass("com.android.server.am.ProcessManagerService") hook { clazz ->
                clazz.findMethods { name == "killOnce" && parameterTypes[0].simpleName == "ProcessRecord" }
                    .maxByOrNull { it.parameterCount }!! hook {
                    val processNameField = findClass("com.android.server.am.ProcessRecord").findField {
                        name == "processName"
                    }!!
                    before { param ->
                        val killer = processNameField.get(processMaps.get(Binder.getCallingPid())) ?: return@before
                        if (KILLERS.contains(killer)) {
                            val processRecord = param.args[0]
                            getPackageList(processRecord).forEach {
                                if (AppLockManagerService.getInstance(context).whitelist.contains(it)) {
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