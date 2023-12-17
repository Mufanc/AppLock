package xyz.mufanc.applock.core.persist

import android.os.FileObserver
import android.util.ArraySet
import xyz.mufanc.applock.core.util.Log
import java.io.File
import java.util.Collections

class ScopeObserver : FileObserver(TARGET_DIR, EVENTS) {

    companion object {
        private const val TAG = "ScopeObserver"

        private const val TARGET_DIR = "/data/system"
        private const val SCOPE_FILE = "applock-debug.list"
        private const val EVENTS = CREATE or CLOSE_WRITE or MOVED_TO
    }

    private val scope = Collections.synchronizedSet(ArraySet<String>())

    init {
        startWatching()
    }

    override fun onEvent(event: Int, path: String?) {
        val file = File(TARGET_DIR, path ?: return)

        if (file.name != SCOPE_FILE) {
            return
        }

        try {
            scope.clear()
            scope.addAll(file.readText().trim().split("\\s+"))
            Log.i(TAG, "scope changed: $scope")
        } catch (err: Throwable) {
            Log.e(TAG, "failed to read scope: $err")
        }
    }

    fun query(pkg: String): Boolean {
        return scope.contains(pkg)
    }
}
