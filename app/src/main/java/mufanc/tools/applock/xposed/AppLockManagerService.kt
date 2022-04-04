package mufanc.tools.applock.xposed

import android.os.Process
import mufanc.tools.applock.IAppLockManager

class AppLockManagerService : IAppLockManager.Stub() {

    override fun handshake(): IntArray {
        return intArrayOf(Process.myPid(), Process.myUid())
    }

    override fun updatePackageList(packageList: Array<out String>?) {
        TODO("Not yet implemented")
    }

    override fun readPackageList(): Array<String> {
        TODO("Not yet implemented")
    }
}