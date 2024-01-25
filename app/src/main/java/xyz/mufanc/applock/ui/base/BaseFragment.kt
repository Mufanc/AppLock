package xyz.mufanc.applock.ui.base

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.annotation.CallSuper
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.viewbinding.ViewBinding
import java.lang.reflect.ParameterizedType

abstract class BaseFragment<B : ViewBinding, M: ViewModel> : Fragment() {

    private val geneticTypes = (javaClass.genericSuperclass as ParameterizedType).actualTypeArguments

    private var _binding: B? = null
    protected val binding get() = _binding!!

    private var _model: M? = null
    protected val model get() = _model!!

    @Suppress("UNCHECKED_CAST")
    override fun onCreate(cache: Bundle?) {
        super.onCreate(cache)
        _model = ViewModelProvider(this)[geneticTypes[1] as Class<M>]
    }

    @CallSuper
    @Suppress("UNCHECKED_CAST")
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        cache: Bundle?
    ): View? {

        _binding = (geneticTypes[0] as Class<*>)
            .getDeclaredMethod("inflate", LayoutInflater::class.java, ViewGroup::class.java, Boolean::class.java)
            .invoke(null, layoutInflater, container, false) as B

        return binding.root
    }

    @CallSuper
    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    override fun onDestroy() {
        super.onDestroy()
        _model = null
    }
}
