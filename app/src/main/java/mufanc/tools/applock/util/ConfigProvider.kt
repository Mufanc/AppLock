package mufanc.tools.applock.util

import android.content.ContentProvider
import android.content.ContentValues
import android.content.Context
import android.database.Cursor
import android.net.Uri
import android.os.*
import androidx.preference.PreferenceManager
import mufanc.easyhook.api.Logger
import mufanc.tools.applock.BuildConfig


class ConfigProvider : ContentProvider() {

    class Configs(
        val scope: Array<String>,
        val killLevel: Int
    ) : Parcelable {
        constructor(parcel: Parcel) : this(
            parcel.createStringArray()!!,
            parcel.readInt()
        )

        override fun writeToParcel(parcel: Parcel, flags: Int) {
            parcel.writeStringArray(scope)
            parcel.writeInt(killLevel)
        }

        override fun describeContents(): Int = 0

        companion object CREATOR : Parcelable.Creator<Configs> {
            override fun createFromParcel(parcel: Parcel): Configs {
                return Configs(parcel)
            }

            override fun newArray(size: Int): Array<Configs?> {
                return arrayOfNulls(size)
            }
        }
    }

    private var killLevel: Int = 101

    companion object {
        private const val BUNDLE_KEY = "DATA"
        private const val METHOD_GET_CONFIGS = "FETCH"

        fun fetch(context: Context): Configs {
            context.contentResolver.acquireUnstableContentProviderClient(
                Uri.parse("content://${BuildConfig.APPLICATION_ID}.provider")
            )?.call(
                METHOD_GET_CONFIGS, null, Bundle()
            )?.also {
                Logger.i(it.keySet().toList())
            }?.getParcelable<Configs>(BUNDLE_KEY)?.let { configs ->
                Logger.i("@Server: load scope from provider: ${configs.scope}")
                Logger.i("@Server: load kill level from provider: ${configs.killLevel}")
                return configs
            } ?: let {
                throw Exception("@Server: failed to load configs!")
            }
        }
    }

    override fun call(method: String, arg: String?, extras: Bundle?): Bundle {
        val reply = Bundle()

        if (Binder.getCallingUid() != Process.SYSTEM_UID) {
            Logger.i("@Provider: authentication failed! (uid: ${Binder.getCallingUid()})")
            return Bundle()
        }

        Logger.i(method)
        if (method == METHOD_GET_CONFIGS) {
//            reply.putParcelable(
//                BUNDLE_KEY,
//                Configs(ScopeManager.scope.toTypedArray(), killLevel)  // Todo: 动态加载配置
//            )
            reply.putStringArray("scope", ScopeManager.scope.toTypedArray())
            reply.putInt("killLevel", killLevel)
            Logger.i("Fuck you!")
        }

        return reply
    }

    override fun onCreate(): Boolean {
        ScopeManager.init(context!!)
        killLevel = when (
            PreferenceManager.getDefaultSharedPreferences(
                context!!.createDeviceProtectedStorageContext()
            ).getString("KILL_LEVEL", "TRIM_MEMORY").also {
                Logger.e(it)
            }
        ) {
            "NONE" -> 100
            "TRIM_MEMORY" -> 101
            else -> throw RuntimeException()
        }
        return true
    }

    override fun query(p0: Uri, p1: Array<String?>?, p2: String?, p3: Array<String?>?, p4: String?): Cursor? = null

    override fun getType(p0: Uri): String? = null

    override fun insert(p0: Uri, p1: ContentValues?): Uri? = null

    override fun delete(p0: Uri, p1: String?, p2: Array<out String>?): Int = 0

    override fun update(p0: Uri, p1: ContentValues?, p2: String?, p3: Array<out String>?): Int = 0
}