package mufanc.tools.applock.xposed

import android.app.ActivityThread
import android.app.IActivityManager
import android.content.Context
import android.os.Binder
import android.os.Build
import android.os.Parcel
import android.os.ServiceManager
import android.util.ArrayMap
import android.util.SparseArray
import com.github.mufanc.easyhook.util.*
import de.robv.android.xposed.XC_MethodHook
import de.robv.android.xposed.XposedBridge
import miui.process.ProcessConfig
import mufanc.tools.applock.BuildConfig
import mufanc.tools.applock.IAppLockManager
import mufanc.tools.applock.MyApplication
import java.lang.reflect.Field
import java.lang.reflect.Method

object AppLockHelper {

    val server by lazy { AppLockManagerService(systemContext) }
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

    private val TRANSACTION_CODE = "Lock"
        .toByteArray()
        .mapIndexed { i, ch -> ch.toInt() shl (i * 8) }
        .sum()

    private val KILLER_SET = setOf("com.miui.home", "com.android.systemui")

    private val systemContext by lazy {
        ActivityThread.currentActivityThread().systemContext as Context
    }

    private val processMaps by lazy {
        val pidMap = IActivityManager.Stub.asInterface(
            ServiceManager.getService(Context.ACTIVITY_SERVICE)
        ).getField("mPidsSelfLocked")
        val arr = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
            pidMap?.getField("mPidMap")
        } else {
            pidMap
        }
        arr as SparseArray<*>?
    }

    private object TransactionHook : XC_MethodHook() {
        override fun beforeHookedMethod(param: MethodHookParam) {
            catch {
                if (param.args[0] != TRANSACTION_CODE) return
                if (systemContext.packageManager.getNameForUid(Binder.getCallingUid()) != BuildConfig.APPLICATION_ID) return
                (param.args[2] as Parcel).writeStrongBinder(server)
                param.result = true
            }
        }
    }

    private object KillProcessHook : XC_MethodHook() {

        // Compatible with different Android version
        private lateinit var field1: Field
        private lateinit var field2: Field
        private lateinit var getKeySet: Method

        private val processName =
            findField("com.android.server.am.ProcessRecord") {
                name == "processName"
            }

        private fun getPackageList(record: Any): Set<String> {
            if (!::field1.isInitialized) {
                field1 = findField(record::class.java) {
                    name == "pkgList" || name == "mPkgList"
                }!!
                field1.isAccessible = true
            }
            val obj = field1.get(record)
            @Suppress("Unchecked_Cast")
            return if (obj is ArrayMap<*, *>) {  // Android 9
                obj.keys as Set<String>
            } else {  // Android 10+
                if (!::field2.isInitialized) {
                    field2 = findField(obj::class.java) { name == "mPkgList" }!!
                    field2.isAccessible = true
                    getKeySet = findMethod(field2.get(obj)::class.java) { name == "keySet" }!!
                }
                getKeySet.invoke(field2.get(obj)) as Set<String>
            }
        }

        override fun beforeHookedMethod(param: MethodHookParam) {
            catch {
                val killer = processName?.get(processMaps?.get(Binder.getCallingPid()))
                killer ?: return
                if (KILLER_SET.contains(killer)) {
                    val processRecord = param.args[0]
                    getPackageList(processRecord).forEach {
                        if (server.whitelist.contains(it)) {
                            param.args[2] = ProcessConfig.KILL_LEVEL_TRIM_MEMORY
                            Log.i("@AppLock: ${processRecord.getField("processName")}")
                            return
                        }
                    }
                }
            }
        }
    }

    fun main() {
        XposedBridge.hookMethod(
            findMethod("miui.process.ProcessManagerNative") {
                name == "onTransact"
            },
            TransactionHook
        )

        XposedBridge.hookMethod(
            findMethods("com.android.server.am.ProcessManagerService") {
                name == "killOnce" && parameterTypes[0].simpleName == "ProcessRecord"
            }.maxByOrNull {
                it.parameterCount
            },
            KillProcessHook
        )
    }
}