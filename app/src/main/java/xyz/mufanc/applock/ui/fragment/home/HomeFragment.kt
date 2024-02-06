package xyz.mufanc.applock.ui.fragment.home

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import xyz.mufanc.applock.databinding.FragmentHomeBinding
import xyz.mufanc.applock.ui.base.BaseFragment

class HomeFragment : BaseFragment<FragmentHomeBinding, HomeViewModel>() {
    
    companion object {
        private const val TAG = "HomeFragment"
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        cache: Bundle?
    ): View? {
        return super.onCreateView(inflater, container, cache).also {
            binding.props = model
            binding.lifecycleOwner = this
        }
    }
}
