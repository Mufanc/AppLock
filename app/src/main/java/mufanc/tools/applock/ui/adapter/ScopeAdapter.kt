package mufanc.tools.applock.ui.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.*
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import mufanc.tools.applock.R
import mufanc.tools.applock.databinding.ViewAppsItemBinding
import mufanc.tools.applock.ui.viewmodel.ScopeModel
import java.text.Collator
import java.util.*

class ScopeAdapter(
    private val appList: MutableSet<ScopeModel.AppInfo>,
    val lockedApps: MutableSet<String>
) : RecyclerView.Adapter<ScopeAdapter.ViewHolder>(), Filterable {

    private val filteredList = mutableListOf<ScopeModel.AppInfo>()

    init {
        filter.filter("")
    }

    class ViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val appIcon: ImageView
        val appName: TextView
        val packageName: TextView
        val locked: CheckBox

        init {
            ViewAppsItemBinding.bind(view).let {
                appIcon = it.appIcon
                appName = it.appName
                packageName = it.packageName
                locked = it.locked
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        return LayoutInflater
            .from(parent.context)
            .inflate(R.layout.view_apps_item, parent, false)
            .let { ViewHolder(it) }
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val appInfo = filteredList[position]
        holder.apply {
            appIcon.setImageDrawable(appInfo.icon)
            appName.text = appInfo.appName
            packageName.text = appInfo.packageName
            locked.isChecked = lockedApps.contains(appInfo.packageName)
            locked.setOnCheckedChangeListener { view, checked ->
                if (!view.isPressed) return@setOnCheckedChangeListener
                if (checked) {
                    lockedApps.add(appInfo.packageName)
                } else {
                    lockedApps.remove(appInfo.packageName)
                }
            }
        }
    }

    override fun getItemCount() = filteredList.size

    override fun getFilter(): Filter {
        return object : Filter() {
            override fun performFiltering(query: CharSequence): FilterResults {
                return FilterResults().apply {
                    values = appList.filter { info ->
                        query.toString().let {
                            info.appName.lowercase().contains(it) || info.packageName.contains(it)
                        }
                    }.sortedWith { o1, o2 ->
                        val c1 = lockedApps.contains(o1.packageName)
                        val c2 = lockedApps.contains(o2.packageName)
                        if (c1 != c2) return@sortedWith if (c1) -1 else 1
                        Collator.getInstance(Locale.getDefault()).compare(o1.appName, o2.appName)
                    }
                }
            }

            @Suppress("Unchecked_Cast")
            override fun publishResults(query: CharSequence?, results: FilterResults) {
                val newList = results.values as List<ScopeModel.AppInfo>

                DiffUtil.calculateDiff(object : DiffUtil.Callback() {
                    override fun getOldListSize() = filteredList.size

                    override fun getNewListSize() = newList.size

                    override fun areItemsTheSame(op: Int, np: Int) =
                        filteredList[op].packageName == newList[np].packageName

                    override fun areContentsTheSame(op: Int, np: Int) =
                        filteredList[op].packageName == newList[np].packageName
                }).let {
                    filteredList.clear()
                    filteredList.addAll(newList)
                    it.dispatchUpdatesTo(this@ScopeAdapter)
                }
            }
        }
    }
}