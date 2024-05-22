package xyz.mufanc.applock.ui.widgets

import android.content.Context
import android.util.AttributeSet
import android.view.LayoutInflater
import android.view.MotionEvent
import android.widget.CompoundButton
import android.widget.FrameLayout
import xyz.mufanc.applock.databinding.ComponentMuitiChoiceItemBinding

class MultiChoiceItem(
    context:Context,
    attrs:AttributeSet? = null
) : FrameLayout(context, attrs) {

    private val binding = ComponentMuitiChoiceItemBinding.inflate(LayoutInflater.from(context), this, true)
    private var onCheckedChangeListener: CompoundButton.OnCheckedChangeListener? = null

    override fun dispatchTouchEvent(ev: MotionEvent?): Boolean {
        if (!isEnabled) return false
        return super.dispatchTouchEvent(ev)
    }

    init {
        binding.root.setOnClickListener {
            if (isEnabled) {
                isChecked = !isChecked
                onCheckedChangeListener?.onCheckedChanged(binding.checkbox, isChecked)
            }
        }
    }

    var isChecked: Boolean
        get() = binding.checkbox.isChecked
        set(value) {
            binding.checkbox.isChecked = value
        }

    fun setOnCheckedChangeListener(listener: CompoundButton.OnCheckedChangeListener) {
        onCheckedChangeListener = listener
        binding.checkbox.setOnCheckedChangeListener(listener)
    }
}
