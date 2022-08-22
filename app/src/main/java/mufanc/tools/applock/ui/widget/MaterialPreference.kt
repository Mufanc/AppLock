package mufanc.tools.applock.ui.widget

import android.content.Context
import androidx.preference.Preference
import mufanc.tools.applock.R

class MaterialPreference(context: Context) : Preference(context) {
    init {
        layoutResource = R.layout.item_preference
    }
}
