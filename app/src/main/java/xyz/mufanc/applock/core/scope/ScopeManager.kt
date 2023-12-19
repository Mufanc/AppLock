package xyz.mufanc.applock.core.scope

import android.util.ArraySet
import xyz.mufanc.applock.core.util.Log
import java.util.Collections

object ScopeManager {

    private const val TAG = "ScopeManager"

    private lateinit var observer: ScopeObserver
    private val scope = Collections.synchronizedSet(ArraySet<String>())

    fun init() {
        observer = ScopeObserver { pkgs ->
            scope.clear()
            scope.addAll(pkgs)
            Log.i(TAG, "update scope: $scope")
        }
    }

    fun query(pkg: String): Boolean {
        return scope.contains(pkg)
    }
}
