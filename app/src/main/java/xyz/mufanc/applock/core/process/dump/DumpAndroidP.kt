package xyz.mufanc.applock.core.process.dump

import android.util.ArrayMap
import com.android.server.am.ProcessRecord
import io.github.libxposed.api.XposedInterface
import xyz.mufanc.applock.core.process.bean.KillInfo
import xyz.mufanc.applock.core.process.bean.ProcessInfo
import xyz.mufanc.applock.core.util.Ref
import java.lang.reflect.Method

class DumpAndroidP : BaseDumper() {

    companion object {
        val hookTarget: Method
            get() = ProcessRecord::class.java.getDeclaredMethod(
                "kill",
                String::class.java, Boolean::class.java
            )
    }

    override fun dump(callback: XposedInterface.BeforeHookCallback): KillInfo {
        val obj = Ref(callback.thisObject!!)

        return KillInfo(
            reason = callback.args[0] as String,
            description = "",
            reasonCode = -1,
            subReason = -1,
            processInfo = ProcessInfo(
                killedByAm = obj["killedByAm"].obtain(),
                pid = obj["pid"].obtain(),
                uid = obj["uid"].obtain(),
                gids = (obj["gids"].obtain<IntArray>()).toList(),
                isolated = obj["isolated"].obtain(),
                name = obj["processName"].obtain(),
                packageList = (obj["pkgList"].obtain<ArrayMap<String, *>>()).keys.toList(),
                processType = obj["curProcState"].obtain()
            )
        )
    }
}
