package xyz.mufanc.applock.ui.widgets

import android.content.Context
import android.util.AttributeSet
import android.view.LayoutInflater
import com.bumptech.glide.Glide
import com.google.android.material.card.MaterialCardView
import xyz.mufanc.applock.R
import xyz.mufanc.applock.bean.AppInfo
import xyz.mufanc.applock.databinding.ComponentAppInfoCardBinding

class AppInfoCard(
    context: Context,
    attrs: AttributeSet? = null
) : MaterialCardView(context, attrs) {

    private val binding = ComponentAppInfoCardBinding.inflate(LayoutInflater.from(context), this, true)

    var info: AppInfo? = null
        set(value) {
            field = value
            if (value == null) return

            binding.run {
                Glide.with(context)
                    .load(value)
                    .placeholder(R.drawable.ic_app_icon_placeholder)
                    .into(appIcon)

                appName.text = value.label
                packageName.text = value.packageName
            }
        }

    val checkbox get() = binding.checkbox
}
