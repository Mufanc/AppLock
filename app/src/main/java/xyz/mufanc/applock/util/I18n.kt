package xyz.mufanc.applock.util

import android.annotation.SuppressLint
import android.content.Context
import xyz.mufanc.applock.generated.locale.AppLocale

object I18n {

    @SuppressLint("StaticFieldLeak")
    lateinit var strings: AppLocale

    fun init(context: Context) {
        strings = AppLocale.attach(context)
    }
}
