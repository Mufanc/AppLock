package xyz.mufanc.applock.core.process

import com.android.server.am.ProcessRecord
import io.github.libxposed.api.XposedInterface
import io.github.libxposed.api.annotations.BeforeInvocation
import io.github.libxposed.api.annotations.XposedHooker
import xyz.mufanc.applock.core.process.model.KillInfo
import xyz.mufanc.applock.core.process.model.KillInfoFactory
import xyz.mufanc.applock.core.util.ApiAdapter
import xyz.mufanc.applock.core.util.Log
import java.lang.reflect.Method

@XposedHooker
class KillProcessMonitor : XposedInterface.Hooker {

    companion object : ApiAdapter<Unit, Method>() {

        private const val TAG = "KillProcessMonitor"

        fun init(ixp: XposedInterface) {
            ixp.hook(adapt(Unit), KillProcessMonitor::class.java)
        }

        private fun formatLog(info: KillInfo, backtrace: Sequence<StackTraceElement>): String {
            val header = "-".repeat(20) + " KillProcess " + "-".repeat(20)

            return StringBuilder()
                .appendLine()
                .appendLine(header)
                .appendLine(info)
                .appendLine("Backtrace:")
                .appendLine(backtrace.joinToString("\n") { "  -> ${it.className}.${it.methodName}()" })
                .appendLine("-".repeat(header.length))
                .toString()
        }

        @BeforeInvocation
        @JvmStatic
        fun before(callback: XposedInterface.BeforeHookCallback): KillProcessMonitor {
            val info = KillInfoFactory.create(callback)
            val backtrace = Thread.currentThread().stackTrace.asSequence().drop(2)

            Log.d(TAG, formatLog(info, backtrace))

            return KillProcessMonitor()
        }

        override fun doAndroidP(from: Unit): Method {
            return ProcessRecord::class.java.getDeclaredMethod(
                "kill",
                String::class.java, Boolean::class.java
            )
        }

        override fun doAndroidQ(from: Unit): Method {
            TODO("Not yet implemented")
        }

        override fun doAndroidR(from: Unit): Method {
            TODO("Not yet implemented")
        }

        override fun doAndroidS(from: Unit): Method {
            return ProcessRecord::class.java.getDeclaredMethod(
                "killLocked",
                String::class.java, Int::class.java, Int::class.java, Boolean::class.java
            )
        }

        override fun doAndroidT(from: Unit): Method {
            return ProcessRecord::class.java.getDeclaredMethod(
                "killLocked",
                String::class.java, String::class.java, Int::class.java, Int::class.java, Boolean::class.java
            )
        }

        override fun doAndroidU(from: Unit): Method {
            TODO("Not yet implemented")
        }
    }
}
