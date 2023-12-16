package xyz.mufanc.applock.core.process.dump

import android.util.ArrayMap
import com.android.server.am.ProcessRecord
import io.github.libxposed.api.XposedInterface
import xyz.mufanc.applock.core.process.bean.KillInfo
import xyz.mufanc.applock.core.process.bean.ProcessInfo
import xyz.mufanc.applock.core.util.Ref
import java.lang.reflect.Method

class DumpAndroidT : BaseDumper() {

    companion object {
        val hookTarget: Method
            get() = ProcessRecord::class.java.getDeclaredMethod(
                "killLocked",
                String::class.java, String::class.java, Int::class.java, Int::class.java, Boolean::class.java
            )
    }

    override fun dump(callback: XposedInterface.BeforeHookCallback): KillInfo {
        val obj = Ref(callback.thisObject!!)

        return KillInfo(
            reason = callback.args[0] as String,
            description = callback.args[1] as String,
            reasonCode = callback.args[2] as Int,
            subReason = callback.args[3] as Int,
            processInfo = ProcessInfo(
                killedByAm = obj["mKilledByAm"].obtain(),
                pid = obj["mPid"].obtain(),
                uid = obj["uid"].obtain(),
                gids = (obj["mGids"].obtain<IntArray>()).toList(),
                isolated = obj["isolated"].obtain(),
                name = obj["processName"].obtain(),
                packageList = (obj["mPkgList"]["mPkgList"].obtain<ArrayMap<String, *>>()).keys.toList(),
                processType = obj["mState"]["mCurProcState"].obtain()
            )
        )
    }
}
