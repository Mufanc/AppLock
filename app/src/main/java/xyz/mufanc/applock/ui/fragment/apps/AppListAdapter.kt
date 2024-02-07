package xyz.mufanc.applock.ui.fragment.apps

import android.util.ArraySet
import android.view.LayoutInflater
import android.view.ViewGroup
import android.widget.Filter
import android.widget.Filterable
import androidx.lifecycle.LifecycleOwner
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import xyz.mufanc.applock.bean.AppInfo
import xyz.mufanc.applock.core.util.Log
import xyz.mufanc.applock.databinding.ItemFragmentAppsAppListBinding

class AppListAdapter(
    private val props: AppsViewModel,
    lifecycleOwner: LifecycleOwner
) : RecyclerView.Adapter<AppListAdapter.ViewHolder>(), Filterable {

    companion object {
        private const val TAG = "AppListAdapter"
    }

    class ViewHolder(binding: ItemFragmentAppsAppListBinding) : RecyclerView.ViewHolder(binding.root) {
        val card = binding.appInfo
        val checkbox = card.checkbox
    }

    private val prefs get() = props.scope.value!!
    private val scope = ArraySet<String>()

    private val apps = ArrayList<AppInfo>()
    private val filteredApps = ArrayList<AppInfo>()

    private val filter = object : Filter() {
        override fun performFiltering(constraint: CharSequence?): FilterResults {
            val query = constraint.toString().lowercase()

            return FilterResults().apply {
                values = apps
                    .filter { info ->
                        info.label.lowercase().contains(query) ||
                            info.packageName.lowercase().contains(query)
                    }
                    .sortedWith(
                        compareBy<AppInfo> { if (scope.contains(it.packageName)) 0 else 1 }
                            .then(compareBy { it.label })
                    )
            }
        }

        @Suppress("Unchecked_Cast")
        override fun publishResults(query: CharSequence?, results: FilterResults) {
            val filterResult = results.values as List<AppInfo>
            val diff = DiffUtil.calculateDiff(object : DiffUtil.Callback() {
                override fun getOldListSize() = filteredApps.size
                override fun getNewListSize() = filterResult.size
                override fun areContentsTheSame(i: Int, j: Int) = areItemsTheSame(i, j)
                override fun areItemsTheSame(i: Int, j: Int) =
                    filteredApps[i].packageName == filterResult[j].packageName
            })

            filteredApps.replace(filterResult)

            diff.dispatchUpdatesTo(this@AppListAdapter)
            props.loading.value = false
        }
    }

    private fun <T> MutableCollection<T>.replace(new: Collection<T>) {
        clear()
        addAll(new)
    }

    override fun getItemCount(): Int = filteredApps.size

    override fun onCreateViewHolder(parent: ViewGroup, type: Int): ViewHolder {
        return ViewHolder(
            ItemFragmentAppsAppListBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        )
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val info = filteredApps[position]

        holder.card.info = info
        holder.checkbox.isChecked = scope.contains(info.packageName)
        holder.checkbox.setOnCheckedChangeListener { _, checked ->
            if (!holder.checkbox.userTriggered) return@setOnCheckedChangeListener

            val current = filteredApps[holder.adapterPosition]
            val pkg = current.packageName
            val editor = prefs.edit()

            if (checked) {
                scope.add(pkg)
                editor.putBoolean(pkg, true)
            } else {
                scope.remove(pkg)
                editor.remove(pkg)
            }

            editor.apply()

            Log.d(TAG, "update for $pkg: $checked")
        }
    }

    override fun getFilter(): Filter {
        return filter
    }

    init {
        props.scope.observe(lifecycleOwner) { prefs ->
            scope.replace(prefs?.all?.keys ?: emptySet())
        }

        props.apps.observe(lifecycleOwner) { list ->
            apps.replace(list)
            filter.filter(props.query.value)
        }

        props.query.observe(lifecycleOwner) { query ->
            filter.filter(query)
        }

        Log.d(TAG, "initialize")
    }
}
