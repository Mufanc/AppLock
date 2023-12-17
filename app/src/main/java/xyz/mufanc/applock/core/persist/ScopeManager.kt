package xyz.mufanc.applock.core.persist

import android.os.FileObserver

object ScopeManager {

    private lateinit var observer: ScopeObserver

    fun init() {
        observer = ScopeObserver()
    }

    fun query(pkg: String): Boolean {
        if (::observer.isInitialized && observer.query(pkg)) {
            return true
        }

        return false
    }
}
