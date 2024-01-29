package xyz.mufanc.applock.ui.base

import android.os.Bundle
import android.view.LayoutInflater
import androidx.appcompat.app.AppCompatActivity
import androidx.viewbinding.ViewBinding
import java.lang.reflect.ParameterizedType

abstract class BaseActivity<B : ViewBinding> : AppCompatActivity() {

    private val geneticTypes = (javaClass.genericSuperclass as ParameterizedType).actualTypeArguments

    protected lateinit var binding: B

    @Suppress("UNCHECKED_CAST")
    override fun onCreate(cache: Bundle?) {
        super.onCreate(cache)
        binding = (geneticTypes[0] as Class<*>)
            .getDeclaredMethod("inflate", LayoutInflater::class.java)
            .invoke(null, layoutInflater) as B
    }
}
