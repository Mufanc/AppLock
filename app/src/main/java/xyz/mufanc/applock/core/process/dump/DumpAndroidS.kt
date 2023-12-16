package xyz.mufanc.applock.core.process.dump

import android.util.ArrayMap
import com.android.server.am.ProcessRecord
import io.github.libxposed.api.XposedInterface
import xyz.mufanc.applock.core.process.bean.KillInfo
import xyz.mufanc.applock.core.process.bean.ProcessInfo
import xyz.mufanc.applock.core.util.Ref
import java.lang.reflect.Method

class DumpAndroidS : BaseDumper() {

    companion object {
        val hookTarget: Method
            get() = ProcessRecord::class.java.getDeclaredMethod(
                "killLocked",
                String::class.java, Int::class.java, Int::class.java, Boolean::class.java
            )
    }

    override fun dump(callback: XposedInterface.BeforeHookCallback): KillInfo {
        val ref = Ref(callback.thisObject!!)
        
        return KillInfo(
            reason = callback.args[0] as String,
            description = "",
            reasonCode = callback.args[1] as Int,
            subReason = callback.args[2] as Int,
            processInfo = ProcessInfo(
                killedByAm = ref["mKilledByAm"].obtain(),
                pid = ref["mPid"].obtain(),
                uid = ref["uid"].obtain(),
                gids = (ref["mGids"].obtain<IntArray>()).toList(),
                isolated = ref["isolated"].obtain(),
                name = ref["processName"].obtain(),
                packageList = (ref["mPkgList"]["mPkgList"].obtain<ArrayMap<String, *>>()).keys.toList(),
                processType = ref["mState"]["mCurProcState"].obtain()
            )
        )
    }
}
