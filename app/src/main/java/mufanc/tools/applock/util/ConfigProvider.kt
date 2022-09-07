package mufanc.tools.applock.util

import android.content.ContentProvider
import android.content.ContentValues
import android.content.Context
import android.database.Cursor
import android.net.Uri
import android.os.Binder
import android.os.Bundle
import android.os.Process
import mufanc.easyhook.api.Logger
import mufanc.tools.applock.BuildConfig
import mufanc.tools.applock.core.xposed.AppLockHelper
import kotlin.reflect.KProperty


class ConfigProvider : ContentProvider() {

    class Configs(private val bundle: Bundle) {
        val scope: Array<String> by bundle
        val killLevel: Int by bundle

        constructor(scope: Array<String>, killLevel: Int) : this(Bundle()) {
            bundle.putStringArray(::scope.name, scope)
            bundle.putInt(::killLevel.name, killLevel)
        }

        fun getBundle() = bundle

        private operator fun <T> Bundle.getValue(configs: Configs, property: KProperty<*>): T {
            @Suppress("Unchecked_Cast")
            return get(property.name) as T
        }
    }

    companion object {
        private const val BUNDLE_KEY = "DATA"
        private const val METHOD_GET_CONFIGS = "FETCH"

        fun fetch(context: Context): Configs {
            context.contentResolver.acquireUnstableContentProviderClient(
                Uri.parse("content://${BuildConfig.APPLICATION_ID}.provider")
            )?.call(
                METHOD_GET_CONFIGS, null, Bundle()
            )?.getBundle(BUNDLE_KEY)?.let {
                val configs = Configs(it)
                Logger.i("@Server: load scope from provider: ${configs.scope.contentToString()}")
                Logger.i("@Server: load kill level from provider: ${configs.killLevel} " +
                        "(${AppLockHelper.killLevelToString(configs.killLevel)})")
                return configs
            } ?: error("@Server: failed to load configs!")
        }
    }

    override fun call(method: String, arg: String?, extras: Bundle?): Bundle {
        val reply = Bundle()

        if (Binder.getCallingUid() != Process.SYSTEM_UID) {
            Logger.i("@Provider: authentication failed! (uid: ${Binder.getCallingUid()})")
            return Bundle()
        }

        if (method == METHOD_GET_CONFIGS) {
            reply.putBundle(
                BUNDLE_KEY, Configs(
                    ScopeManager.scope.toTypedArray(),
                    when (Settings.KILL_LEVEL.value) {
                        Settings.KillLevel.TRIM_MEMORY -> 101
                        Settings.KillLevel.NONE -> 100
                    }
                ).getBundle()
            )
        }

        return reply
    }

    override fun onCreate(): Boolean {
        val context = context!!

        ScopeManager.init(context)
        Settings.init(context.createDeviceProtectedStorageContext())

        return true
    }

    override fun query(p0: Uri, p1: Array<String?>?, p2: String?, p3: Array<String?>?, p4: String?): Cursor? = null

    override fun getType(p0: Uri): String? = null

    override fun insert(p0: Uri, p1: ContentValues?): Uri? = null

    override fun delete(p0: Uri, p1: String?, p2: Array<out String>?): Int = 0

    override fun update(p0: Uri, p1: ContentValues?, p2: String?, p3: Array<out String>?): Int = 0
}
