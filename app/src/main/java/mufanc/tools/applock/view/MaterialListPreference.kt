package mufanc.tools.applock.view

import android.content.Context
import android.util.AttributeSet
import androidx.preference.ListPreference
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import mufanc.tools.applock.R

class MaterialListPreference(
    context: Context,
    attributeSet: AttributeSet
) : ListPreference(context, attributeSet) {

    override fun onAttached() {
        value ?: run {
            value = entryValues[0].toString()
        }
        summary = entry
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