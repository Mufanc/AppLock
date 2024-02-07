package xyz.mufanc.applock.ui.widgets

import android.content.Context
import android.util.AttributeSet
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
}
