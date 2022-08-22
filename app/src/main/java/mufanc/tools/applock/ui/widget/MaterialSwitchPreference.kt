package mufanc.tools.applock.ui.widget

import android.content.Context
import android.view.View
import androidx.preference.PreferenceViewHolder
import androidx.preference.SwitchPreferenceCompat
import mufanc.tools.applock.R

class MaterialSwitchPreference(context: Context) : SwitchPreferenceCompat(context) {

    init {
        layoutResource = R.layout.item_switch_preference
    }

    override fun onBindViewHolder(holder: PreferenceViewHolder) {
        super.onBindViewHolder(holder)
        javaClass.superclass.getDeclaredMethod("syncSwitchView", View::class.java)
            .apply { isAccessible = true }
            .invoke(this, holder.findViewById(R.id.switch_widget))
    }
}