package mufanc.tools.applock.xposed

import android.app.ActivityThread
import android.content.Context
import android.os.Binder
import android.os.Parcel
import android.util.ArrayMap
import com.github.mufanc.easyhook.util.*
import de.robv.android.xposed.XC_MethodHook
import de.robv.android.xposed.XposedBridge
import miui.process.ProcessConfig
import mufanc.tools.applock.BuildConfig
import mufanc.tools.applock.IAppLockManager
import mufanc.tools.applock.MyApplication
import java.io.File
import java.lang.reflect.Field
import java.lang.reflect.Method

object AppLockHelper {

    val server by lazy { AppLockManagerService() }
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

    private val packageManager by lazy {
        (ActivityThread.currentActivityThread().systemContext as Context).packageManager
    }

    private object TransactionHook : XC_MethodHook() {
        override fun beforeHookedMethod(param: MethodHookParam) {
            catch {
                if (param.args[0] != TRANSACTION_CODE) return
                if (packageManager.getNameForUid(Binder.getCallingUid()) != BuildConfig.APPLICATION_ID) return
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

        private fun getPackageList(record: Any): Set<String> {
            if (!this::field1.isInitialized) {
                field1 = findField(record::class.java) {
                    name == "pkgList" || name == "mPkgList"
                }!!
            }
            val obj = field1.get(record)
            @Suppress("Unchecked_Cast")
            return if (obj is ArrayMap<*, *>) {  // Android 9
                obj.keys as Set<String>
            } else {  // Android 10+
                if (!this::field2.isInitialized) {
                    field2 = findField(obj::class.java) { name == "mPkgList" }!!
                    getKeySet = findMethod(field2.get(obj)::class.java) { name == "keySet" }!!
                }
                getKeySet.invoke(field2.get(obj)) as Set<String>
            }
        }

        override fun beforeHookedMethod(param: MethodHookParam) {
            catch {
                val killer = File("/proc/${Binder.getCallingPid()}/cmdline").readText().trim { it == '\u0000' }
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