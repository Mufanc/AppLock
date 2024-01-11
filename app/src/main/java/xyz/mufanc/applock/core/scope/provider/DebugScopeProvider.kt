package xyz.mufanc.applock.core.scope.provider

import android.os.FileObserver
import io.github.libxposed.api.XposedInterface
import xyz.mufanc.applock.core.util.Log
import java.io.File

@Suppress("Unused")
data object DebugScopeProvider : ScopeProvider() {

    private const val TAG = "ScopeObserver"

    private const val TARGET_DIR = "/data/system"
    private const val SCOPE_FILE = "applock-debug.list"
    private const val EVENTS = FileObserver.CREATE or FileObserver.CLOSE_WRITE or FileObserver.MOVED_TO

    private val PACKAGE_NAME_PATTERN = "^[A-Za-z\\d_.]+\$".toRegex()

    private val scopeFile = File(TARGET_DIR, SCOPE_FILE)

    private lateinit var observer: FileObserver

    private fun refresh() {
        if (!scopeFile.exists()) return

        try {
            val scope = scopeFile.readText()
                .trim()
                .split("\\s+".toRegex())
                .filter { name ->
                    name.matches(PACKAGE_NAME_PATTERN)
                }
                .toSet()

            emit(scope)
        } catch (err: Throwable) {
            Log.e(TAG, "failed to read scope: $err")
        }
    }

    override fun isAvailable(): Boolean = true

    override fun init(ixp: XposedInterface) {
        observer = object : FileObserver(TARGET_DIR, EVENTS) {
            override fun onEvent(event: Int, path: String?) {
                val file = File(TARGET_DIR, path ?: return)

                if (file.name != SCOPE_FILE) return

                refresh()
            }

        }

        observer.startWatching()
        refresh()
    }
}
