package xyz.mufanc.applock.ui.widgets

import android.content.Context
import android.content.pm.ApplicationInfo
import android.content.pm.PackageManager
import android.util.AttributeSet
import android.view.LayoutInflater
import com.bumptech.glide.Glide
import com.google.android.material.card.MaterialCardView
import xyz.mufanc.applock.App
import xyz.mufanc.applock.R
import xyz.mufanc.applock.databinding.ComponentAppInfoCardBinding

class AppInfoCard(
    context: Context,
    attrs: AttributeSet? = null
) : MaterialCardView(context, attrs) {

    companion object {
        private val pm: PackageManager by lazy { App.instance.packageManager }
    }

    private val binding = ComponentAppInfoCardBinding.inflate(LayoutInflater.from(context), this, true)

    var info: ApplicationInfo? = null
        set(value) {
            field = value
            if (value == null) return

            binding.run {
                Glide.with(context)
                    .load(value)
                    .placeholder(R.drawable.ic_app_icon_placeholder)
                    .into(appIcon)

                appName.text = pm.getApplicationLabel(value)
                packageName.text = value.packageName
            }
        }

    var inScope: Boolean = false
        set(value) {
            field = value
            binding.checkbox.isChecked = value
        }
}
