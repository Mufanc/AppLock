package mufanc.tools.applock.ui.adapter

import android.content.pm.ApplicationInfo
import android.content.pm.PackageManager
import android.view.LayoutInflater
import android.view.ViewGroup
import android.widget.*
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import mufanc.tools.applock.databinding.ItemAppSelectBinding
import java.text.Collator
import java.util.*

class ScopeAdapter(
    private val packageManager: PackageManager,
    val scope: MutableSet<String>
) : RecyclerView.Adapter<ScopeAdapter.ViewHolder>(), Filterable {

    data class AppInfo(
        val appName: String,
        val packageName: String,
        val applicationInfo: ApplicationInfo
    ) {
        override fun hashCode(): Int = packageName.hashCode()
        override fun equals(other: Any?): Boolean {
            if (this === other) return true
            if (javaClass != other?.javaClass) return false
            return packageName == (other as AppInfo).packageName
        }
    }

    private val appList: List<AppInfo>
    private val showList = mutableListOf<AppInfo>()

    init {
        // Todo: 完善 SettingsManager 后从这里获取不同列表
        appList = packageManager
            .getInstalledApplications(PackageManager.MATCH_DISABLED_COMPONENTS)
            .map {
                AppInfo(it.loadLabel(packageManager).toString(), it.packageName, it)
            }
        filter.filter("")
    }

    class ViewHolder(binding: ItemAppSelectBinding) : RecyclerView.ViewHolder(binding.root) {
        val appIcon: ImageView
        val appName: TextView
        val packageName: TextView
        val checkbox: CheckBox

        init {
            appIcon = binding.appIcon
            appName = binding.appName
            packageName = binding.packageName
            checkbox = binding.checkbox
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        return ViewHolder(ItemAppSelectBinding.inflate(
            LayoutInflater.from(parent.context), parent, false
        ))
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val info = showList[position]
        with (holder) {
            appIcon.setImageDrawable(info.applicationInfo.loadIcon(packageManager))
            appName.text = info.appName
            packageName.text = info.packageName
            checkbox.isChecked = scope.contains(info.packageName)
            checkbox.setOnCheckedChangeListener { view, checked ->
                if (view.isPressed) {
                    if (checked) {
                        scope.add(info.packageName)
                    } else {
                        scope.remove(info.packageName)
                    }
                }
            }
        }
    }

    override fun getItemCount() = showList.size

    override fun getFilter(): Filter {
        return object : Filter() {
            override fun performFiltering(query: CharSequence): FilterResults {
                return FilterResults().apply {
                    values = appList.filter { info ->
                        query.toString().let {
                            info.appName.lowercase().contains(it) || info.packageName.contains(it)
                        }
                    }.sortedWith { o1, o2 ->
                        val c1 = scope.contains(o1.packageName)
                        val c2 = scope.contains(o2.packageName)
                        if (c1 != c2) return@sortedWith if (c1) -1 else 1
                        Collator.getInstance(Locale.getDefault()).compare(o1.appName, o2.appName)
                    }
                }
            }

            @Suppress("Unchecked_Cast")
            override fun publishResults(query: CharSequence?, results: FilterResults) {
                val newList = results.values as List<AppInfo>
                DiffUtil.calculateDiff(object : DiffUtil.Callback() {
                    override fun getOldListSize() = showList.size
                    override fun getNewListSize() = newList.size
                    override fun areContentsTheSame(op: Int, np: Int) = areItemsTheSame(op, np)
                    override fun areItemsTheSame(op: Int, np: Int) =
                        showList[op].packageName == newList[np].packageName
                }).let {
                    showList.clear()
                    showList.addAll(newList)
                    it.dispatchUpdatesTo(this@ScopeAdapter)
                }
            }
        }
    }
}