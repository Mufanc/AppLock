package mufanc.tools.applock.util

import android.content.ContentProvider
import android.content.ContentValues
import android.database.Cursor
import android.net.Uri
import android.os.Bundle
import android.util.Log
import mufanc.tools.applock.MyApplication


class ScopeProvider : ContentProvider() {

    override fun call(method: String, arg: String?, extras: Bundle?): Bundle {
        MyApplication.context = context!!
        val reply = Bundle()
        when (method) {
            "scope" -> {
                reply.putStringArray("scope", ScopeDatabase.readScope().toTypedArray())
                Log.i(MyApplication.TAG, "send scope!")
            }
        }
        return reply
    }

    override fun onCreate(): Boolean {
        return true
    }

    // No other provider methods
    override fun query(
        uri: Uri,
        projection: Array<String?>?,
        selection: String?,
        selectionArgs: Array<String?>?,
        sortOrder: String?,
    ): Cursor? {
        return null
    }

    override fun getType(uri: Uri): String? {
        return null
    }

    override fun insert(uri: Uri, p1: ContentValues?): Uri? {
        return null
    }

    override fun delete(uri: Uri, selection: String?, selectionArgs: Array<out String>?): Int {
        return 0
    }

    override fun update(uri: Uri, values: ContentValues?, selection: String?, selectionArgs: Array<out String>?): Int {
        return 0
    }
}