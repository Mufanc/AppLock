package mufanc.tools.applock.ui.widget

import android.content.Context
import androidx.preference.ListPreference
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import mufanc.tools.applock.R

class MaterialListPreference(context: Context) : ListPreference(context) {

    init {
        layoutResource = R.layout.item_preference
    }

    override fun onAttached() {
        summary = entry ?: entries[0]
    }

    override fun getValue(): String {
        super.getValue() ?: let {
            value = entryValues[0].toString()
        }
        return super.getValue()
    }

    override fun onClick() {
        MaterialAlertDialogBuilder(context)
            .setTitle(title)
            .setSingleChoiceItems(entries, findIndexOfValue(value)) { dialog, index ->
                if(callChangeListener(entryValues[index].toString())){
                    setValueIndex(index)
                }
                summary = entry
                dialog.dismiss()
            }
            .setNegativeButton(R.string.cancel) { dialog, _ -> dialog.dismiss()}
            .show()
    }
}
