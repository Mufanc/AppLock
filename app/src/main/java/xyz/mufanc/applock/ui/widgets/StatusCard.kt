package xyz.mufanc.applock.ui.widgets

import android.content.Context
import android.util.AttributeSet
import android.view.LayoutInflater
import androidx.appcompat.content.res.AppCompatResources
import com.google.android.material.card.MaterialCardView
import xyz.mufanc.applock.R
import xyz.mufanc.applock.databinding.ComponentStatusCardBinding

class StatusCard(
    context: Context,
    attrs: AttributeSet? = null
) : MaterialCardView(context, attrs) {

    companion object {
        private const val TAG = "StatusCard"
    }

    private val binding = ComponentStatusCardBinding.inflate(LayoutInflater.from(context), this, true)

    var ok: Boolean = false
        set(value) {
            field = value
            binding.statusIcon.setImageDrawable(
                AppCompatResources.getDrawable(
                    context,
                    if (value) R.drawable.ic_status_card_ok else R.drawable.ic_status_card_error
                )
            )
        }

    var title: CharSequence? = null
        set(value) {
            field = value
            binding.title.text = value
        }

    var description: CharSequence? = null
        set(value) {
            field = value
            binding.description.text = value
        }
}
