package mufanc.tools.applock.xposed

import android.app.ActivityThread
import android.content.Context
import android.os.Binder
import android.os.Parcel
import com.github.mufanc.easyhook.util.findMethod
import de.robv.android.xposed.XC_MethodHook
import de.robv.android.xposed.XposedBridge
import mufanc.tools.applock.BuildConfig
import mufanc.tools.applock.IAppLockManager
import mufanc.tools.applock.MyApplication

object AppLockHelper {

    val server = AppLockManagerService()
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

    private val context: Context by lazy {
        ActivityThread.currentActivityThread().systemContext
    }

    private object TransactionHooker : XC_MethodHook() {
        override fun beforeHookedMethod(param: MethodHookParam) {
            if (param.args[0] != TRANSACTION_CODE) return
            if (context.packageManager.getNameForUid(Binder.getCallingUid()) != BuildConfig.APPLICATION_ID) return
            (param.args[2] as Parcel).writeStrongBinder(server)
            param.result = true
        }
    }

    fun main() {
        XposedBridge.hookMethod(
            findMethod("miui.process.ProcessManagerNative") {
                name == "onTransact"
            },
            TransactionHooker
        )
    }
}