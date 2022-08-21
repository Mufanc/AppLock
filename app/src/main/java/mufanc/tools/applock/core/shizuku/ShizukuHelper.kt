package mufanc.tools.applock.core.shizuku

import android.content.pm.PackageManager
import android.widget.Toast
import miui.process.ProcessCloudData
import miui.process.ProcessManagerNative
import mufanc.easyhook.api.catch
import mufanc.tools.applock.MyApplication
import mufanc.tools.applock.R
import rikka.shizuku.Shizuku
import rikka.shizuku.ShizukuBinderWrapper
import kotlin.random.Random

object ShizukuHelper {

    fun getSelinuxContext(): String {
        return try {
            Shizuku.getSELinuxContext()!!
        } catch (err: Throwable) {
            "null"
        }
    }

    fun checkPermission(): Boolean {
        return try {
            Shizuku.checkSelfPermission() == PackageManager.PERMISSION_GRANTED
        } catch (err: Throwable) {
            false
        }
    }

    fun requestPermission(callback: (result: Int) -> Unit) {
        catch {
            if (checkPermission()) return
            val requestCode = Random.nextInt()
            val listener = object : Shizuku.OnRequestPermissionResultListener {
                override fun onRequestPermissionResult(code: Int, result: Int) {
                    Shizuku.removeRequestPermissionResultListener(this)
                    if (code == requestCode) {
                        callback(result)
                    }
                }
            }
            Shizuku.addRequestPermissionResultListener(listener)
            if (!Shizuku.shouldShowRequestPermissionRationale()) {
                Shizuku.requestPermission(requestCode)
            }
        }
    }

    fun writePackageList(packageList: List<String>) {
        fun internal(result: Int) {
            if (result == PackageManager.PERMISSION_GRANTED) {
                MyApplication.processManager?.let { binder ->
                    ProcessManagerNative.asInterface(ShizukuBinderWrapper(binder)).apply {
                        updateCloudData(ProcessCloudData().also { it.setCloudWhiteList(packageList) })
                    }
                    Toast.makeText(MyApplication.context, R.string.scope_saved, Toast.LENGTH_SHORT).show()
                }
            }
        }

        if (!checkPermission()) {
            requestPermission { internal(it) }
        } else {
            internal(PackageManager.PERMISSION_GRANTED)
        }
    }
}