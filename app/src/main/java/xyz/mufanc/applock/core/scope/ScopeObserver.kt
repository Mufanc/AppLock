package xyz.mufanc.applock.core.scope

import android.os.FileObserver
import xyz.mufanc.applock.core.util.Log
import java.io.File

class ScopeObserver(
    private val callback: (List<String>) -> Unit
) : FileObserver(TARGET_DIR, EVENTS) {

    companion object {
        private const val TAG = "ScopeObserver"

        private const val TARGET_DIR = "/data/system"
        private const val SCOPE_FILE = "applock-debug.list"
        private const val EVENTS = CREATE or CLOSE_WRITE or MOVED_TO

        private val PACKAGE_NAME_PATTERN = "^[A-Za-z\\d_.]+\$".toRegex()
    }

    private val scopeFile = File(TARGET_DIR, SCOPE_FILE)

    private fun refresh() {
        if (!scopeFile.exists()) return

        try {
            callback(
                scopeFile.readText().trim().split("\\s+".toRegex()).filter { name ->
                    name.matches(PACKAGE_NAME_PATTERN)
                }
            )
        } catch (err: Throwable) {
            Log.e(TAG, "failed to read scope: $err")
        }
    }

    override fun onEvent(event: Int, path: String?) {
        val file = File(TARGET_DIR, path ?: return)

        if (file.name != SCOPE_FILE) return

        refresh()
    }

    init {
        startWatching()
        refresh()
    }
}
