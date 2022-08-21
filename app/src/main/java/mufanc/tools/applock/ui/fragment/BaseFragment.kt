package mufanc.tools.applock.ui.fragment

import android.view.LayoutInflater
import androidx.fragment.app.Fragment
import androidx.viewbinding.ViewBinding
import java.lang.reflect.ParameterizedType

open class BaseFragment<T : ViewBinding> : Fragment() {
    @Suppress("Unchecked_Cast")
    protected val binding by lazy {
        (javaClass.genericSuperclass as ParameterizedType).actualTypeArguments
            .let { it[0] as Class<T> }
            .getDeclaredMethod("inflate", LayoutInflater::class.java)
            .invoke(null, layoutInflater) as T
    }
}