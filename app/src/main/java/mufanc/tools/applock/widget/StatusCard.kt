package mufanc.tools.applock.widget

import android.content.Context
import android.util.AttributeSet
import android.view.LayoutInflater
import androidx.appcompat.content.res.AppCompatResources
import com.google.android.material.card.MaterialCardView
import mufanc.tools.applock.R
import mufanc.tools.applock.databinding.ViewStatusCardBinding

class StatusCard(
    context: Context,
    attributeSet: AttributeSet? = null
) : MaterialCardView(context, attributeSet) {

    private val binding = ViewStatusCardBinding
        .inflate(LayoutInflater.from(context), this, true)

    var title: CharSequence?
        get() = binding.title.text
        set(value) {
            binding.title.text = value
        }

    var subtitle: CharSequence?
        get() = binding.subtitle.text
        set(value) {
            binding.subtitle.text = value
        }

    var status: Boolean = false
        set(value) {
            field = value
            binding.icon.setImageDrawable(if (value) {
                AppCompatResources.getDrawable(context, R.drawable.ic_result_success)
            } else {
                AppCompatResources.getDrawable(context, R.drawable.ic_result_failed)
            }!!)
        }

    init {
        context.obtainStyledAttributes(attributeSet, R.styleable.StatusCard).apply {
            try {
                title = getString(R.styleable.StatusCard_title)
                subtitle = getString(R.styleable.StatusCard_subtitle)
                status = getBoolean(R.styleable.StatusCard_status, false)
            } finally {
                recycle()
            }
        }
    }
}