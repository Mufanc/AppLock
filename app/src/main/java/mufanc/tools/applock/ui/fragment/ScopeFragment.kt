package mufanc.tools.applock.ui.fragment

import android.os.Bundle
import android.view.*
import androidx.appcompat.widget.SearchView
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import mufanc.tools.applock.R
import mufanc.tools.applock.databinding.FragmentScopeBinding
import mufanc.tools.applock.ui.adapter.ScopeAdapter
import mufanc.tools.applock.ui.fragment.base.BaseFragment
import mufanc.tools.applock.ui.viewmodel.ScopeModel
import mufanc.tools.applock.util.Globals

class ScopeFragment : BaseFragment<FragmentScopeBinding>() {

    private var adapter: ScopeAdapter? = null

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?,
    ): View {
        setHasOptionsMenu(true)

        val model = ViewModelProvider(this)[ScopeModel::class.java]
        model.loadAppList(requireActivity()) {
            with (binding) {
                progress.visibility = View.GONE
                applist.apply {
                    layoutManager = LinearLayoutManager(
                        context, LinearLayoutManager.VERTICAL, false
                    )
                    adapter = ScopeAdapter(
                        requireContext().packageManager,
                        model.lockedAppList
                    ).also { this@ScopeFragment.adapter = it }
                }
            }
        }

        with (binding) {
            refresh.setOnRefreshListener {
                model.loadAppList(requireActivity(), true) {
                    adapter?.filter?.filter("")
                    refresh.isRefreshing = false
                }
            }

            return root
        }
    }

    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        inflater.inflate(R.menu.fragment_apps_menu, menu)

        menu.findItem(R.id.search_app).actionView
            .let { it as SearchView }
            .apply {
                setOnQueryTextListener(
                    object : SearchView.OnQueryTextListener {
                        override fun onQueryTextChange(query: String): Boolean {
                            adapter?.apply { filter.filter(query.lowercase()) }
                            return true
                        }

                        override fun onQueryTextSubmit(query: String?) = false
                    }
                )
                setOnCloseListener {
                    adapter?.apply { filter.filter("") }
                    false
                }
            }

        menu.findItem(R.id.save_app_list)
            .setOnMenuItemClickListener {
                adapter?.apply {
                    Globals.LOCKED_APPS = scope
                    filter.filter("")
                }
                true
            }
    }
}