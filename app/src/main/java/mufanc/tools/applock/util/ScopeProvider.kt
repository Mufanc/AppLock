package mufanc.tools.applock.util

import android.content.ContentProvider
import android.content.ContentValues
import android.database.Cursor
import android.net.Uri
import android.os.Bundle
import mufanc.easyhook.api.Logger


class ScopeProvider : ContentProvider() {

    override fun call(method: String, arg: String?, extras: Bundle?): Bundle {
        val reply = Bundle()
        when (method) {
            "scope" -> {
                reply.putStringArray("scope", ScopeManager.scope.toTypedArray())
                Logger.i("@Provider: send scope to server!")
            }
        }
        return reply
    }

    override fun onCreate(): Boolean {
        ScopeManager.init(context!!)
        return true
    }

    override fun query(p0: Uri, p1: Array<String?>?, p2: String?, p3: Array<String?>?, p4: String?): Cursor? = null

    override fun getType(p0: Uri): String? = null

    override fun insert(p0: Uri, p1: ContentValues?): Uri? = null

    override fun delete(p0: Uri, p1: String?, p2: Array<out String>?): Int = 0

    override fun update(p0: Uri, p1: ContentValues?, p2: String?, p3: Array<out String>?): Int = 0
}