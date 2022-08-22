package mufanc.tools.applock.ui.fragment

import android.os.Bundle
import android.view.*
import androidx.appcompat.widget.SearchView
import androidx.core.view.MenuProvider
import androidx.lifecycle.Lifecycle
import androidx.recyclerview.widget.LinearLayoutManager
import mufanc.tools.applock.R
import mufanc.tools.applock.databinding.FragmentScopeBinding
import mufanc.tools.applock.ui.adapter.ScopeAdapter
import mufanc.tools.applock.util.ScopeManager

class ScopeFragment : BaseFragment<FragmentScopeBinding>() {

    private val adapter by lazy {
        ScopeAdapter(requireActivity(), ScopeManager.scope) {
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
                override fun onCreateMenu(menu: Menu, inflater: MenuInflater) {
                    inflater.inflate(R.menu.fragment_apps_menu, menu)

                    menu.findItem(R.id.search_app).actionView
                        .let { it as SearchView }
                        .apply {
                            setOnQueryTextListener(
                                object : SearchView.OnQueryTextListener {
                                    override fun onQueryTextChange(query: String): Boolean {
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
                                ScopeManager.commit()
                                filter.filter("")
                            }
                        }
                    }
                    return true
                }
            }, this, Lifecycle.State.RESUMED
        )

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
}