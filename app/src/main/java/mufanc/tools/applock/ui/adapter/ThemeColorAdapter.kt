package mufanc.tools.applock.ui.adapter

import android.annotation.SuppressLint
import android.app.Activity
import android.app.Dialog
import android.os.Build
import android.view.LayoutInflater
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.Toast
import androidx.annotation.DrawableRes
import androidx.annotation.StyleRes
import androidx.recyclerview.widget.RecyclerView
import mufanc.tools.applock.R
import mufanc.tools.applock.databinding.ItemThemeColorBinding
import mufanc.tools.applock.util.Settings

class ThemeColorAdapter(
    private val activity: Activity,
    private val dialog: Dialog
) : RecyclerView.Adapter<ThemeColorAdapter.ViewHolder>() {

    enum class ThemeColor(@DrawableRes val iconId: Int) {
        PYRO(R.drawable.ic_element_pyro),        // 火
        HYDRO(R.drawable.ic_element_hydro),      // 水
        ANEMO(R.drawable.ic_element_anemo),      // 风
        ELECTRO(R.drawable.ic_element_electro),  // 雷
        DENDRO(R.drawable.ic_element_dendro),    // 草
        CRYO(R.drawable.ic_element_cryo),        // 冰
        GEO(R.drawable.ic_element_geo),          // 岩
        DYNAMIC(R.drawable.ic_element_dynamic)
    }

    class ViewHolder(binding: ItemThemeColorBinding) : RecyclerView.ViewHolder(binding.root) {
        val icon: ImageView = binding.elementIcon
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        return ViewHolder(ItemThemeColorBinding.inflate(
            LayoutInflater.from(parent.context), parent, false
        ))
    }

    @SuppressLint("ApplySharedPref")
    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val color = ThemeColor.values()[position]
        holder.icon.setImageResource(color.iconId)
        holder.itemView.setOnClickListener {
            if (color == ThemeColor.DYNAMIC && Build.VERSION.SDK_INT < Build.VERSION_CODES.S) {
                Toast.makeText(holder.itemView.context, R.string.require_api_31, Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }
            Settings.prefs.edit().apply {
                putString(ThemeColor::class.java.simpleName, color.name)
            }.commit()
            dialog.dismiss()
            activity.recreate()
        }
    }

    override fun getItemCount(): Int = ThemeColor.values().size

    companion object {
        @StyleRes
        fun getColorThemeStyle(): Int {
            val value = Settings.prefs
                .getString(ThemeColor::class.java.simpleName, "")!!
                .ifEmpty { ThemeColor.PYRO.name }
            return when (ThemeColor.valueOf(value)) {
                ThemeColor.PYRO -> R.style.Theme_AppLock_GenshinElement_Pyro
                ThemeColor.HYDRO -> R.style.Theme_AppLock_GenshinElement_Hydro
                ThemeColor.ANEMO -> R.style.Theme_AppLock_GenshinElement_Anemo
                ThemeColor.ELECTRO -> R.style.Theme_AppLock_GenshinElement_Electro
                ThemeColor.DENDRO -> R.style.Theme_AppLock_GenshinElement_Dendro
                ThemeColor.CRYO -> R.style.Theme_AppLock_GenshinElement_Cyro
                ThemeColor.GEO -> R.style.Theme_AppLock_GenshinElement_Geo
                ThemeColor.DYNAMIC -> R.style.Theme_AppLock_GenshinElement_Dynamic
            }
        }
    }
}
