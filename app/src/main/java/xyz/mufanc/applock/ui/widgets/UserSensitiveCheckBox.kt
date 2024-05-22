package xyz.mufanc.applock.ui.widgets

import android.content.Context
import android.util.AttributeSet
import android.widget.CompoundButton
import com.google.android.material.checkbox.MaterialCheckBox

class UserSensitiveCheckBox(
    context: Context,
    attrs: AttributeSet? = null
) : MaterialCheckBox(context, attrs) {

    var userTriggered = false

    override fun performClick(): Boolean {
        userTriggered = true
        val handled = super.performClick()
        userTriggered = false
        return handled
    }

    override fun setOnCheckedChangeListener(listener: OnCheckedChangeListener?) {
        if (listener == null) {
            super.setOnCheckedChangeListener(null)
            return
        }

        super.setOnCheckedChangeListener(
            object : OnCheckedChangeListener {
                override fun onCheckedChanged(buttonView: CompoundButton?, isChecked: Boolean) {
                    if (!userTriggered) return
                    listener.onCheckedChanged(buttonView, isChecked)
                }
            }
        )
    }
}
