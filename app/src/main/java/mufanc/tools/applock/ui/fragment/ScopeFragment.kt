package mufanc.tools.applock.ui.fragment

import android.os.Bundle
import android.view.*
import android.widget.Toast
import androidx.appcompat.widget.SearchView
import androidx.core.view.MenuProvider
import androidx.lifecycle.Lifecycle
import androidx.recyclerview.widget.LinearLayoutManager
import mufanc.tools.applock.R
import mufanc.tools.applock.databinding.FragmentScopeBinding
import mufanc.tools.applock.ui.adapter.ScopeAdapter
import mufanc.tools.applock.util.ScopeManager
import mufanc.tools.applock.util.update

class ScopeFragment : BaseFragment<FragmentScopeBinding>() {

    private val localScope = mutableSetOf<String>()

    private val adapter by lazy {
        ScopeAdapter(requireActivity(), localScope) {
            with (binding) {
                progress.visibility = View.INVISIBLE
                appList.scrollToPosition(0)
            }
        }
    }

    override fun onCreate(bundle: Bundle?) {
        super.onCreate(bundle)

        requireActivity().addMenuProvider(
            object : MenuProvider {
                private var lastQueryText: String = ""

                override fun onCreateMenu(menu: Menu, inflater: MenuInflater) {
                    inflater.inflate(R.menu.fragment_apps_menu, menu)

                    menu.findItem(R.id.search_app).actionView
                        .let { it as SearchView }
                        .apply {
                            setOnQueryTextListener(
                                object : SearchView.OnQueryTextListener {
                                    override fun onQueryTextChange(query: String): Boolean {
                                        lastQueryText = query
                                        adapter.apply { filter.filter(query.lowercase()) }
                                        return true
                                    }

                                    override fun onQueryTextSubmit(query: String?) = false
                                }
                            )
                            setOnCloseListener {
                                adapter.apply { filter.filter("") }
                                false
                            }
                        }
                }

                override fun onMenuItemSelected(menuItem: MenuItem): Boolean {
                    when (menuItem.itemId) {
                        R.id.save_scope -> {
                            binding.appList.scrollToPosition(0)
                            adapter.apply {
                                ScopeManager.scope.update(localScope)
                                ScopeManager.commit()
                                filter.filter(lastQueryText)
                                Toast.makeText(requireContext(), R.string.scope_saved, Toast.LENGTH_SHORT).show()
                            }
                        }
                    }
                    return true
                }
            }, this, Lifecycle.State.RESUMED
        )

        refreshLocalScope()

        with (binding) {
            appList.layoutManager = LinearLayoutManager(
                context, LinearLayoutManager.VERTICAL, false
            )
            appList.adapter = adapter
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?,
    ): View {
        return binding.root
    }

    override fun onResume() {
        super.onResume()
        refreshLocalScope()
    }

    private fun refreshLocalScope() {
        localScope.update(ScopeManager.scope)
    }
}
