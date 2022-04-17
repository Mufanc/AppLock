package mufanc.tools.applock.xposed

import android.content.Context
import android.net.Uri
import android.os.Binder
import android.os.IPowerManager
import android.os.Process
import android.os.ServiceManager
import com.github.mufanc.easyhook.util.Log
import mufanc.tools.applock.BuildConfig
import mufanc.tools.applock.IAppLockManager
import java.io.File

class AppLockManagerService(
    private val context: Context
) : IAppLockManager.Stub() {

    val whitelist: MutableSet<String> by lazy {
        var result = mutableSetOf<String>()
        Binder.restoreCallingIdentity(Binder.clearCallingIdentity().also {
            context.contentResolver.call(
                Uri.parse("content://${BuildConfig.APPLICATION_ID}.provider"),
                "scope", null, null
            )?.getStringArray("scope")?.also {
                result = it.toMutableSet()
                Log.i("Load scope from provider: ${it.contentToString()}")
            } ?: let {
                Log.w("Failed to resolve whitelist!")
            }
        })
        result
    }

    override fun handshake(): IntArray {
        Log.i("handshake from client!")
        return intArrayOf(BuildConfig.VERSION_CODE, Process.myPid(), Process.myUid())
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

    override fun writePackageList(packageList: Array<out String>) {
        whitelist.clear()
        whitelist.addAll(packageList)
        Log.i("scope updated: ${packageList.contentToString()}")
    }

    override fun importScopeFromOldVersion(): Array<String> {
        val dir = "/data/system/app_lock"
        File("$dir/whitelist.txt").let { file ->
            if (file.exists()) {
                val scope = file.readLines()
                    .map { it.trim() }.filterNot { it.isEmpty() }
                    .toTypedArray()
                file.delete()
                File(dir).delete()
                return scope
            }
        }
        return arrayOf()
    }
}