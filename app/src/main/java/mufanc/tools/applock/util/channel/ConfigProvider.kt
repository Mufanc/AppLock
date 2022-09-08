package mufanc.tools.applock.util.channel

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
import mufanc.tools.applock.util.ScopeManager
import mufanc.tools.applock.util.Settings


class ConfigProvider : ContentProvider() {

    companion object {
        private const val METHOD_GET_CONFIGS = "FETCH"

        fun fetch(context: Context): Configs {
            context.contentResolver.acquireUnstableContentProviderClient(
                Uri.parse("content://${BuildConfig.APPLICATION_ID}.provider")
            )?.call(
                METHOD_GET_CONFIGS, null, Bundle()
            )?.let {
                val configs = Configs(it)
                Logger.i("@Server: load scope from provider: ${configs.scope.contentToString()}")
                Logger.i("@Server: load kill-level from provider: ${configs.killLevel} " +
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
            reply.putAll(Configs.collect().asBundle())
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
