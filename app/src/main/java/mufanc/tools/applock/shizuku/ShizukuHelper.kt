package mufanc.tools.applock.shizuku

import android.content.pm.PackageManager
import miui.process.ProcessCloudData
import miui.process.ProcessManagerNative
import mufanc.tools.applock.MyApplication
import rikka.shizuku.Shizuku
import rikka.shizuku.ShizukuBinderWrapper

object ShizukuHelper {

    fun checkPermission(): Boolean {
        return Shizuku.checkSelfPermission() == PackageManager.PERMISSION_GRANTED
    }

    fun requestPermission() {
        if (checkPermission()) return
        if (!Shizuku.shouldShowRequestPermissionRationale()) {
            Shizuku.requestPermission(0)
        }
    }

    fun writePackageList(packageList: List<String>) {
        fun internal() {
            MyApplication.processManager?.let { binder ->
                ProcessManagerNative.asInterface(ShizukuBinderWrapper(binder)).apply {
                    updateCloudData(ProcessCloudData().also { it.setCloudWhiteList(packageList) })
                }
            }
        }

        if (!checkPermission()) {
            val listener = object : Shizuku.OnRequestPermissionResultListener {
                override fun onRequestPermissionResult(code: Int, result: Int) {
                    Shizuku.removeRequestPermissionResultListener(this)
                    if (result == PackageManager.PERMISSION_GRANTED) {
                        internal()
                    }
                }
            }
            Shizuku.addRequestPermissionResultListener(listener)
            requestPermission()
        } else {
            internal()
        }
    }
}