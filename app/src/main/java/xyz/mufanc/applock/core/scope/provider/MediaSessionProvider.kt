package xyz.mufanc.applock.core.scope.provider

import android.media.session.MediaSessionManager
import android.media.session.MediaSessionManager.OnActiveSessionsChangedListener
import android.app.ActivityThread
import io.github.libxposed.api.XposedInterface
import xyz.mufanc.applock.core.util.ContextHelper
import xyz.mufanc.applock.core.util.Log

@Suppress("Unused")
data object MediaSessionProvider : ScopeProvider() {

    private const val TAG = "MediaSessionProvider"

    private val manager: MediaSessionManager by lazy {
        ContextHelper.systemContext()
            .getSystemService(MediaSessionManager::class.java)
    }

    private val listener = OnActiveSessionsChangedListener { controllers ->
        if (controllers != null) {
            emit(controllers.map { it.packageName }.toSet())
        }
    }

    override fun isAvailable(): Boolean = true

    override fun init(ixp: XposedInterface) {
        manager.addOnActiveSessionsChangedListener(listener, null)
        Log.d(TAG, "initialized")
    }
}